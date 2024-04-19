package at.fhooe.ai.robocode.seidlsickinger.Model;

import java.awt.geom.Point2D;
import java.io.PipedOutputStream;

public class EnemyWave {
    public Point2D fireLocation;
    public Point2D fireTarget;
    public long fireTime;
    public double bulletVelocity;

    public int direction;

    public EnemyWave(){}

    public long getFireTime() {
        return fireTime;
    }
    public Point2D getFireLocation() {
        return fireLocation;
    }
    public double getBulletVelocity() {
        return  bulletVelocity;
    }
}
