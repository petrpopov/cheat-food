package com.petrpopov.cheatfood.web.other;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * User: petrpopov
 * Date: 18.09.13
 * Time: 13:05
 */

@Component
public class ImageService {

    private final String favIcon = "favicon.ico";
    private final String basicName = "basic.png";
    private final String imagesPath = "/resources/img/";
    private final String extension = ".png";

    private final String typesCategory = "types";
    private final String markersCategory = "markers";

    @Autowired
    private ServletContext context;

    public byte[] getMarkerByTypeCode(String typeCode) {

        return getImageBytesByCategory(typeCode, markersCategory);
    }

    public byte[] getIconByTypeCode(String typeCode) {
        return getImageBytesByCategory(typeCode, typesCategory);
    }

    public byte[] getFavicon() {

        byte[] bytes = getImageBytes(getImagePath(favIcon));

        return bytes;
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

    private String getImagePath(String fileName) {

        StringBuilder builder = new StringBuilder();

        builder.append(imagesPath);
        builder.append(fileName);

        return builder.toString();
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
