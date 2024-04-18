package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.Target;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyFirstRobot extends AdvancedRobot {

    final static double BULLET_POWER = 3;

    Map<String, Target> targets = new HashMap<>();
    List<Target> sortedTargets = new ArrayList<>();

    public void run() {
        setAdjustRadarForGunTurn(true);

        while (true) {
            // scan battlefield
            setTurnRadarRight(180);

            Target target = null;

            if (targets.size() > 1) {
                sortedTargets = sortTargetsByDistance();
                if (!sortedTargets.isEmpty()) {
                    target = sortedTargets.getFirst();
                }
            } else if (sortedTargets.isEmpty() && !targets.isEmpty()) {
                target = targets.values().stream().findFirst().orElse(null);
            }

            if (target != null) {
                double distanceToNextWall = Math.min(
                        Math.min(getX(), getBattleFieldWidth() - getX()),
                        Math.min(getY(), getBattleFieldHeight() - getY())
                );

                if (distanceToNextWall < 100 && distanceToNextWall < target.distance) {
                    double angleToCenter = Math.atan2(getBattleFieldWidth() / 2 - getX(), getBattleFieldHeight() / 2 - getY());

                    setTurnRightRadians(Utils.normalRelativeAngle(angleToCenter - getHeadingRadians()));
                    setAhead(100);
                } else {
                    double absBearing = Math.atan2(target.position.x - getX(), target.position.y - getY());
                    double turnRadius = absBearing + Math.PI / 2;

                    turnRadius -= Math.max(0.5, (1 / target.distance) * 100);

                    if (target.distance < 50) {
                        double angleToTarget = absBearing - getGunHeadingRadians();

                        setTurnGunRightRadians(Utils.normalRelativeAngle(angleToTarget));

                        setFireBullet(BULLET_POWER);
                        System.out.println("Shot!");
                    }

                    setTurnRightRadians(Utils.normalRelativeAngle(turnRadius - getHeadingRadians()));
                    setMaxVelocity(400 / getTurnRemaining());
                    setAhead(100);
                }
            } else {
                System.out.println("Idling");
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
                e.getName(),
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
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } else {
            ahead(100);
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

        if (!sortedTargets.isEmpty()) {
            Target target = sortedTargets.get(0);
            Point2D.Double positionTarget = target.position;
            double r = (getTime() - target.time) * 3;

            g.setColor(Color.YELLOW);
            g.drawOval(
                    (int) (positionTarget.x - r),
                    (int) (positionTarget.y - r),
                    (int) (2 * r),
                    (int) (2 * r)
            );
        }
    }

    private List<Target> sortTargetsByDistance() {
        return new ArrayList<>(targets.values()).stream().filter(
                target -> target.time + 10 > getTime()
        ).sorted((a, b) -> {
            double distanceA = a.position.distance(getX(), getY());
            double distanceB = b.position.distance(getX(), getY());
            return Double.compare(distanceA, distanceB);
        }).collect(Collectors.toList());
    }
}






















