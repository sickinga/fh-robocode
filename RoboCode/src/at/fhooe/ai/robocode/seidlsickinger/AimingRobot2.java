package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
import robocode.ScannedRobotEvent;

import java.awt.*;

public class AimingRobot2 extends BaseRobot2 {
    private EnemyBot enemyBot = new EnemyBot("SquareRobot2");
    private Position enemyPos;

    @Override
    public void run() {
        setTargetPosition(new Position(getBattleFieldWidth()/2, getBattleFieldHeight()/2,System.currentTimeMillis()));
        System.out.println("Move to center");
        while (targetPositionReached() == false){
            System.out.println("Move to center...");
            moveToTargetPosition();
            doNothing();
        }
        System.out.println("TargetPosition reached");
        setTurnRadarRight(Double.POSITIVE_INFINITY);
        System.out.println("Start scanning");
        long lastTargetUpdate = 0;
        while (true){
            if(enemyBot.hasNextPosition() == false) continue;
            Position current = getPosition();
            long diff =getTime() - lastTargetUpdate;
            if((diff)>5) {
                enemyPos = enemyBot.nextPosition(getTime());
                double distance = getDistance(current, enemyPos);
                long bulletTime = getBulletTime(1, distance);
                long gunTurnTime = gunTimeTurn(enemyPos);
                enemyPos = enemyBot.nextPosition(getTime() + bulletTime + gunTurnTime);
                setTargetGunPosition(enemyPos);
                lastTargetUpdate = getTime();
            }
            if(targetGunHeadingReached()){
                setFire(1);
                setFire(1);
            } else {
                moveToGunTargetHeading();
            }
            doNothing();
        }
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        if(enemyPos == null) return;
        g.setPaint(Color.red);
        g.drawOval((int)Math.round(enemyPos.getX()),(int)Math.round(enemyPos.getY()), 5,5);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if(IsTarget(event.getName())){
            Position enemyPos = getRobotPosition(event.getBearing(),event.getDistance());
            enemyBot.addPoint(enemyPos);
        }
    }

    private boolean IsTarget(String name){
        return name.contains("SquareRobot2");
    }
    private double getBulletSpeed(int firepower){
        return 20.0 - 3.0 * firepower;
    }
    private long getBulletTime(int firepower, double distance){
        double bulletSpeed = getBulletSpeed(firepower);
        return Math.round(distance / bulletSpeed);
    }
    private long gunTimeTurn(Position p){
        double angle = AngleCalculator.getAngleBetweenTwoPoints(getPosition(), p);
        return Math.round(angle / 20);
    }
}
