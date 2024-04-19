package at.fhooe.ai.robocode.seidlsickinger.Utils;

public class BulletUtils {
    public static double getVelocity(double firepower){
        return 20.0 - 3.0 * firepower;
    }
}
