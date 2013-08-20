package com.petrpopov.cheatfood.model.data;

/**
 * User: petrpopov
 * Date: 19.08.13
 * Time: 16:17
 */
public class LocationsInfo {

    private long totalCount;
    private long regionCount;
    private long newCount;

    public LocationsInfo() {
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getRegionCount() {
        return regionCount;
    }

    public void setRegionCount(long regionCount) {
        this.regionCount = regionCount;
    }

    public long getNewCount() {
        return newCount;
    }

    public void setNewCount(long newCount) {
        this.newCount = newCount;
    }
}
