package org.springframework.social.foursquare.custom;

/**
 * User: petrpopov
 * Date: 20.02.13
 * Time: 11:41
 */
public class MultipartParameter {

    /**
     * Constructor
     *
     * @param name name of the parameter
     * @param contentType content type
     * @param content content
     */
    public MultipartParameter(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    /**
     * Returns content
     *
     * @return content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns content type
     *
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns parameter name
     *
     * @return parameter name
     */
    public String getName() {
        return name;
    }

    private String name;

    private String contentType;
    private byte[] content;
}
