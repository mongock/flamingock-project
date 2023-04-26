package io.mongock.core.util;

public class StopWatch {

    private long startedAt = -1L;

    private long duration = -1L;



    public static StopWatch start() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.startCounting();
        return stopWatch;
    }

    private StopWatch(){}
    public void startCounting() {
        startedAt = System.currentTimeMillis();
    }

    public long stop(){
        duration = System.currentTimeMillis()  - startedAt;
        return duration;
    }

    public long getDuration() {
        return duration;
    }
}
