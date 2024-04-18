package at.fhooe.ai.robocode.seidlsickinger;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFirstRobot extends AdvancedRobot {

    final static double BULLET_POWER = 3;
    final static double BULLET_DAMAGE = BULLET_POWER * 4;
    final static double BULLET_SPEED = 20 - 3 * BULLET_POWER;

    private static class Target {
        public Point2D.Double position;
        public double heading;
        public long time;
        public double energy;
        public double distance;
        public double velocity;

        public Target(Point2D.Double position, double heading, long time, double energy, double distance, double velocity) {
            this.position = position;
            this.heading = heading;
            this.time = time;
            this.energy = energy;
            this.distance = distance;
            this.velocity = velocity;
        }
    }

    Map<String, Target> targets = new HashMap<>();
    List<Target> sortedTargets = new ArrayList<>();

    public void run() {

        while (true) {
            // scan battlefield
            setTurnRadarRight(180);

            Target target = null;

            if (targets.size() > 1 || sortedTargets.size() == 0) {
                sortedTargets = sortTargetsByDistance();
                if (sortedTargets.size() > 0) {
                    target = sortedTargets.get(0);
                }
            }

            if (target != null) {
                double distanceToNextWall = Math.min(
                        Math.min(getX(), getBattleFieldWidth() - getX()),
                        Math.min(getY(), getBattleFieldHeight() - getY())
                );

                if (distanceToNextWall < 100 && distanceToNextWall < target.distance) {
                    // slow down
                    setMaxVelocity(100);
                } else {
                    double absBearing = Math.atan2(target.position.x - getX(), target.position.y - getY());
                    double turnRadius = absBearing + Math.PI / 2;

                    turnRadius -= Math.max(0.5, (1 / target.distance) * 100);

                    setTurnRightRadians(Utils.normalRelativeAngle(turnRadius - getHeadingRadians()));

                    setMaxVelocity(400 / getTurnRemaining());
                    setAhead(100);
                }
            } else {
                setTurnRight(90);
                setAhead(100);
            }
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = getHeadingRadians() + e.getBearingRadians();
        double distance = e.getDistance();

        if (e.getEnergy() == 0) {
            targets.remove(e.getName());
            return;
        }

        targets.put(e.getName(), new Target(
                new Point2D.Double(
                        getX() + Math.sin(absBearing) * distance,
                        getY() + Math.cos(absBearing) * distance
                ),
                e.getHeadingRadians(),
                getTime(),
                e.getEnergy(),
                distance,
                e.getVelocity()
        ));
    }

    public void onHitByBullet(HitByBulletEvent e) {
        back(100);
    }

    public void onHitWall(HitWallEvent e) {
        if (e.getBearing() > -90 && e.getBearing() < 90) {

            back(100);
        } else {
            ahead(100);
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > 45 || e.getBearing() < -45)
            ahead(100);
        else {
            double gunAngle = e.getBearing() + getHeading() - getGunHeading();
            turnGunRight(gunAngle);
            fireBullet(BULLET_POWER);
        }
    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.drawLine(
                (int) getX(),
                (int) getY(),
                (int) (getX() + Math.sin(getHeadingRadians()) * 100),
                (int) (getY() + Math.cos(getHeadingRadians()) * 100)
        );
        g.setColor(Color.RED);
        g.drawLine(
                (int) getX(),
                (int) getY(),
                (int) (getX() + Math.sin(getGunHeadingRadians()) * 100),
                (int) (getY() + Math.cos(getGunHeadingRadians()) * 100)
        );
        for (Target target : targets.values()) {
            g.setColor(Color.BLUE);
            Point2D.Double position = target.position;
            double heading = target.heading;
            double r = (getTime() - target.time) * 3;

            g.drawOval((int) (position.x - r), (int) (position.y - r), (int) (2 * r), (int) (2 * r));
            g.drawLine(
                    (int) position.x,
                    (int) position.y,
                    (int) (position.x + Math.sin(heading) * r),
                    (int) (position.y + Math.cos(heading) * r)
            );
        }
    }

    private List<Target> sortTargetsByDistance() {
        List<Target> sortedTargets = new ArrayList<>(targets.values());
        sortedTargets.sort((a, b) -> {
            double distanceA = a.position.distance(getX(), getY());
            double distanceB = b.position.distance(getX(), getY());
            return Double.compare(distanceA, distanceB);
        });
        return sortedTargets;
    }
}






















