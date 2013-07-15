package org.springframework.social.foursquare.api;

/**
 * User: petrpopov
 * Date: 20.02.13
 * Time: 18:05
 */
public class Icon {

    private String prefix;
    private String suffix;

    public Icon() {
    }

    public Icon(String prefix) {
        this.prefix = prefix;
    }

    public Icon(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
