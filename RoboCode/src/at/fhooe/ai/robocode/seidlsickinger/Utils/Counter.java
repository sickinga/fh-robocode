package at.fhooe.ai.robocode.seidlsickinger.Utils;

import java.util.ArrayList;
import java.util.List;

public class Counter {

    private List<ICounterObserver> counterObserverList = new ArrayList<ICounterObserver>();
    private int count = 0;
    private long lastTime = System.currentTimeMillis();
    private int instanceId;

    private static Counter instance;

    private static int nextId = 0;
    public Counter(){
        instanceId = nextId++;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long diff = System.currentTimeMillis() - lastTime;
                System.out.println(diff);
                if(diff > 10){
                    lastTime = System.currentTimeMillis();
                    count++;
                    notifyObserver();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public static Counter GetInstance(){
        if(instance == null){
            instance = new Counter();
        }
        return instance;
    }

    public void registerObserver(ICounterObserver counterObserver){
        counterObserverList.add(counterObserver);
    }
    public void notifyObserver(){
        for(ICounterObserver counterObserver : counterObserverList){
            counterObserver.CounterUpdated(count);
        }
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public int hashCode() {
        return instanceId;
    }
}
