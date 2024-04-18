package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
import robocode.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TrackingRobot extends AdvancedRobot {
    private HashMap<String,EnemyBot> botList = new HashMap<>();
    private double _direction = 1;
    private Random random = new Random();
    private long pos =0;

    @Override
    public void run() {
        super.run();
        setTurnRadarRight(Double.POSITIVE_INFINITY);
        moveToPosition(new Position(getBattleFieldWidth()/2, getBattleFieldHeight()/2));
        /*long last =getTime();
        while (true){
            long diff = getTime() - last;
            if(diff > 2){
                System.out.println(getTime() + "- Fire");
                Position position = getPosition();
                EnemyBot enemyBot = getNextTarget();
                if(enemyBot == null) continue;
                Position nextPos = enemyBot.nextPosition(getTime());
                double angleCorrection = getAngleGunCorrection(position,nextPos);
                long bulletTime = getBulletTime(1,position,nextPos);
                long angleTurnTime = (long) angleCorrection / 20;
                nextPos = enemyBot.nextPosition(getTime() + bulletTime + angleTurnTime);
                angleCorrection = getAngleGunCorrection(position,nextPos);
                turnGunRight(angleCorrection);
                fire(1);
                last = getTime();
            }
            doNothing();
        }*/
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
        if(botList.containsKey(name) == false){
            botList.put(name,new EnemyBot(name));
        }
        EnemyBot bot = botList.get(name);
        Position position = getEnemyPosition(event.getBearing(),event.getDistance());
        bot.addPoint(position);
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
        var bots = botList.entrySet();
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
                g.drawOval((int)next.getX(),(int)next.getY(),diff,diff);
            }
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
        moveCrazy();
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        _direction *=-1;
        double distance = random.nextDouble(100);
        distance = Math.max(50,distance);
        distance = distance*_direction;
        ahead(distance);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        _direction *= -1;
        double distance = random.nextDouble(100);
        distance = Math.max(50,distance);
        distance = distance*_direction;
        ahead(distance);
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
}
