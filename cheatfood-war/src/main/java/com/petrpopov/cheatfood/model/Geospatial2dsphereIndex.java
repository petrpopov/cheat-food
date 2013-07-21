package com.petrpopov.cheatfood.model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

/**
 * User: petrpopov
 * Date: 21.07.13
 * Time: 10:38
 */
public class Geospatial2dsphereIndex extends GeospatialIndex {

    private final String field;

    /**
     * Creates a new {@link org.springframework.data.mongodb.core.index.GeospatialIndex} for the given field.
     *
     * @param field must not be empty or {@literal null}.
     */
    public Geospatial2dsphereIndex(String field) {
        super(field);
        this.field = field;
    }

    @Override
    public DBObject getIndexKeys() {
        DBObject dbo = new BasicDBObject();
        dbo.put(field, "2dsphere");
        return dbo;
    }
}
