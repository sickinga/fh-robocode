package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

public class FollowerRobot extends BaseRobot {

    Position target = new Position(200,200);

    @Override
    public void run() {
        target = new Position(getBattleFieldWidth()/2,getBattleFieldHeight()/2);
        setTurnRadarRight(Double.POSITIVE_INFINITY);
        while (true){
            moveToPosition(target.getX(),target.getY());
            doNothing();
        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        if(IsTarget(e.getName())){
            double angleToEnemy = e.getBearing();

            // Calculate the angle to the scanned robot
            double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

            // Calculate the coordinates of the robot
            double enemyX = (getX() + Math.sin(angle) * e.getDistance());
            double enemyY = (getY() + Math.cos(angle) * e.getDistance());
            target = new Position(enemyX,enemyY);
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if(IsTarget(event.getName())){
            System.out.println("Target crashed");
        }
    }

    private boolean IsTarget(String name){
        return name.contains("SquareBot3");
    }
}
