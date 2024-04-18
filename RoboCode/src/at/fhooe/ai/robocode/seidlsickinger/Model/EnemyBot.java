package at.fhooe.ai.robocode.seidlsickinger.Model;

public class EnemyBot {
    private String _name;

    private Position last;
    private Position last2;

    public EnemyBot(String name) {
        _name = name;
    }
    public void addPoint(Position point) {
        last2 = last;
        last = point;
    }
    public boolean hasNextPosition(){
        if(last == null) return false;
        if(last2==null) return false;
        return true;
    }
    public Position nextPosition(long timestamp){
        if(hasNextPosition() == false){
            return null;
        }
        long timeDiff = last.getTimeStamp() - last2.getTimeStamp();
        double diffX = last.getX() - last2.getX();
        double diffY = last.getY() - last2.getY();
        double speedX = diffX /timeDiff;
        double speedY = diffY /timeDiff;
        long timeGone =  timestamp-last.getTimeStamp();
        Position nextPos = new Position(last.getX() + speedX*timeGone, last.getY() + speedY*timeGone,timestamp);
        return nextPos;
    }
    public Position getLast(){
        return last;
    }

    public boolean isAlive(long timestamp){
        if(last == null) return false;
        double diff = last.getTimeStamp() - timestamp;
        if(diff > 10) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s Last: %s, Last2: %s", _name, last, last2);
    }
}
