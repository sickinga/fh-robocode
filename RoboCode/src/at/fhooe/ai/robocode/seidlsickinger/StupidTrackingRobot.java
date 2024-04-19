package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Utils.PositionCalculator;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class StupidTrackingRobot extends AdvancedRobot {

    public HashMap<String,Position> lastPosition = new HashMap<>();

    @Override
    public void run() {
        super.run();
        setTurnRadarLeft(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        String name = event.getName();
        String[] parts = name.split("\\.");
        name = parts[parts.length - 1];
        Position curr = new Position(getX(),getY());
        long time = getTime();
        double absBearing = event.getBearingRadians() + getHeadingRadians();
        Point2D.Double _myLocation = new Point2D.Double(getX(),getY());
        Point2D _enemyLocation = project(_myLocation, absBearing, event.getDistance());
        Position position = Position.fromPoint2D(_enemyLocation, time,event.getEnergy());
        lastPosition.put(name,position);
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
        long time = getTime();
        for (Position p : lastPosition.values()) {
            System.out.println("Draw Position: " + p);
            g.setStroke(new BasicStroke(2));
            g.setColor(Color.RED);
            long diff = time-p.getTimeStamp();
            long width = diff * 10;
            drawCircle(p.getX(),p.getY(),width,g);
        }
    }
    private void drawCircle(double x, double y, double width, Graphics2D g){
        double offset = width / 2;
        x = x - offset;
        y = y - offset;
        g.drawOval((int)x,(int)y,(int)width,(int)width);
    }
    public static Point2D.Double project(Point2D.Double sourceLocation,
                                         double angle, double length) {
        return new Point2D.Double(sourceLocation.x + Math.sin(angle) * length,
                sourceLocation.y + Math.cos(angle) * length);
    }
}
