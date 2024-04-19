package at.fhooe.ai.robocode.seidlsickinger.Model;

import java.awt.geom.Point2D;

public class Position{
    private double x;
    private double y;
    private long _timeStamp;
    private double _energy;

    public Position(double x, double y, long timeStamp){
        this(x,y,timeStamp,0);
    }
    public Position(double x,double y) {
       this(x,y, System.currentTimeMillis(), 0);
    }
    public Position(double x, double y, long timeStamp, double energy){
        this.x = x;
        this.y = y;
        _timeStamp = timeStamp;
        _energy = energy;
    }

    //GETTER
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double get_energy(){
        return _energy;
    }
    public long getTimeStamp(){return _timeStamp; }
    //TO STRING
    @Override
    public String toString() {
        return String.format("(%.1f;%.1f;%d)", x, y,_timeStamp);
    }

    public Point2D toPoint2D(){
        return new Point2D.Double(x, y);
    }
    public static Position fromPoint2D(Point2D point, long time,double _energy){
        return new Position(point.getX(),point.getY(), time,_energy);
    }
}
