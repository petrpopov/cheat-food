package org.springframework.social.foursquare.custom;

/**
 * User: petrpopov
 * Date: 20.02.13
 * Time: 11:40
 */
public abstract class IOHandler {
    /**
     * Method used in API queries
     *
     * @param url URL of the query
     * @param method method used
     * @return Response
     */
    public abstract Response fetchData(String url, Method method);

    /**
     * Method used in multipart/mime API queries
     *
     * @param url URL of the query
     * @param params multipart parameters
     * @return Response
     */
    public abstract Response fetchDataMultipartMime(String url, MultipartParameter... params);
}
