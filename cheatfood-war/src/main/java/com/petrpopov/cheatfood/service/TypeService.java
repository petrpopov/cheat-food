package com.petrpopov.cheatfood.service;

import com.mongodb.DBCollection;
import com.petrpopov.cheatfood.model.entity.Text;
import com.petrpopov.cheatfood.model.entity.Type;
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

        types1 = getAllConstantTypes();

        if( !op.collectionExists(Type.class) ) {
            String name = op.getCollectionName(Type.class);
            DBCollection collection = op.getCollection(name);

            if( collection.count() == types1.size() ) {
                return;
            }

            saveAllTypes(types1);
        }
        else {
            List<Type> savedTypeList = this.findAll();

            if( savedTypeList == null ) {
                saveAllTypes(types1);
                return;
            }

            if( savedTypeList.isEmpty() ) {
                saveAllTypes(types1);
                return;
            }

            for (Type type : types1) {
                if( ifTypeIsInList(type, savedTypeList) == false ) {
                    op.save(type);
                }
            }
        }
    }

    @Override
    public List<Type> findAll() {
        List<Type> types = super.findAll();

        List<Type> sorted = new ArrayList<Type>();
        for (Type type : types1) {
            Type byCode = getTypeByCode(type.getCode(), types);
            if( byCode != null ) {
                sorted.add(byCode);
            }
        }

        return sorted;
    }

    private void saveAllTypes(List<Type> types) {
        for (Type type : types) {
            op.save(type);
        }
    }

    private List<Type> getAllConstantTypes() {

        List<Type> types = new ArrayList<Type>();

        types.add( new Type(new Text(lang, "Лапшичная"), "noodle" ));
        types.add( new Type(new Text(lang, "Шашлычная"), "kebab" ));
        types.add( new Type(new Text(lang, "Чебуречная"), "meatpastry" ));
        types.add( new Type(new Text(lang, "Пельменная"), "ravioli" ));
        types.add( new Type(new Text(lang, "Хинкальная"), "hinkali" ));
        types.add( new Type(new Text(lang, "Шаверменная"), "shawarma" ));
        types.add( new Type(new Text(lang, "Чайхона"), "tea" ));
        types.add( new Type(new Text(lang, "Блинная"), "pancake" ));
        types.add( new Type(new Text(lang, "Бутербродная"), "sandwich" ));
        types.add( new Type(new Text(lang, "Бургерная"), "burger" ));
        types.add( new Type(new Text(lang, "Пироги"), "pie" ));
        types.add( new Type(new Text(lang, "Лепешки"), "tandoor" ));
        types.add( new Type(new Text(lang, "Рюмочная"), "glass" ));
        types.add( new Type(new Text(lang, "Булочная"), "bread" ));
        types.add( new Type(new Text(lang, "Столовая"), "refectory" ));
        types.add( new Type(new Text(lang, "Кафе"), "cafe" ));
        types.add( new Type(new Text(lang, "Кулинария"), "gastronomy" ));
        types.add( new Type(new Text(lang, "Другое"), "other" ));

        return types;
    }

    private Type getTypeByCode(String code, List<Type> types) {

        if( types == null )
            return null;

        for (Type type : types) {
            if( type.getCode().equals(code) )
                return type;
        }

        return null;
    }

    private boolean ifTypeIsInList(Type type, List<Type> types) {

        if( types == null )
            return false;

        for (Type type1 : types) {
            if( type1.getCode().equals(type.getCode()) )
                return true;
        }

        return false;
    }
}
