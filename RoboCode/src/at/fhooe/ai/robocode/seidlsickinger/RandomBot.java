package at.fhooe.ai.robocode.seidlsickinger;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;

import java.util.Random;

public class RandomBot extends AdvancedRobot {

    Random random = new Random();
    double _direction = 1;

    @Override
    public void run() {
        super.run();

        while (true) {
            moveCrazy();
            doNothing();
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        System.out.println("onBulletHit");
        moveCrazy();
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        _direction *=-1;
        double distance = random.nextDouble(100);
        distance = Math.max(50,distance);
        distance = distance*_direction;
        ahead(distance);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        _direction *= -1;
        double distance = random.nextDouble(100);
        distance = Math.max(50,distance);
        distance = distance*_direction;
        ahead(distance);
    }
    private void moveCrazy(){
        System.out.println("moveCrazy");
        boolean shouldTurn = random.nextInt(100) > 50;
        if(shouldTurn){
            double angle = 25+random.nextDouble(20);
            System.out.println("Turn "+angle);
            turnRight(angle);
        }
        boolean shouldChangeDirection= random.nextInt(100) > 50;
        if(shouldChangeDirection){
            _direction = _direction * -1;
            System.out.println("Change Direction : "+_direction);
        }
        double distance = random.nextDouble(100);
        distance = Math.max(50,distance);
        distance = distance*_direction;
        System.out.println("Ahead "+distance);
        ahead(distance);
    }
}
