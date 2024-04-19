package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyWave;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import at.fhooe.ai.robocode.seidlsickinger.Utils.AngleCalculator;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class WaveSurfer  extends AdvancedRobot {

    List<EnemyWave> enemyWaveList = new ArrayList<>();
    HashMap<String,Double> energyList = new HashMap();
    HashMap<String, Long> lastDetected = new HashMap();

    int k = 0;
    int n = 3;

    @Override
    public void run() {
        super.run();
        setTurnRadarRight(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        double energy = event.getEnergy();
        String name = event.getName();
        String[] parts = name.split("\\.");
        name = parts[parts.length-1];
        double absBearing = event.getBearingRadians() + getHeadingRadians();
        Point2D.Double _myLocation = new Point2D.Double(getX(),getY());
        Point2D.Double _myLocationCorr = new Point2D.Double(getX()+getWidth()/2,getY()+getHeight()/2);
        Point2D _enemyLocation = project(_myLocation, absBearing, event.getDistance());
        long time =getTime();
        if(energyList.containsKey(name)){
            double lastEnergy = energyList.get(name);
            long lastTime = lastDetected.get(name);
            long diff = time - lastTime;
            energyList.put(name,energy);
            lastDetected.put(name,time);
            double energyDrop = lastEnergy-energy;
            if(energyDrop < 3.01 && energyDrop > 0.01){
                EnemyWave enemyWave = new EnemyWave();
                enemyWave.fireTime = time-diff;
                enemyWave.bulletVelocity = 20.0 - 3.0 * energyDrop;
                enemyWave.fireLocation = _enemyLocation;
                enemyWave.fireTarget = _myLocationCorr;
                enemyWaveList.add(enemyWave);
            }
        } else {
            energyList.put(name,energy);
            lastDetected.put(name,time);
        }
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
        long time = getTime();
        for(int i=0;i<enemyWaveList.size();i++) {
            EnemyWave enemyWave = enemyWaveList.get(i);
            g.setColor(Color.LIGHT_GRAY);
            g.setBackground(Color.LIGHT_GRAY);
            long diff = time - enemyWave.getFireTime();
            double distance = Point2D.distance(getX(), getY(), enemyWave.fireLocation.getX(), enemyWave.fireLocation.getY()) + enemyWave.bulletVelocity * 2;
            double travelDistance = diff * enemyWave.bulletVelocity;
            double remainingDistance = travelDistance - distance;
            if (remainingDistance > (8 * enemyWave.bulletVelocity)) {
                enemyWaveList.remove(enemyWave);
                continue;
            }
            Point2D firePos = enemyWave.getFireLocation();
            double width = travelDistance * 2.0;
            g.setColor(Color.gray);
            drawCircle(firePos.getX(),firePos.getY(),width,g);

        }

    }
    private void drawCircle(double x, double y, double width,Graphics2D g){
        double offset = width / 2.0;
        x = x - offset;
        y = y - offset;
        g.drawOval((int)x,(int)y,(int)width,(int)width);

    }
    private Position getPosition(){
        return new Position(getX(),getY());
    }
    private Position toPosition(Point2D point2D){
        return new Position(point2D.getX(),point2D.getY());
    }
    private Position getEnemyPosition(double bearing, double distance, double energy){
        double angleToEnemy = bearing;

        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((getHeading() + angleToEnemy % 360));

        // Calculate the coordinates of the robot
        double enemyX = (getX() + Math.sin(angle) * distance);
        double enemyY = (getY() + Math.cos(angle) * distance);
        return new Position(enemyX,enemyY,getTime(),energy);
    }
    public static Point2D.Double project(Point2D.Double sourceLocation,
                                         double angle, double length) {
        return new Point2D.Double(sourceLocation.x + Math.sin(angle) * length,
                sourceLocation.y + Math.cos(angle) * length);
    }
}
