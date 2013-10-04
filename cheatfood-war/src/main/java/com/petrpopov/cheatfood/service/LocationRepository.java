package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.entity.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * User: petrpopov
 * Date: 19.09.13
 * Time: 17:43
 */

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

}
