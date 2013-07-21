package com.petrpopov.cheatfood.model;

import com.petrpopov.cheatfood.config.CheatException;

/**
 * User: petrpopov
 * Date: 19.07.13
 * Time: 18:59
 */
public class GeoJSONPoint {

    private String type;
    private double[] coordinates;

    public GeoJSONPoint() {
        type = "Point";
        this.coordinates = new double[2];
    }

    public GeoJSONPoint(double[] coordinates) {
        type = "Point";
        this.coordinates = coordinates;
    }

    public GeoJSONPoint(double latitude, double longitude) {
        type = "Point";
        this.coordinates = new double[2];
        this.coordinates[0] = longitude;
        this.coordinates[1] = latitude;
    }

    public GeoJSONPoint(String[] coord) throws CheatException {
        if( coord.length < 2 )
            throw new CheatException("Incorrect coordinates format !");

        type = "Point";
        try {
            double lat = Double.parseDouble(coord[0]);
            double lng = Double.parseDouble(coord[1]);

            this.coordinates[0] = lat;
            this.coordinates[1] = lng;
        }
        catch (Exception e) {
            throw new CheatException(e);
        }
    }

    public GeoJSONPoint(String latitude, String longitude) throws CheatException {
        type = "Point";
        this.coordinates = new double[2];

        try {
            double lat = Double.parseDouble(latitude);
            double lng = Double.parseDouble(longitude);

            this.coordinates[0] = lat;
            this.coordinates[1] = lng;
        }
        catch (Exception e) {
            throw new CheatException(e);
        }
    }

    public String getCoordinatesInString() {

        String lat = String.valueOf(coordinates[1]);
        String lng = String.valueOf(coordinates[0]);

        return lat + "," + lng;
    }

    public double getLatitude() {
        if( coordinates.length >= 2 )
            return coordinates[1];
        return 0.0;
    }

    public double getLongitude() {
        if( coordinates.length >= 2 )
            return coordinates[0];
        return 0.0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
