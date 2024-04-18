package at.fhooe.ai.robocode.seidlsickinger;

import robocode.AdvancedRobot;
import robocode.BattleRules;
import robocode.StatusEvent;

public class AheadBackRobot extends AdvancedRobot {
    private int direction = -1;
    private int i =0;

    @Override
    public void run() {
        System.out.println("run");
        while (true){

            ahead(direction*10);
            direction*=-1;

            i++;
            if((i%100) == 0){
                fire(1);
            }
        }
    }

}
