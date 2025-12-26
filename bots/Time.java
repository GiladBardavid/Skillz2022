package bots;

public class Time {

    public static long startTime = 0;

    public static void updateStartTime() {
        startTime = System.currentTimeMillis();
    }

    public static long getTimeUsed() {
        return System.currentTimeMillis() - startTime;
    }
}
