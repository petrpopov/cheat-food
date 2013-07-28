package com.petrpopov.cheatfood.service;

import com.mongodb.DBCollection;
import com.petrpopov.cheatfood.model.Text;
import com.petrpopov.cheatfood.model.Type;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * User: petrpopov
 * Date: 12.07.13
 * Time: 2:54
 */

@Component
public class TypeService extends GenericService<Type> {

    private String lang = "ru";
    private List<Type> types1 = new ArrayList<Type>();

    public TypeService() {
        super(Type.class);
    }

    @PostConstruct
    public void init() {
        types1.add( new Type(new Text(lang, "Шашлычная") ) );
        types1.add( new Type(new Text(lang, "Чебуречная") ) );
        types1.add( new Type(new Text(lang, "Шаверменная") ) );
        types1.add( new Type(new Text(lang, "Чайхона") ) );
        types1.add( new Type(new Text(lang, "Блинная") ) );
        types1.add( new Type(new Text(lang, "Бутербродная") ) );
        types1.add( new Type(new Text(lang, "Рюмочная") ) );
        types1.add( new Type(new Text(lang, "Булочная") ) );
        types1.add( new Type(new Text(lang, "Столовая") ) );
        types1.add( new Type(new Text(lang, "Кафе") ) );
        types1.add( new Type(new Text(lang, "Кулинария") ) );
        types1.add( new Type(new Text(lang, "Другое") ) );


        if( !op.collectionExists(Type.class) ) {
            String name = op.getCollectionName(Type.class);
            DBCollection collection = op.getCollection(name);

            if( collection.count() == types1.size() ) {
                return;
            }

            for (Type type : types1) {
                op.save(type);
            }
        }
    }
}
