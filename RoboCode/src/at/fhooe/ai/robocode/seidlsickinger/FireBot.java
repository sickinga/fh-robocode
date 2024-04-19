package at.fhooe.ai.robocode.seidlsickinger;

import robocode.AdvancedRobot;

public class FireBot extends AdvancedRobot {

    int i = 5;

    @Override
    public void run() {
        super.run();

        while (true){
            System.out.println("Turns until next fire "+i);
            if(i == 0){
                setFire(3);
                i=5;
            }
            i--;
            doNothing();
        }
    }
}
