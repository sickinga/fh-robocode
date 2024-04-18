package at.fhooe.ai.robocode.seidlsickinger.Utils;

import at.fhooe.ai.robocode.seidlsickinger.Model.Position;

import java.awt.geom.Point2D;

public class AngleCalculator {
    public static double getAngleBetweenTwoPoints(Position p1, Position p2) {
        double dx = p2.getX()-p1.getX();
        double dy = p2.getY()-p1.getY();
        double dist = Point2D.distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        double asinRad= Math.asin(dx/dist);
        double arcSin = Math.toDegrees(asinRad);
        double bearing = 0;

        if (dx > 0 && dy >= 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (dx <= 0 && dy >= 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (dx >= 0 && dy < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (dx < 0 && dy < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing % 360;
    }
}
