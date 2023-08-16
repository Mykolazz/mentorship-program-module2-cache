package com.epam.ld.module2.cache;

public abstract class CacheService {

    protected double averagePutTime;
    protected double averageGetTime;
    protected double maxPutTime;
    protected double maxGetTime;
    protected int cacheEvictions = 0;

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
