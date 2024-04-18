package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import robocode.AdvancedRobot;
import robocode.RobotStatus;
import robocode.StatusEvent;

import java.util.List;

public class BaseRobot3 extends AdvancedRobot {

    //private List<EnemyBot>

    private RobotStatus robotStatus;

    @Override
    public void onStatus(StatusEvent e) {
        super.onStatus(e);
        robotStatus = e.getStatus();
    }

    @Override
    public void run() {

    }
}
