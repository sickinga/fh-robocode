package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Model.Target;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
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
    private double _direction = 1;
    private Random random = new Random();
    private long pos =0;

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
                    setAhead(100);
                }
            } else {
                System.out.println("Idling");
                setTurnRight(90);
                setAhead(100);
            }
            execute();
        }
    }
    private EnemyBot getNextTarget(){
        if(botList.isEmpty()) return null;
        EnemyBot target = null;
        double targetDistance = Double.POSITIVE_INFINITY;
        long time = getTime();
        for(Map.Entry<String,EnemyBot> botEntry: botList.entrySet()){
            EnemyBot bot = botEntry.getValue();
            if(bot.isAlive(getTime()) == false) continue;
            if(target == null) target = bot;
            else {
                double distance = getDistance(bot.nextPosition(time));
                if(distance < targetDistance){
                    target = bot;
                    targetDistance = distance;
                }
            }
        }
        return target;
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        String name = getCoreName(event.getName());
        double distance = event.getDistance();
        double absBearing = getHeadingRadians() + event.getBearingRadians();

        if(botList.containsKey(name) == false){
            botList.put(name,new EnemyBot(name));
        }
        EnemyBot bot = botList.get(name);
        Position position = getEnemyPosition(event.getBearing(), distance);
        bot.addPoint(position);

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
        var bots = botList.entrySet();
        g.setStroke(new BasicStroke(2));
        g.setPaint(Color.red);
        Position current = getPosition();
        for (Map.Entry<String,EnemyBot> bot : bots){
            EnemyBot b = bot.getValue();
            Position last = b.getLast();
            if(b.hasNextPosition()){
                if(b.isAlive(getTime())) g.setPaint(Color.red);
                else g.setPaint(Color.gray);
                Position next = b.nextPosition(getTime());
                long bulletTime = getBulletTime(1,current,next);
                long angleTime = getTurnAngleTime(current,next);
                next = b.nextPosition(getTime() + bulletTime + angleTime);
                int diff = (int)(getTime() - last.getTimeStamp());
                g.drawOval((int)next.getX(),(int)next.getY(),diff*10,diff*10);
            }
        }

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

    private Position getEnemyPosition(double bearing, double distance){
        double angleToEnemy = bearing;

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (getX() + Math.sin(angle) * distance);
        double enemyY = (getY() + Math.cos(angle) * distance);
        return new Position(enemyX,enemyY,getTime());
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
        if (event.getBearing() > -90 && event.getBearing() < 90) {
            back(100);
        } else {
            ahead(100);
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
    private void moveCrazy(){
        System.out.println("moveCrazy");
        boolean shouldTurn = random.nextInt(100) > 50;
        if(shouldTurn){
            double angle = random.nextDouble(45);
            System.out.println("Turn "+angle);
            turnRight(angle);
        }
        double distance = random.nextDouble(100);
        distance = Math.max(50,distance);
        distance = distance*_direction;
        System.out.println("Ahead "+distance);
        ahead(distance);
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
