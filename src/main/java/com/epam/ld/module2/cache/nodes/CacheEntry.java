package com.epam.ld.module2.cache.nodes;

public class CacheEntry {

    private String data;

    public CacheEntry(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "data='" + data + '\'' +
                '}';
    }
}






