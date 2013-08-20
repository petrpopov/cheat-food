package com.petrpopov.cheatfood.model.entity;

/**
 * User: petrpopov
 * Date: 28.07.13
 * Time: 6:05
 */
public class Geo2DPoint {

    private double lng;
    private double lat;

    public Geo2DPoint() {
    }

    public Geo2DPoint(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double[] getCoordinates() {
        double[] res = new double[2];
        res[0] = lng;
        res[1] = lat;
        return res;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Geo2DPoint that = (Geo2DPoint) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lng, lng) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lng);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
