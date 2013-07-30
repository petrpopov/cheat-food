package com.petrpopov.cheatfood.web.other;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User: petrpopov
 * Date: 31.07.13
 * Time: 0:17
 */

//got from here: https://randomcoder.org/articles/jsessionid-considered-harmful

@Component
public class DisableUrlSessionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest))
        {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.isRequestedSessionIdFromURL())
        {
            HttpSession session = httpRequest.getSession();
            if (session != null) session.invalidate();
        }

        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse)
        {
            public String encodeRedirectUrl(String url) { return url; }
            public String encodeRedirectURL(String url) { return url; }
            public String encodeUrl(String url) { return url; }
            public String encodeURL(String url) { return url; }
        };

        chain.doFilter(request, wrappedResponse);
    }


}
