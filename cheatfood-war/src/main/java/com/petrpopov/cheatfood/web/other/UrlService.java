package com.petrpopov.cheatfood.web.other;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * User: petrpopov
 * Date: 28.08.13
 * Time: 15:58
 */

@Component
public class UrlService {

    public String getGlobalUrl(HttpServletRequest request) {

        return request.getScheme()+"://"
                + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
