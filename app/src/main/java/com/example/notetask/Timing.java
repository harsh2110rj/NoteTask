package com.example.notetask;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple Timing Object
 * Sets its start time when created, and calculates duration,
 *
 */
class Timing implements Serializable {
private static final long serialVersionUID=20201120L;
private static final String TAG=Timing.class.getSimpleName();

private long m_Id;
private Task mTask;
private long mStartTime;
private long mDuration;

    public Timing(Task task) {
        mTask = task;
        // Initialise the start time to now and duration is zero for a new task
        Date currentTime=new Date();
        mStartTime=currentTime.getTime()/1000;//For seconds
        mDuration=0;
        
    }

     long getId() {
        return m_Id;
    }

     void setId(long Id) {
        m_Id = Id;
    }

     Task getTask() {
        return mTask;
    }

     void setTask(Task task) {
        mTask = task;
    }

     long getStartTime() {
        return mStartTime;
    }

     void setStartTime(long startTime) {
        mStartTime = startTime;
    }

     long getDuration() {
        return mDuration;
    }

     void setDuration() {
        //calculate the duration from mStartTime to dateTime
        Date currentTime=new Date();
        mDuration=(currentTime.getTime()/1000)-mStartTime;//Seconds
        Log.d(TAG,mTask.getId()+"- Start time: "+mStartTime+" | Duration: "+mDuration);
        
    }
}

