package at.fhooe.ai.robocode.seidlsickinger;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.RobotStatus;
import robocode.StatusEvent;

public class WallAvoidRobot extends AdvancedRobot {
    private int _direction = 1;
    private int _speed = 100;

    private RobotStatus _status;

    public void run() {
        while (true){
            doNothing();
            if(willCrash()){
                _direction *= -1;
                System.out.println("Direction changed");
            }
            ahead(_speed*_direction);
        }
    }
    public boolean willCrash(){
        double rad = Math.toRadians(45);
        double speedX = _speed * _direction * Math.cos(rad);
        double speedY = _speed * _direction * Math.sin(rad);
        double nextPosX = _status.getX() + speedX;
        double nextPosY = _status.getY() + speedY;

        if(nextPosX < 0 || nextPosX > getBattleFieldWidth()){
            System.out.println("Will Crash X, Direction = "+_direction);
            return true;
        }
        if(nextPosY < 0 || nextPosY > getBattleFieldHeight()){
            System.out.println("Will Crash Y , Direction = "+_direction);
            return true;
        }
        return false;
    }


    @Override
    public void onStatus(StatusEvent e) {
        _status = e.getStatus();
        System.out.println("Energy = " + _status.getEnergy());
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        _direction = _direction * -1;
    }
}
