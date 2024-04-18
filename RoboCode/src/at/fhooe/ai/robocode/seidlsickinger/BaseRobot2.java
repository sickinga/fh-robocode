package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
import robocode.AdvancedRobot;

import java.awt.geom.Point2D;

public class BaseRobot2 extends AdvancedRobot {
    private Position _targetPosition = null;
    private double _targetHeading = 0;
    private double _targetGunHeading;

    protected void setTargetPosition(Position targetPosition){
        setTargetAngle(targetPosition);
        this._targetPosition = targetPosition;
    }
    protected void setTargetHeading(double targetHeading){
        this._targetHeading = targetHeading % 360;
    }
    protected void setTargetGunHeading(double targetHeading){
        this._targetGunHeading = targetHeading % 360;
    }

    protected boolean targetPositionReached(){
        if(_targetPosition == null) return true;
        double diffX = Math.abs(_targetPosition.getX() - getX());
        double diffY = Math.abs(_targetPosition.getY() - getY());
        if(diffX < 0.01 && diffY < 0.01) return true;
        return false;
    }
    protected boolean targetHeadingReached(){
        double heading = getHeading();
        double diffHeading = Math.abs(heading - _targetHeading);
        if(diffHeading < 0.001) return true;
        return false;
    }
    protected void moveToTargetHeading(){
        if(targetHeadingReached()){
            return;
        }
        double heading = getHeading();
        double correction = _targetHeading - heading;
        if(Math.abs(correction) > 180){
            correction -= 360;
        }
        turnRight(correction);
    }
    protected boolean targetGunHeadingReached(){
        double heading = normalizeAngle(getGunHeading());
        double diffHeading = Math.abs(heading - _targetGunHeading);
        if(diffHeading < 0.001) return true;
        return false;
    }
    private double normalizeAngle(double angle){
        if(angle < 0) angle+= 360;
        if(angle > 360) angle = angle % 360;
        return angle;
    }
    protected void moveToGunTargetHeading(){
        if(targetGunHeadingReached()){
            return;
        }
        double heading = getGunHeading();
        double correction = _targetGunHeading - heading;
        if(Math.abs(correction) > 180){
            correction -= 360;
        }
        //System.out.println(String.format("Move Gun Angle from %f to %f (diff = %f)", heading, _targetGunHeading, correction));
        turnGunRight(correction);
    }
    protected void moveToTargetPosition(){
        if(targetPositionReached()){ return;}
        setTargetAngle(_targetPosition);
        if(targetHeadingReached() == false){
            moveToTargetHeading();
            return;
        }
        double distance = getDistance(getPosition(), _targetPosition);
        ahead(distance);
    }

    double absoluteBearing(double x1, double y1, double x2, double y2) {
        return AngleCalculator.getAngleBetweenTwoPoints(new Position(x1,y1),new Position(x2,y2));
    }
    void setTargetAngle(Position p) {
        double targetHeading = absoluteBearing(getX(),getY(), p.getX(), p.getY());
        setTargetHeading(targetHeading);
    }
    void setTargetGunPosition(Position p) {
        double targetGunAngle = absoluteBearing(getX(),getY(), p.getX(), p.getY());
        setTargetGunHeading(targetGunAngle);
    }

    double getDistance(Position start, Position end){
        return Point2D.distance(start.getX(), start.getY(), end.getX(), end.getY());
    }
    Position getPosition(){
        return new Position(getX(),getY());
    }
    public Position getRobotPosition(double bearing, double distance) {
        double angleToEnemy = bearing;

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (getX() + Math.sin(angle) * distance);
        double enemyY = (getY() + Math.cos(angle) * distance);
        return new Position(enemyX,enemyY, getTime());
    }


}
