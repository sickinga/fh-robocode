package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyWave;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Model.Target;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
import at.fhooe.ai.robocode.seidlsickinger.Utils.BulletUtils;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TrackingRobot extends AdvancedRobot {
    final static double BULLET_POWER = 3;
    private HashMap<String,EnemyBot> botList = new HashMap<>();
    private List<EnemyWave> enemyWaveList = new ArrayList<>();

    Map<String, Target> targets = new HashMap<>();
    List<Target> sortedTargets = new ArrayList<>();

    @Override
    public void run() {
        setTurnRadarRight(Double.POSITIVE_INFINITY);
        setAdjustRadarForGunTurn(true);

        while (true) {
            // scan battlefield
            setTurnRadarRight(180);

            Target target = null;

            if (targets.size() > 1) {
                sortedTargets = sortTargetsByDistance();
                if (!sortedTargets.isEmpty()) {
                    target = sortedTargets.getFirst();
                }
            } else if (sortedTargets.isEmpty() && !targets.isEmpty()) {
                target = targets.values().stream().findFirst().orElse(null);
            }

            if (target != null) {
                double distanceToNextWall = Math.min(
                        Math.min(getX(), getBattleFieldWidth() - getX()),
                        Math.min(getY(), getBattleFieldHeight() - getY())
                );

                if (distanceToNextWall < 100 && distanceToNextWall < target.distance) {
                    double angleToCenter = Math.atan2(getBattleFieldWidth() / 2 - getX(), getBattleFieldHeight() / 2 - getY());

                    setTurnRightRadians(Utils.normalRelativeAngle(angleToCenter - getHeadingRadians()));
                    setAhead(100);
                } else {
                    double absBearing = Math.atan2(target.position.x - getX(), target.position.y - getY());
                    double turnRadius = absBearing + Math.PI / 2;

                    turnRadius -= Math.max(0.5, (1 / target.distance) * 100);

                    if (target.distance < 50) {
                        double angleToTarget = absBearing - getGunHeadingRadians();

                        setTurnGunRightRadians(Utils.normalRelativeAngle(angleToTarget));

                        setFireBullet(BULLET_POWER);
                        System.out.println("Shot!");
                    }

                    setTurnRightRadians(Utils.normalRelativeAngle(turnRadius - getHeadingRadians()));
                    setMaxVelocity(400 / getTurnRemaining());

                    if (target.distance < 100 && absBearing - getHeadingRadians() > Math.PI / 6) {
                        setAhead(50);
                    } else {
                        setAhead(100);
                    }
                }
            } else {
                System.out.println("Idling");
                setTurnRight(90);
                setAhead(100);
            }
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        long time = getTime();
        String name = getCoreName(event.getName());
        double distance = event.getDistance();
        double absBearing = getHeadingRadians() + event.getBearingRadians();
        double energy = event.getEnergy();
        Point2D currentPosition = new Point2D.Double(getX(), getY());
        Point2D.Double _myLocation = new Point2D.Double(getX(),getY());



        if(!botList.containsKey(name)){
            botList.put(name,new EnemyBot(name));
        }
        EnemyBot bot = botList.get(name);
        Point2D _enemyLocation = project(_myLocation, absBearing, event.getDistance());
        Position position = Position.fromPoint2D(_enemyLocation, time,event.getEnergy());
        //Position position = getEnemyPosition(event.getBearing(), distance, energy);
        bot.addPoint(position);
        if(bot.hasEnergyDroped()){
            EnemyWave enemyWave = new EnemyWave();
            double energyDroped = bot.getEnergyDropped();
            enemyWave.fireLocation = bot.getLast().toPoint2D();
            enemyWave.bulletVelocity = BulletUtils.getVelocity(energyDroped);
            enemyWave.fireTime = time;
            enemyWaveList.add(enemyWave);
        }

        if (event.getEnergy() == 0) {
            targets.remove(name);
            return;
        }

        targets.put(event.getName(), new Target(
                name,
                new Point2D.Double(
                        getX() + Math.sin(absBearing) * distance,
                        getY() + Math.cos(absBearing) * distance
                ),
                event.getHeadingRadians(),
                getTime(),
                event.getEnergy(),
                distance,
                event.getVelocity()
        ));
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        g.setPaint(Color.red);
        drawEnemyPositions(g);
        drawEnemyWaves(g);
        if (!sortedTargets.isEmpty()) {
            Target target = sortedTargets.get(0);
            Point2D.Double positionTarget = target.position;
            double r = (getTime() - target.time) * 3;

            g.setColor(Color.YELLOW);
            g.drawOval(
                    (int) (positionTarget.x - r),
                    (int) (positionTarget.y - r),
                    (int) (2 * r),
                    (int) (2 * r)
            );
        }
    }
    private void drawEnemyPositions(Graphics2D g){
        g.setStroke(new BasicStroke(1));
        Position current = getPosition();
        var bots = botList.entrySet();
        for (Map.Entry<String,EnemyBot> bot : bots){
            EnemyBot b = bot.getValue();
            Position last = b.getLast();
            if(b.hasNextPosition()){
                if(b.isAlive(getTime())) g.setPaint(Color.red);
                g.setStroke(new BasicStroke(2));
                Position next = b.nextPosition(getTime());
                long bulletTime = getBulletTime(1,current,next);
                long angleTime = getTurnAngleTime(current,next);
                next = b.nextPosition(getTime() + bulletTime + angleTime);
                int diff = (int)(getTime() - last.getTimeStamp());
                double width = diff * 5;
                drawCircle(next.getX(),next.getY(),width,g);

                /*g.setColor(Color.YELLOW);
                drawLine(last,next,g);
                fillCircle(last.getX(),last.getY(),5,g);
                fillCircle(next.getX(),next.getY(),5,g);*/
            }
        }
    }
    private void drawEnemyWaves(Graphics2D g){
        g.setStroke(new BasicStroke(1));
        long time = getTime();
        for(int i=0;i<enemyWaveList.size();i++) {
            EnemyWave enemyWave = enemyWaveList.get(i);
            g.setColor(Color.LIGHT_GRAY);
            g.setBackground(Color.LIGHT_GRAY);
            long diff = time - enemyWave.getFireTime();
            double distance = Point2D.distance(getX(), getY(), enemyWave.fireLocation.getX(), enemyWave.fireLocation.getY()) + enemyWave.bulletVelocity * 2;
            double travelDistance = diff * enemyWave.bulletVelocity;
            double remainingDistance = travelDistance - distance;
            if (remainingDistance > (8 * enemyWave.bulletVelocity)) {
                enemyWaveList.remove(enemyWave);
                continue;
            }
            Point2D firePos = enemyWave.getFireLocation();
            double width = travelDistance * 2.0;
            g.setColor(Color.gray);
            drawCircle(firePos.getX(),firePos.getY(),width,g);

        }
    }
    private void drawCircle(double x, double y, double width, Graphics2D g){
        double offset = width / 2;
        x = x - offset;
        y = y - offset;
        g.drawOval((int)x,(int)y,(int)width,(int)width);
    }
    private void fillCircle(double x, double y, double width, Graphics2D g){
        double offset = width / 2;
        x = x - offset;
        y = y - offset;
        g.fillOval((int)x,(int)y,(int)width,(int)width);
    }
    private void drawLine(Position p1,Position p2, Graphics2D g){
        g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
    }

    private Position getEnemyPosition(double bearing, double distance, double energy){
        double angleToEnemy = bearing;

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (getX() + Math.sin(angle) * distance);
        double enemyY = (getY() + Math.cos(angle) * distance);
        return new Position(enemyX,enemyY,getTime(),energy);
    }
    public static Point2D.Double project(Point2D.Double sourceLocation,
                                         double angle, double length) {
        return new Point2D.Double(sourceLocation.x + Math.sin(angle) * length,
                sourceLocation.y + Math.cos(angle) * length);
    }
    private void moveToPosition(Position p){
        moveToPositionAngle(p);
        double distance = getDistance(p);
        ahead(distance);
    }
    private void moveToPositionAngle(Position p){
        var angle = AngleCalculator.getAngleBetweenTwoPoints(getPosition(),p);
        moveToAngle(angle);
    }
    private void moveToAngle(double angle){
        double heading = getHeading();
        double correction = angle - heading;
        if(Math.abs(correction) > 180){
            correction -= 360;
        }
        turnRight(correction);
    }
    private double getDistance(Position p){
        Position position = getPosition();
        double distance = Point2D.distance(position.getX(), position.getY(), p.getX(), p.getY());
        return distance;
    }
    private Position getPosition(){
        return new Position(getX(),getY());
    }
    private String getCoreName(String name){
        String[] parts = name.split("\\.");
        return parts[parts.length-1];
    }
    private long getBulletTime(int firepower, Position curr,Position p){
        double bulletVelocity = 20.0 - 3.0 * firepower;
        double distance = Point2D.distance(curr.getX(), curr.getY(), p.getX(), p.getY());
        return (long) (distance/bulletVelocity);
    }
    private long getTurnAngleTime(Position curr, Position p){
        double correction = getAngleCorrection(curr,p);
        double time = Math.abs(correction) / 20;
        return (long) time;
    }
    private double getAngleGunCorrection(Position curr, Position p){
        double heading = normalizeAngle(getGunHeading());
        double angle = AngleCalculator.getAngleBetweenTwoPoints(curr, p);
        double correction = angle - heading;
        if(Math.abs(correction) > 180){
            correction -= 360;
        }
        return correction;
    }
    private double getAngleCorrection(Position curr, Position p){
        double heading = getHeading();
        double angle = AngleCalculator.getAngleBetweenTwoPoints(curr, p);
        double correction = angle - heading;
        if(Math.abs(correction) > 180){
            correction -= 360;
        }
        return correction;
    }
    private double normalizeAngle(double angle){
        if(angle < 0) angle+= 360;
        if(angle > 360) angle = angle % 360;
        return angle;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        System.out.println("onBulletHit");
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if (event.getBearingRadians() > Math.PI / 2 || event.getBearingRadians() < -Math.PI / 2) {
            back(100);
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        if (event.getBearing() > -90 && event.getBearing() < 90) {
            back(100);
        } else {
            ahead(100);
        }
    }

    private List<Target> sortTargetsByDistance() {
        return new ArrayList<>(targets.values()).stream().filter(
                target -> target.time + 10 > getTime()
        ).sorted((a, b) -> {
            double distanceA = a.position.distance(getX(), getY());
            double distanceB = b.position.distance(getX(), getY());
            return Double.compare(distanceA, distanceB);
        }).collect(Collectors.toList());
    }
}
