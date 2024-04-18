package at.fhooe.ai.robocode.seidlsickinger;

import robocode.AdvancedRobot;
import robocode.RobotStatus;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

public class RadarRobot extends AdvancedRobot {
    private RobotStatus _status;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        while (true) {
            turnRadarRight(45);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        System.out.println("onScannedRobot");
    }
    public void onBulletHit(ScannedRobotEvent event) {
        System.out.println("onBulletHit");
    }
    public void onBulletMissed(ScannedRobotEvent event) {
        System.out.println("onBulletMissed");
    }

    @Override
    public void onStatus(StatusEvent e) {
        _status = e.getStatus();
    }
}
