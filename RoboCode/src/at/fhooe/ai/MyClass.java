package at.fhooe.ai;

import at.fhooe.ai.robocode.seidlsickinger.Model.EnemyBot;
import at.fhooe.ai.robocode.seidlsickinger.Model.Position;

public class MyClass {

    public static void main(String[] args) {
        /*Position p1 = new Position(50, 50);
        Position p2 = new Position(500, 50);
        Position p3 = new Position(500, 500);
        Position p4 = new Position(50, 500);

        System.out.println("A1");
        double angle = AngleCalculator.getAngleBetweenTwoPoints(p1,p2);
        System.out.println("A2");
        double angle2 = AngleCalculator.getAngleBetweenTwoPoints(p2,p3);
        System.out.println("A3");
        double angle3 = AngleCalculator.getAngleBetweenTwoPoints(p3,p4);
        System.out.println("A4");
        double angle4 = AngleCalculator.getAngleBetweenTwoPoints(p4,p1);
        System.out.println("Calc done");


        System.out.println("Angle1 = " +angle);
        System.out.println("Angle2 = " +angle2);
        System.out.println("Angle3 = " +angle3);
        System.out.println("Angle4 = " +angle4);*/
        /*EnemyBot enemyBot = new EnemyBot("Test");
        enemyBot.addPoint(new Position(126,0,0));
        enemyBot.addPoint(new Position(20,0,10));

        Position nextPos;// = enemyBot.nextPosition(100);
        //System.out.println("Next Pos = "+nextPos);

        enemyBot.addPoint(new Position(126,50,96));
        enemyBot.addPoint(new Position(190,50,104));
        nextPos = enemyBot.nextPosition(123);
        System.out.println("Next Pos = "+nextPos);*
         */
        /*String sample = "Hallo.Welt.test";
        String[] parts = sample.split("\\.");
        System.out.println(parts.length);*/
        EnemyBot enemyBot = new EnemyBot("Sample");
        System.out.println("No Positions - "+enemyBot.hasNextPosition());
        enemyBot.addPoint(new Position(0,0));
        System.out.println("One Position - "+enemyBot.hasNextPosition());
        enemyBot.addPoint(new Position(0,0));
        System.out.println("Two Position - "+enemyBot.hasNextPosition());
    }
}
