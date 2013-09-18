package com.petrpopov.cheatfood.web.controllers;

import com.petrpopov.cheatfood.web.other.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: petrpopov
 * Date: 29.07.13
 * Time: 11:25
 */

@Controller
@RequestMapping("/api/images")
public class TypeImageController {

    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "markers/{typeCode}", produces = "image/png")
    @ResponseBody
    public byte[] getMarkerByTypeCode(@PathVariable String typeCode) {

        byte[] bytes = imageService.getMarkerByTypeCode(typeCode);
        return bytes;
    }

    @RequestMapping(value = "types/{typeCode}", produces = "image/png")
    @ResponseBody
    public byte[] getIconByTypeCode(@PathVariable String typeCode) {

        byte[] bytes = imageService.getIconByTypeCode(typeCode);
        return bytes;
    }
}
