package com.example.notetask.debug;

public class TestTiming {
    long taskId;
    long startTime;
    long duration;

    public TestTiming(long taskId, long startTime, long duration) {
        this.taskId = taskId;
        this.startTime = startTime/1000;//Seconds
        this.duration = duration;
    }

}
