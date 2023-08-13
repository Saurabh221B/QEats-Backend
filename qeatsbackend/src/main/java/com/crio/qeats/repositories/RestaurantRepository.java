/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositories;

import com.crio.qeats.models.RestaurantEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RestaurantRepository extends MongoRepository<RestaurantEntity, String> {
    //List<RestaurantEntity> findByName(String name);
    List<RestaurantEntity> findByNameRegexIgnoreCase(String searchString);
    Optional<List<RestaurantEntity>> findRestaurantsByRestaurantIdIn(List<String> restaurantsIdList);
    List<RestaurantEntity> findByAttributesIgnoreCase(String searchString);
    List<RestaurantEntity> findByNameIgnoreCaseContainingOrderByNameAsc(String searchString);
    //List<RestaurantEntity> findBy
  
}





