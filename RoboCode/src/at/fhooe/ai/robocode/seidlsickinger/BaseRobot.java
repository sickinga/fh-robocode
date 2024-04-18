package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import robocode.AdvancedRobot;
import robocode.util.Utils;

import javax.swing.text.Utilities;
import java.awt.geom.Point2D;

public abstract class BaseRobot extends AdvancedRobot {

    protected void moveToAngle(double targetAngle){
        double heading = getHeading();
        double correction = targetAngle - heading;
        turnRight(correction);
    }
    protected void moveGunToAngle(double targetAngle){
        double heading = normalizeAngle(getGunHeading());
        double correction = targetAngle- heading;
        System.out.println(String.format("Correct angle %f to %f (correction = %f)", heading,targetAngle, correction));
        turnGunRight(correction);
    }
    protected void moveToPosition(Position p){
        moveToPosition(p.getX(),p.getY());
    }
    protected void moveToPosition(double x, double y){
        /*if(getX() != x){

            moveToAngle(90);
            double diff =  x- getX();
            setAhead(diff);
            System.out.println("Correct position x, curr = "+ getX() +", diff = "+ diff);
        } else if(getY() != y){

            moveToAngle(0);
            double diff =  y - getY();
            System.out.println("Correct position y, curr = "+ getY() + ", diff = "+ diff);
            setAhead(diff);
        }*/
        double targetAngle = absoluteBearing(getX(),getY(),x,y);
        moveToAngle(targetAngle);
    }
    protected boolean positionReached(Position p){
        double diffX = getX()- p.getX();
        double diffY = getY()- p.getY();
        if(Math.abs(diffX) > 0.0001) return false;
        if(Math.abs(diffY) > 0.0001) return false;
        return true;
    }

    protected Position getPositionOfRobot(double bearing, double distance){
        double angleToEnemy = bearing;

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (getX() + Math.sin(angle) * distance);
        double enemyY = (getY() + Math.cos(angle) * distance);
        return new Position(enemyX,enemyY);
    }
    protected void aimPosition(Position p){
        double targetAngle = absoluteBearing(getX(),getY(),p.getX(),p.getY());
        moveGunToAngle(targetAngle);
    }

    private double normalizeAngle(double angle){
        if(angle < 0) angle+= 360;
        if(angle > 360) angle = angle % 360;
        return angle;
    }
    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }
    boolean positionAimed( Position p){
        double targetAngle = absoluteBearing(getX(),getY(),p.getX(),p.getY());
        double currAngle = normalizeAngle(getHeading());
        double diff = Math.abs(targetAngle - currAngle);
        if(diff < 0.0001) return true;
        return false;
    }
}
