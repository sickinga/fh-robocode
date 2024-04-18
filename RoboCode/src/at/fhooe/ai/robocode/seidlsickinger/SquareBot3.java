package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
import robocode.AdvancedRobot;

import java.awt.geom.Point2D;

public class SquareBot3 extends AdvancedRobot {

    Position[] positionList;

    @Override
    public void run() {
        positionList = new Position[]{
                new Position(50,50),
                new Position(getBattleFieldWidth()-50,50),
                new Position(getBattleFieldWidth()-50,getBattleFieldHeight()-50),
                new Position(50,getBattleFieldHeight()-50),
        };
        super.run();
        int i = 0;
        Position target =  positionList[i % positionList.length];
        moveToPosition(target);
        Position last = getPosition();
        while (true) {
            last= getPosition();
            if(positionReached(target)){
                i++;
                System.out.println("Position reached "+target);
                target = positionList[i % positionList.length];
                System.out.println("Next target "+target);
                moveToPosition(target);

            }
            double distance = getDistance(last);
            if(distance < 0.01){
                moveToPosition(target);
            }
            System.out.println("Moving to "+target);
            doNothing();
        }

    }

    private boolean positionReached(Position p){
        double diffX = Math.abs(getX() - p.getX());
        double diffY = Math.abs(getY() - p.getY());
        if(diffX < 0.01 && diffY < 0.01){
            return true;
        }
        return false;
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

    private Position getPosition(){
        return new Position(getX(),getY());
    }
    private double getDistance(Position p){
        Position position = getPosition();
        double distance = Point2D.distance(position.getX(), position.getY(), p.getX(), p.getY());
        return distance;
    }
}
