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
        types1.add( new Type(new Text(lang, "Шашлычная"), "kebab" ));
        types1.add( new Type(new Text(lang, "Чебуречная"), "meatpastry" ));
        types1.add( new Type(new Text(lang, "Шаверменная"), "shawarma" ));
        types1.add( new Type(new Text(lang, "Чайхона"), "chayhona" ));
        types1.add( new Type(new Text(lang, "Блинная"), "pancake" ));
        types1.add( new Type(new Text(lang, "Бутербродная"), "sandwich" ));
        types1.add( new Type(new Text(lang, "Рюмочная"), "glass" ));
        types1.add( new Type(new Text(lang, "Булочная"), "bread" ));
        types1.add( new Type(new Text(lang, "Столовая"), "refectory" ));
        types1.add( new Type(new Text(lang, "Кафе"), "cafe" ));
        types1.add( new Type(new Text(lang, "Кулинария"), "gastronomy" ));
        types1.add( new Type(new Text(lang, "Другое"), "other" ));


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
