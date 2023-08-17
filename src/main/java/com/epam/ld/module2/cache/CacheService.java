package com.epam.ld.module2.cache;

public abstract class CacheService {
    protected static final int DEFAULT_MAX_SIZE = 1000;
    protected final int maxSize;
    protected double averagePutTime;
    protected double averageGetTime;
    protected double maxPutTime;
    protected double maxGetTime;
    protected int cacheEvictions = 0;
    protected int putsCounter = 0;
    protected int getsCounter = 0;

    protected CacheService(){
        this.maxSize = DEFAULT_MAX_SIZE;
    }

    protected CacheService(int maxSize){
        this.maxSize = maxSize;
    }

    protected void calculatePuttingTime(long begin, long end){
        long time = end - begin;
        if (time > maxPutTime){
            maxPutTime = time;
        }
        if(averagePutTime == 0){
            averagePutTime = time;
        }
        else{
            averagePutTime = (averagePutTime + time)/putsCounter;
        }
    }

    protected void calculateGettingTime(long begin, long end){
        long time = end - begin;
        if (time > maxGetTime){
            maxGetTime = time;
        }
        if(averageGetTime == 0){
            averageGetTime= time;
        }
        else{
            averageGetTime = (averageGetTime + time)/getsCounter;
        }
    }

    public int getCacheEvictions() {
        return cacheEvictions;
    }

    public String getStatistic(){
        return "cacheEvictions: " + cacheEvictions + "\n"
                + "averagePutTime: " + averagePutTime + "\n"
                + "averageGetTime: " + averageGetTime + "\n"
                + "maxPutTime: " + maxPutTime + "\n"
                + "maxGetTime: "+ maxGetTime;
    }
}
