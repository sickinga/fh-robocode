package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;

public class CenterRobot extends  BaseRobot2 {

    @Override
    public void run() {
        super.run();
        setTargetPosition(new Position(getBattleFieldWidth()/2, getBattleFieldHeight()/2));
        while (targetPositionReached() == false){
            moveToTargetPosition();
        }
        System.out.println("Target reached");
    }
}
