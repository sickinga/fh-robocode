package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import robocode.*;

import java.util.HashMap;

public class MyFirstRobot extends AdvancedRobot {
    private HashMap<String,ScannedRobot> _robotList = new HashMap<String, ScannedRobot>();

    private RobotStatus robotStatus = null;

    int i =0;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setTurnRadarRight(0);
        setTurnGunRight(0);
        while (true) {
            /*turnRadarRight(15);
            turnGunRight(15);
            ahead(100);
            turnRight(15);
            turnGunRight(15);
            turnRadarRight(15);
            back(100);*/
            turnGunRight(10);
            turnRadarRight(10);
            i++;
            if(i == 10){
                PrintRobots();
                i=0;
            }
        }
    }
    private void PrintRobots(){
        System.out.println("Printing robots...");
        for(int i=0;i<_robotList.size();i++){
            System.out.println(_robotList.get(i));
        }
        System.out.println("Done printing robots...");
    }

    public void onScannedRobot(robocode.ScannedRobotEvent e) {
        double angleToEnemy = e.getBearing();

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((robotStatus.getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (robotStatus.getX() + Math.sin(angle) * e.getDistance());
        double enemyY = (robotStatus.getY() + Math.cos(angle) * e.getDistance());
        System.out.println(String.format("%s at position (%f|%f) detected", e.getName(),enemyX,enemyY));
        if(_robotList.containsKey(e.getName()) == false){
            _robotList.put(e.getName(),new ScannedRobot(e.getName()));
        }
        _robotList.get(e.getName()).AddPosition(new Position(enemyX,enemyY));
    }

    public void onHitByBullet(robocode.HitByBulletEvent e) {
        back(100);
    }

    public void onHitWall(robocode.HitWallEvent e) {
        back(100);
    }

    public void onHitRobot(robocode.HitRobotEvent e) {
        back(100);
    }

    @Override
    public void onStatus(StatusEvent e) {
        super.onStatus(e);
        robotStatus = e.getStatus();
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
    }
}
