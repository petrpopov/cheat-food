package com.petrpopov.cheapfood.model;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:36
 */
public class Address {

    private String zipcode;
    private String country;
    private String region;
    private String city;
    private String address;

    public Address() {
    }

    public Address(String zipcode, String country, String region, String city, String address) {
        this.zipcode = zipcode;
        this.country = country;
        this.region = region;
        this.city = city;
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address1 = (Address) o;

        if (address != null ? !address.equals(address1.address) : address1.address != null) return false;
        if (city != null ? !city.equals(address1.city) : address1.city != null) return false;
        if (country != null ? !country.equals(address1.country) : address1.country != null) return false;
        if (region != null ? !region.equals(address1.region) : address1.region != null) return false;
        if (zipcode != null ? !zipcode.equals(address1.zipcode) : address1.zipcode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = zipcode != null ? zipcode.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
