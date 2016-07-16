package com.lamfire.utils;

import com.lamfire.logger.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-9-30
 * Time: ÏÂÎç12:02
 * To change this template use File | Settings | File Templates.
 */
public class OPSMonitor{
    private static final Logger LOGGER = Logger.getLogger(OPSMonitor.class);

    private final AtomicInteger counter = new AtomicInteger(0);
    private String id;
    private int ops = 0;
    private int prevCount = 0;
    private int interval = 1;

    private long prevTime = System.nanoTime();
    private long lastExpendTime = 0;

    private long maxExpendTime = 0;
    private long totalExpendTime = 0;

    private Runnable worker = new Runnable() {
        @Override
        public void run() {
            int thisCount = counter.get();
            ops = Math.abs(thisCount - prevCount);
            prevCount = thisCount;
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("["+id+"]count=" + thisCount +",ops=" + ops + "/" + interval +"s");
            }
        }
    } ;

    public OPSMonitor(String id){
        this.id = id;
    }



    public synchronized void done(){
        long thisTime = System.nanoTime();
        counter.incrementAndGet();
        this.lastExpendTime = thisTime - prevTime;
        if(this.maxExpendTime < lastExpendTime){
            this.maxExpendTime = lastExpendTime;
        }
        this.totalExpendTime += lastExpendTime;
        this.prevTime = thisTime;
    }

    public long getLastExpendTimeNano(){
        return this.lastExpendTime;
    }

    public long getLastExpendTimeMillis(){
        return this.lastExpendTime /1000/1000;
    }

    public long getLastExpendTimeSecond(){
        return this.lastExpendTime /1000 /1000 /1000;
    }

    public long getMaxExpendTimeNano(){
        return  this.maxExpendTime;
    }

    public long getMaxExpendTimeMillis(){
        return  this.maxExpendTime /1000/1000;
    }

    public long getMaxExpendTimeSecond(){
        return  this.maxExpendTime /1000/1000/1000;
    }

    public long getAvgExpendTimeNano(){
        int count = counter.get();
        if(count ==0){
            return 0;
        }
        return  this.totalExpendTime / count;
    }

    public long getAvgExpendTimeMillis(){
        return  getAvgExpendTimeNano() /1000/1000;
    }

    public long getAvgExpendTimeSecond(){
        return  getAvgExpendTimeNano() /1000/1000/1000;
    }

    public long getTotalExpendTimeNano(){
        return  this.totalExpendTime;
    }

    public long getTotalExpendTimeMillis(){
        return  this.totalExpendTime /1000/1000;
    }

    public long getTotalExpendTimeSecond(){
        return  this.totalExpendTime/1000/1000/1000;
    }

    public int getOps(){
        return ops;
    }

    public int getCount(){
        return this.counter.get();
    }

    public String getId(){
        return id;
    }

    public void setInterval(int intervalSecond){
        this.interval = intervalSecond;
    }

    public int getInterval(){
        return this.interval;
    }

    public void startup(){
        Threads.scheduleWithFixedDelay(worker,interval,interval, TimeUnit.SECONDS);
    }

    public void shutdown(){
        Threads.removeScheduledTask(worker);
    }
}
