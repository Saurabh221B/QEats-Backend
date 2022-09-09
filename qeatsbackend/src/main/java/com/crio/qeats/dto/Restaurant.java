
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: CRIO_TASK_MODULE_SERIALIZATION
//  Implement Restaurant class.
// Complete the class such that it produces the following JSON during serialization.
// {
//  "restaurantId": "10",
//  "name": "A2B",
//  "city": "Hsr Layout",
//  "imageUrl": "www.google.com",
//  "latitude": 20.027,
//  "longitude": 30.0,
//  "opensAt": "18:00",
//  "closesAt": "23:00",
//  "attributes": [
//    "Tamil",
//    "South Indian"
//  ]
// }
@Getter 
@Setter
public class Restaurant {
    @JsonProperty("restaurantId")
    private String restaurantId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("city")
    private String city;
    @JsonProperty("imageUrl")
    private String imageUrl;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("opensAt")
    private String opensAt;
    @JsonProperty("closesAt")
    private String closesAt;
    @JsonProperty("attributes")
    private List<String> attributes = null;
    // @JsonIgnore
    // private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    // @JsonProperty("restaurantId")
    // public String getRestaurantId() {
    // return restaurantId;
    // }
    
    // @JsonProperty("restaurantId")
    // public void setRestaurantId(String restaurantId) {
    // this.restaurantId = restaurantId;
    // }
    
    // @JsonProperty("name")
    // public String getName() {
    // return name;
    // }
    
    // @JsonProperty("name")
    // public void setName(String name) {
    // this.name = name;
    // }
    
    // @JsonProperty("city")
    // public String getCity() {
    // return city;
    // }
    
    // @JsonProperty("city")
    // public void setCity(String city) {
    // this.city = city;
    // }
    
    // @JsonProperty("imageUrl")
    // public String getImageUrl() {
    // return imageUrl;
    // }
    
    // @JsonProperty("imageUrl")
    // public void setImageUrl(String imageUrl) {
    // this.imageUrl = imageUrl;
    // }
    
    // @JsonProperty("latitude")
    // public Double getLatitude() {
    // return latitude;
    // }
    
    // @JsonProperty("latitude")
    // public void setLatitude(Double latitude) {
    // this.latitude = latitude;
    // }
    
    // @JsonProperty("longitude")
    // public Double getLongitude() {
    // return longitude;
    // }
    
    // @JsonProperty("longitude")
    // public void setLongitude(Double longitude) {
    // this.longitude = longitude;
    // }
    
    // @JsonProperty("opensAt")
    // public String getOpensAt() {
    // return opensAt;
    // }
    
    // @JsonProperty("opensAt")
    // public void setOpensAt(String opensAt) {
    // this.opensAt = opensAt;
    // }
    
    // @JsonProperty("closesAt")
    // public String getClosesAt() {
    // return closesAt;
    // }
    
    // @JsonProperty("closesAt")
    // public void setClosesAt(String closesAt) {
    // this.closesAt = closesAt;
    // }
    
    // @JsonProperty("attributes")
    // public List<String> getAttributes() {
    // return attributes;
    // }
    
    // @JsonProperty("attributes")
    // public void setAttributes(List<String> attributes) {
    // this.attributes = attributes;
    // }
    
    // @JsonAnyGetter
    // public Map<String, Object> getAdditionalProperties() {
    // return this.additionalProperties;
    // }
    
    // @JsonAnySetter
    // public void setAdditionalProperty(String name, Object value) {
    // this.additionalProperties.put(name, value);
    // }


}

