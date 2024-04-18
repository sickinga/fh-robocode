package at.fhooe.ai.robocode.seidlsickinger;

import at.fhooe.ai.robocode.seidlsickinger.Utils.Counter;
import robocode.AdvancedRobot;

public class FireringBotA extends AdvancedRobot {

    int i =0;
    boolean flag = true;

    public void run() {
        double gun = getGunHeading();
        turnGunRight(-gun);

        while (true){
            i++;
            if(i == 20){
                if(flag) {
                    fire(1);
                }
                i = 0;
                flag = !flag;
            }
            System.out.println(String.format("%d - %b", i, flag));
            doNothing();
        }
    }
}
