package org.springframework.social.foursquare.api;

/**
 * User: petrpopov
 * Date: 21.02.13
 * Time: 15:35
 */
public class FoursquareUserPhoto {

    private String preffix;
    private String suffix;

    public FoursquareUserPhoto() {
    }

    public FoursquareUserPhoto(String preffix) {
        this.preffix = preffix;
    }

    public FoursquareUserPhoto(String preffix, String suffix) {
        this.preffix = preffix;
        this.suffix = suffix;
    }

    public String getPreffix() {
        return preffix;
    }

    public void setPreffix(String preffix) {
        this.preffix = preffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
