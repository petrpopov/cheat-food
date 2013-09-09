package com.petrpopov.cheatfood.config;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: petrpopov
 * Date: 09.09.13
 * Time: 12:38
 */

@Component
public class TimeSerializer extends JsonSerializer<Date> {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(simpleDateFormat.format(value));
    }
}
