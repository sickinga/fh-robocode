package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;
import robocode.ScannedRobotEvent;

public class AimingRobot extends BaseRobot {

    EnemyBot enemyBot;

    @Override
    public void run() {
        setTurnRadarRight(Double.POSITIVE_INFINITY);
        Position target = new Position(getBattleFieldWidth()/2, getBattleFieldHeight()/2, System.currentTimeMillis());
        Position aimTarget = new Position(getBattleFieldWidth()/2, getBattleFieldHeight()/2,System.currentTimeMillis());
        while (positionReached(target) == false){
            moveToPosition(target.getX(),target.getY());
            doNothing();
        }
        System.out.println("Target position reaeched");
        while (true){
            if(enemyBot == null) continue;
            aimTarget = enemyBot.nextPosition(System.currentTimeMillis());
            if(aimTarget!= null) {
                aimPosition(aimTarget);
                if (positionAimed(aimTarget)) {
                    fire(1);
                }
            }
            doNothing();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if(IsTarget(event.getName())){
            Position position = new Position(event.getBearing(), event.getDistance(),System.currentTimeMillis());
            if(enemyBot == null){
                enemyBot = new EnemyBot(event.getName());
            }
            enemyBot.addPoint(position);
        }
    }

    private boolean IsTarget(String name){
        if(name.contains("SquareRobot")){
            return true;
        }
        return false;
    }
}
