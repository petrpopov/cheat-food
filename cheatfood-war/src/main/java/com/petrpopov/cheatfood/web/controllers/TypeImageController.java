package com.petrpopov.cheatfood.web.controllers;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * User: petrpopov
 * Date: 29.07.13
 * Time: 11:25
 */

@Controller
@RequestMapping("/api/images")
public class TypeImageController {

    private final String basicName = "basic.png";
    private final String imagesPath = "/resources/img/";
    private final String extension = ".png";

    private final String typesCategory = "types";
    private final String markersCategory = "markers";

    @Autowired
    private ServletContext context;



    @RequestMapping(value = "markers/{typeCode}", produces = "image/png")
    @ResponseBody
    public byte[] getMarkerByTypeCode(@PathVariable String typeCode) {

        return getImageBytesByCategory(typeCode, markersCategory);
    }

    @RequestMapping(value = "types/{typeCode}", produces = "image/png")
    @ResponseBody
    public byte[] getIconByTypeCode(@PathVariable String typeCode) {

        return getImageBytesByCategory(typeCode, typesCategory);
    }

    private byte[] getImageBytesByCategory(String typeCode, String category) {

        byte[] image = getImageBytes( getImagePath(typeCode, category) );

        if( image.length == 0 ) {
            image = getImageBytes( getImagePath(basicName, category) );
        }

        return image;
    }

    private byte[] getImageBytes(String path) {

        InputStream in = context.getResourceAsStream(path);
        try {
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private String getImagePath(String typeCode, String category) {

        StringBuilder builder = new StringBuilder();

        builder.append(imagesPath);
        builder.append(category);
        builder.append("/");
        builder.append(getFilenameFromTypeCode(typeCode));

        return builder.toString();
    }

    private String getFilenameFromTypeCode(String typeCode) {

        if( typeCode == null )
            return typeCode;

        if( typeCode.isEmpty() )
            return typeCode;

        if( typeCode.length() < extension.length() )
            return typeCode+extension;

        int index = typeCode.indexOf(extension);
        if( index < 0 )
            return typeCode+extension;

        String sub = typeCode.substring(index, typeCode.length() );
        if( sub.equals(extension) )
            return typeCode;

        return typeCode+extension;
    }
}
