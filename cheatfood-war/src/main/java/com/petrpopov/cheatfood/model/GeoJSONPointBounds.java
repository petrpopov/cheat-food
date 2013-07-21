package com.petrpopov.cheatfood.model;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 19.07.13
 * Time: 20:38
 */
public class GeoJSONPointBounds {

    @NotNull
    private double ne_latitude;

    @NotNull
    private double ne_longitude;

    @NotNull
    private double sw_latitude;

    @NotNull
    private double sw_longitude;

    public GeoJSONPointBounds() {
    }

    public GeoJSONPointBounds(double ne_latitude, double ne_longitude, double sw_latitude, double sw_longitude) {
        this.ne_latitude = ne_latitude;
        this.ne_longitude = ne_longitude;
        this.sw_latitude = sw_latitude;
        this.sw_longitude = sw_longitude;
    }

    public GeoJSONPoint getNorthEast() {
        return new GeoJSONPoint(ne_latitude, ne_longitude);
    }

    public GeoJSONPoint getSouthWest() {
        return new GeoJSONPoint(sw_latitude, sw_longitude);
    }

    public double getNe_latitude() {
        return ne_latitude;
    }

    public void setNe_latitude(double ne_latitude) {
        this.ne_latitude = ne_latitude;
    }

    public double getNe_longitude() {
        return ne_longitude;
    }

    public void setNe_longitude(double ne_longitude) {
        this.ne_longitude = ne_longitude;
    }

    public double getSw_latitude() {
        return sw_latitude;
    }

    public void setSw_latitude(double sw_latitude) {
        this.sw_latitude = sw_latitude;
    }

    public double getSw_longitude() {
        return sw_longitude;
    }

    public void setSw_longitude(double sw_longitude) {
        this.sw_longitude = sw_longitude;
    }
}
