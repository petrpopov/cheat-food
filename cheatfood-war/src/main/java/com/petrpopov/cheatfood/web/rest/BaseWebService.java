package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.entity.Location;

/**
 * User: petrpopov
 * Date: 21.08.13
 * Time: 1:41
 */
public class BaseWebService {

    protected MessageResult checkIfLocationExists(Location location) {

        MessageResult result = new MessageResult();
        if( location == null ) {
            result.setError(true);
            result.setErrorType(ErrorType.unknown_location);
            result.setMessage("There is no such location!");

            return result;
        }

        return result;
    }
}
