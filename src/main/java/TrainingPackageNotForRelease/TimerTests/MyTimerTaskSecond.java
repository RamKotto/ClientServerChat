package TrainingPackageNotForRelease.TimerTests;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyTimerTaskSecond {
    public static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public static boolean time = true;

    public static void myTimer() {
        service.schedule(() ->
                time = false
                , 5, TimeUnit.SECONDS);
        service.shutdown();
    }

    public static void main(String[] args){
        int justDigit = 0;
        myTimer();
        while (time) {
            justDigit++;
            try {
                Thread.sleep(1100);
                System.out.println(justDigit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}