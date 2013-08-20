package com.petrpopov.cheatfood.model.data;

import com.petrpopov.cheatfood.model.entity.Geo2DPoint;

import javax.validation.constraints.NotNull;

/**
 * User: petrpopov
 * Date: 19.07.13
 * Time: 20:38
 */
public class GeoPointBounds {

    @NotNull
    private double ne_latitude;

    @NotNull
    private double ne_longitude;

    @NotNull
    private double sw_latitude;

    @NotNull
    private double sw_longitude;

    public GeoPointBounds() {
    }

    public GeoPointBounds(double ne_latitude, double ne_longitude, double sw_latitude, double sw_longitude) {
        this.ne_latitude = ne_latitude;
        this.ne_longitude = ne_longitude;
        this.sw_latitude = sw_latitude;
        this.sw_longitude = sw_longitude;
    }

    public GeoJSONPoint getNorthEastJSONPoint() {
        return new GeoJSONPoint(ne_latitude, ne_longitude);
    }

    public GeoJSONPoint getSouthWestJSONPoint() {
        return new GeoJSONPoint(sw_latitude, sw_longitude);
    }

    public Geo2DPoint getNorthEast2DPoint() {
        return new Geo2DPoint(ne_longitude, ne_latitude);
    }

    public Geo2DPoint getSouthWest2DPoint() {
        return new Geo2DPoint(sw_longitude, sw_latitude);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoPointBounds that = (GeoPointBounds) o;

        if (Double.compare(that.ne_latitude, ne_latitude) != 0) return false;
        if (Double.compare(that.ne_longitude, ne_longitude) != 0) return false;
        if (Double.compare(that.sw_latitude, sw_latitude) != 0) return false;
        if (Double.compare(that.sw_longitude, sw_longitude) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(ne_latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ne_longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sw_latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sw_longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
