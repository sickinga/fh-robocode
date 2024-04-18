package at.fhooe.ai.robocode.seidlsickinger.Model;

import java.awt.geom.Point2D;

public class Target {
    public String name;
    public Point2D.Double position;
    public double heading;
    public long time;
    public double energy;
    public double distance;
    public double velocity;

    public Target(String name, Point2D.Double position, double heading, long time, double energy, double distance, double velocity) {
        this.name = name;
        this.position = position;
        this.heading = heading;
        this.time = time;
        this.energy = energy;
        this.distance = distance;
        this.velocity = velocity;
    }
}
