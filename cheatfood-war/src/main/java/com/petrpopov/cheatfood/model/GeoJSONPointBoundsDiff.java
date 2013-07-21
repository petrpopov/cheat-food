package com.petrpopov.cheatfood.model;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 21.07.13
 * Time: 16:47
 */
public class GeoJSONPointBoundsDiff {

    @NotNull
    private double ne_latitude_cur;

    @NotNull
    private double ne_longitude_cur;

    @NotNull
    private double sw_latitude_cur;

    @NotNull
    private double sw_longitude_cur;

    private double ne_latitude_prev;
    private double ne_longitude_prev;
    private double sw_latitude_prev;
    private double sw_longitude_prev;

    public GeoJSONPointBoundsDiff() {
    }

    public GeoJSONPointBounds getCurrent() {
        return new GeoJSONPointBounds(ne_latitude_cur, ne_longitude_cur, sw_latitude_cur, sw_longitude_cur);
    }

    public GeoJSONPointBounds getPrevious() {
        if( ne_latitude_prev == 0 || ne_longitude_prev == 0 || sw_latitude_prev == 0 || sw_longitude_prev == 0) {
            return null;
        }

        return new GeoJSONPointBounds(ne_latitude_prev, ne_longitude_prev, sw_latitude_prev, sw_longitude_prev);
    }

    public double getNe_latitude_cur() {
        return ne_latitude_cur;
    }

    public void setNe_latitude_cur(double ne_latitude_cur) {
        this.ne_latitude_cur = ne_latitude_cur;
    }

    public double getNe_longitude_cur() {
        return ne_longitude_cur;
    }

    public void setNe_longitude_cur(double ne_longitude_cur) {
        this.ne_longitude_cur = ne_longitude_cur;
    }

    public double getSw_latitude_cur() {
        return sw_latitude_cur;
    }

    public void setSw_latitude_cur(double sw_latitude_cur) {
        this.sw_latitude_cur = sw_latitude_cur;
    }

    public double getSw_longitude_cur() {
        return sw_longitude_cur;
    }

    public void setSw_longitude_cur(double sw_longitude_cur) {
        this.sw_longitude_cur = sw_longitude_cur;
    }

    public double getNe_latitude_prev() {
        return ne_latitude_prev;
    }

    public void setNe_latitude_prev(double ne_latitude_prev) {
        this.ne_latitude_prev = ne_latitude_prev;
    }

    public double getNe_longitude_prev() {
        return ne_longitude_prev;
    }

    public void setNe_longitude_prev(double ne_longitude_prev) {
        this.ne_longitude_prev = ne_longitude_prev;
    }

    public double getSw_latitude_prev() {
        return sw_latitude_prev;
    }

    public void setSw_latitude_prev(double sw_latitude_prev) {
        this.sw_latitude_prev = sw_latitude_prev;
    }

    public double getSw_longitude_prev() {
        return sw_longitude_prev;
    }

    public void setSw_longitude_prev(double sw_longitude_prev) {
        this.sw_longitude_prev = sw_longitude_prev;
    }
}
