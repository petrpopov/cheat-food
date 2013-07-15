package org.springframework.social.foursquare.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"cc"})
public class Location {
	
	private String address;
	private String crossStreet;
	private String city;
	private String state;
	private String postalCode;
	private String country;
    private String cc;
    private String countryCode;
	private double latitude;
    private double lat;
    private double lng;
	private double longitude;
	private int distance;

    public Location() {
    }

    public Location(String address, String crossStreet, String city, String state,
			String postalCode, String country, double latitude, double longitude) {
		this.address = address;
		this.crossStreet = crossStreet;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCrossStreet() {
		return crossStreet;
	}

	public void setCrossStreet(String crossStreet) {
		this.crossStreet = crossStreet;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
        this.lat = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
        this.lng = longitude;
	}
	
	public int getDistance() {
		return distance;
	}

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
        this.latitude = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
        this.longitude = lng;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
        this.countryCode = cc;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        this.cc = countryCode;
    }
}
