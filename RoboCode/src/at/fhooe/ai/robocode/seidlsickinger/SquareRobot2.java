package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;

import java.util.ArrayList;
import java.util.List;

public class SquareRobot2 extends BaseRobot2 {

    private List<Position> positionList = new ArrayList<>();
    @Override
    public void run() {
        createPositions();
        int posIndex = 0;
        Position target = positionList.get(posIndex);
        setTargetPosition(target);
        while (true){
            Position curr = new Position(getX(),getY());
            if(targetPositionReached()){
                System.out.println("Position reached at " + target);
                posIndex++;
                if(posIndex>=positionList.size()){
                    posIndex = 0;
                }
                target = positionList.get(posIndex);
                System.out.println("New target "+ target);
                setTargetPosition(target);
            }
            moveToTargetPosition();
            System.out.println("Current position is " + curr);
            doNothing();
        }
    }
    private void createPositions(){
        positionList.add(new Position(50,50));
        positionList.add(new Position(getBattleFieldWidth()- 50,50));
        positionList.add(new Position(getBattleFieldWidth()-50,getBattleFieldHeight()-50));
        positionList.add(new Position(50,getBattleFieldHeight()-50));
    }
}
