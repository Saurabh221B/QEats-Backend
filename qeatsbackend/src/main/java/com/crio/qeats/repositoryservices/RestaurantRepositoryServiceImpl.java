/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;
import redis.clients.jedis.Jedis;
import ch.hsr.geohash.GeoHash;
import com.crio.qeats.configs.RedisConfiguration;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.ItemEntity;
import com.crio.qeats.models.MenuEntity;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.ItemRepository;
import com.crio.qeats.repositories.MenuRepository;
import com.crio.qeats.repositories.RestaurantRepository;
import com.crio.qeats.utils.GeoLocation;
import com.crio.qeats.utils.GeoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;


@Primary
@Service
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;
  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private MenuRepository menuRepository;
  
  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Autowired
  private RedisConfiguration redisConfiguration;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }


public List<Restaurant> findAllRestaurantsCloseBy(Double latitude, Double longitude, LocalTime currentTime,
Double servingRadiusInKms) {
List<Restaurant> restaurants = null;
if (redisConfiguration.isCacheAvailable()) {
restaurants = findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime, servingRadiusInKms);
} else {
restaurants = findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
}
return restaurants;
}



  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objectives:
  // 1. Implement findAllRestaurantsCloseby.
  // 2. Remember to keep the precision of GeoHash in mind while using it as a key.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
        ModelMapper modelMapper=modelMapperProvider.get();
        List<RestaurantEntity>restaurantEntitiesList=restaurantRepository.findAll();
        List<Restaurant>restaurantList=new ArrayList<Restaurant>();
        for(RestaurantEntity restaurantEntity:restaurantEntitiesList){
          if(isOpenNow(currentTime, restaurantEntity)){
            if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
              restaurantList.add(modelMapper.map(restaurantEntity, Restaurant.class));
            }

          }
        }

    //List<Restaurant> restaurants = null;


      //CHECKSTYLE:OFF
      //CHECKSTYLE:ON


    return restaurantList;
  }
  

  private List<Restaurant> findAllRestaurantsCloseByFromCache(Double latitude, Double longitude, LocalTime currentTime,
      Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();

    GeoLocation geoLocation = new GeoLocation(latitude, longitude);
    GeoHash geoHash = GeoHash.withCharacterPrecision(geoLocation.getLatitude(), geoLocation.getLongitude(), 7);

    try (Jedis jedis = redisConfiguration.getJedisPool().getResource()) {
      String jsonStringFromCache = jedis.get(geoHash.toBase32());

      if (jsonStringFromCache == null) {
        // Cache needs to be updated.
        String createdJsonString = "";
        try {
          restaurantList = findAllRestaurantsCloseFromDb(geoLocation.getLatitude(), geoLocation.getLongitude(),
              currentTime, servingRadiusInKms);
          createdJsonString = new ObjectMapper().writeValueAsString(restaurantList);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }

        // Do operations with jedis resource
        jedis.setex(geoHash.toBase32(), GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS, createdJsonString);
      } else {
        try {
          restaurantList = new ObjectMapper().readValue(jsonStringFromCache, new TypeReference<List<Restaurant>>() {
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return restaurantList;
  // public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
  //     Double longitude, LocalTime currentTime, Double servingRadiusInKms) {

  //   List<Restaurant> restaurants = null;




  //   return restaurants;
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants whose names have an exact or partial match with the search query.
  @Override
  public List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
      //List<Restaurant>
      //System.out.println("hi I am");
      //  Criteria regexCriteria = Criteria.where("name").regex(searchString, "i");
      //  Query query=new Query(regexCriteria);
      //  query.with(Sort.by(Sort.Direction.ASC, searchString));
      // List<RestaurantEntity> restaurantEntityList=mongoTemplate.find(query, RestaurantEntity.class);
     //List<RestaurantEntity> restaurantEntityList=restaurantRepository.findByNameIgnoreCaseContainingOrderByNameAsc(searchString);
      //System.out.println(restaurantEntities.size());
     
      //ModelMapper modelMapper=modelMapperProvider.get();
      
      // List<Restaurant>restaurantList=new ArrayList<Restaurant>();
      // for(RestaurantEntity restaurantEntity:restaurantEntities){
      //   if(isOpenNow(currentTime, restaurantEntity)){
      //     if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
      //       restaurantList.add(modelMapper.map(restaurantEntity, Restaurant.class));
      //     }

      //  }
      // }
      BasicQuery query = new BasicQuery("{name: {$regex: /" + searchString + "/i}}");
      List<RestaurantEntity> restaurantEntityList = mongoTemplate
          .find(query, RestaurantEntity.class, "restaurants");
      List<Restaurant>restaurantList=mapRestaurantEntityToRestaurant(restaurantEntityList, latitude, longitude, currentTime, servingRadiusInKms);

      return restaurantList;
  }


  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants  
  @Override
  public List<Restaurant> findRestaurantsByAttributes(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
        List<RestaurantEntity> restaurantEntityList=restaurantRepository.findByAttributesIgnoreCase(searchString);

        List<Restaurant>restaurantList=mapRestaurantEntityToRestaurant(restaurantEntityList, latitude, longitude, currentTime, servingRadiusInKms);

        return restaurantList;



    
  }



  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants which serve food items whose names form a complete or partial match
  // with the search query.

  @Override
  public List<Restaurant> findRestaurantsByItemName(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
      List<ItemEntity> itemEntities=itemRepository.findByNameRegexIgnoreCase(searchString);
      List<RestaurantEntity>restaurantEntityList=findRestaurantEntityByItemEntity(itemEntities);
      List<Restaurant>restaurantList=mapRestaurantEntityToRestaurant(restaurantEntityList, latitude, longitude, currentTime, servingRadiusInKms);
      return restaurantList;





      // ModelMapper modelMapper=modelMapperProvider.get();
      // System.out.println("in ---");
      // List<Item>itemList=new ArrayList<Item>();
      // for(ItemEntity itemEntity:itemEntities){
      //   //System.out.println(itemEntity.getName());
      //   //if(isOpenNow(currentTime, restaurantEntity)){
      //    // if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
      //     itemList.add(modelMapper.map(itemEntity, Item.class));
      //   //  }

      //  // }
      // }
      // System.out.println("itemlist size"+ itemList.size());
      // List<String>itemId=itemEntities.stream().
      //                   map(ItemEntity::getItemId).
      //                   collect(Collectors.toList());

     

      // Optional<List<MenuEntity>> menuEntities=menuRepository.findMenusByItemsItemIdIn(itemId);
      // List<MenuEntity> menuEntitiess=menuEntities.get();

      // List<String>restaurantIDsList=menuEntitiess.stream().
      // map(MenuEntity::getRestaurantId).
      // collect(Collectors.toList());

      // for(String id:restaurantIDsList){
      //   System.out.println(id);

      // } 
      // Optional<List<RestaurantEntity>>restaurantEntiytList=restaurantRepository.findRestaurantsByRestaurantIdIn(restaurantIDsList);
      // List<RestaurantEntity>restaurantListt=restaurantEntiytList.get();
      // List<Restaurant>restaurantList=new ArrayList<>();
      // for(RestaurantEntity restaurantEntity:restaurantListt){
      //   //if(isOpenNow(currentTime, restaurantEntity)){
      //    // if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
      //       restaurantList.add(modelMapper.map(restaurantEntity, Restaurant.class));
      //   //  }

      //  // }
      // }
      // System.out.println("restaurantlist");
      // System.out.println(restaurantList);

      // return restaurantList;


      



      //return new ArrayList<>();

  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants which serve food items whose attributes intersect with the search query.
  @Override
  public List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {   
      List<ItemEntity> itemEntities=itemRepository.findByAttributes(searchString);
      List<RestaurantEntity>restaurantEntityList=findRestaurantEntityByItemEntity(itemEntities);
      List<Restaurant>restaurantList=mapRestaurantEntityToRestaurant(restaurantEntityList, latitude, longitude, currentTime, servingRadiusInKms);
      return restaurantList;
    //   ModelMapper modelMapper=modelMapperProvider.get();
    //   System.out.println("in ---");
    //   List<Item>itemList=new ArrayList<Item>();
    //   for(ItemEntity itemEntity:itemEntities){
    //     System.out.println(itemEntity.getName());
    //     System.out.println((itemEntity.getAttributes()).toString());
    //     //if(isOpenNow(currentTime, restaurantEntity)){
    //      // if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
    //       itemList.add(modelMapper.map(itemEntity, Item.class));
    //     //  }

    //    // }
    //   }


    //  return new ArrayList<>();
  }

  public List<RestaurantEntity> findRestaurantEntityByItemEntity( List<ItemEntity> itemEntityList){
    //ModelMapper modelMapper=modelMapperProvider.get();
    System.out.println("in ---");
    // List<Item>itemList=new ArrayList<Item>();
    // for(ItemEntity itemEntity:itemEntityList){
    //   //System.out.println(itemEntity.getName());
    //   //if(isOpenNow(currentTime, restaurantEntity)){
    //    // if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
    //     itemList.add(modelMapper.map(itemEntity, Item.class));
    //   //  }

    //  // }
    // }
    List<String>itemIdList=itemEntityList.stream().
    map(ItemEntity::getItemId).
    collect(Collectors.toList());

    Optional<List<MenuEntity>> menuEntities=menuRepository.findMenusByItemsItemIdIn(itemIdList);
      List<MenuEntity> menuEntitiess=menuEntities.get();

      List<String>restaurantIDsList=menuEntitiess.stream().
      map(MenuEntity::getRestaurantId).
      collect(Collectors.toList());

      Optional<List<RestaurantEntity>>restaurantEntiytList=restaurantRepository.findRestaurantsByRestaurantIdIn(restaurantIDsList);
      List<RestaurantEntity>restaurantEntityList=restaurantEntiytList.get();











    return restaurantEntityList;


  }

  public List<Restaurant> mapRestaurantEntityToRestaurant(List<RestaurantEntity>restaurantEntityList,Double latitude, Double longitude,
   LocalTime currentTime, Double servingRadiusInKms) {
    ModelMapper modelMapper=modelMapperProvider.get();
    List<Restaurant>restaurantList=new ArrayList<Restaurant>();
    for(RestaurantEntity restaurantEntity:restaurantEntityList){
      if(isOpenNow(currentTime, restaurantEntity)){
        if(isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude, servingRadiusInKms)){
          restaurantList.add(modelMapper.map(restaurantEntity, Restaurant.class));
        }

     }
    }
    return restaurantList;
  }






  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objective:
  // 1. Check if a restaurant is nearby and open. If so, it is a candidate to be returned.
  // NOTE: How far exactly is "nearby"?

  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
      LocalTime currentTime, Double latitude, Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return GeoUtils.findDistanceInKm(latitude, longitude,
          restaurantEntity.getLatitude(), restaurantEntity.getLongitude())
          < servingRadiusInKms;
    }

    return false;
  }



}

