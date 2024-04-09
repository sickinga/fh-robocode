package at.fhooe.ai.robocode.seidlsickinger;

import robocode.AdvancedRobot;

public class MyFirstRobot extends AdvancedRobot {
        public void run() {
            while (true) {
                ahead(100);
                turnGunRight(360);
                back(100);
                turnGunRight(360);
            }
        }
    
        public void onScannedRobot(robocode.ScannedRobotEvent e) {
            fire(1);
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
}
