package at.fhooe.ai.robocode.seidlsickinger.Model;

public class Position{
    private double x;
    private double y;
    private long _timeStamp;

    public Position(double x,double y) {
       this(x,y, System.currentTimeMillis());
    }
    public Position(double x, double y, long timeStamp){
        this.x = x;
        this.y = y;
        _timeStamp = timeStamp;
    }

    //GETTER
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public long getTimeStamp(){return _timeStamp; }
    //TO STRING
    @Override
    public String toString() {
        return String.format("(%.1f;%.1f;%d)", x, y,_timeStamp);
    }
}
