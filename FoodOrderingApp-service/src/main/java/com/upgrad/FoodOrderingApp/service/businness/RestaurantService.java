package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;
    private RestaurantDetailsResponseAddress Res;

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantListResponse getAllRestaurants()  {

        List<RestaurantCategoryEntity> restaurantCategoryEntityList= restaurantDao.getAllRestaurants();
        RestaurantListResponse restaurantList=new RestaurantListResponse();
        int k=0;
        String s="";
        RestaurantList resList = null;
        List<RestaurantList> resJson=new ArrayList<>();
        for(RestaurantCategoryEntity restaurantCategory: restaurantCategoryEntityList)
        {
            RestaurantEntity resEntity=restaurantCategory.getRestaurantEntity();
            CategoryEntity categoryEntity=restaurantCategory.getCategoryEntity();

            if(s.equals("") || resEntity.getId()!=k) {
                s="";
                if(resList!=null){
                    resJson.add(resList);
                }
                resList=new RestaurantList();
                RestaurantDetailsResponseAddress resAddress = new RestaurantDetailsResponseAddress();
                AddressEntity addressEntity = resEntity.getAddressEntiy();
                resAddress.setCity(addressEntity.getCity());
                resAddress.setFlatBuildingName(addressEntity.getFlat_buil_number());
                resAddress.setId(UUID.fromString(addressEntity.getUuid()));
                resAddress.setLocality(addressEntity.getLocality());
                resAddress.setPincode(addressEntity.getPincode());
                RestaurantDetailsResponseAddressState restaurantState = new RestaurantDetailsResponseAddressState();
                StateEntity state = addressEntity.getState();
                restaurantState.setId(UUID.fromString(state.getUuid()));
                restaurantState.setStateName(state.getState_name());
                resAddress.setState(restaurantState);
                resList.setId(UUID.fromString(resEntity.getUuid()));
                resList.setAddress(resAddress);
                resList.setAveragePrice(resEntity.getAveragePrice());
                resList.setCustomerRating(resEntity.getCustomerRating());
                resList.setNumberCustomersRated(resEntity.getNumberOfCustomersRated());
                resList.setPhotoURL(resEntity.getPhotoUrl());
                resList.setRestaurantName(resEntity.getRestaurantName());
                s+=categoryEntity.getCategoryName()+",";
                k=resEntity.getId();
            }
            else{
                s+=categoryEntity.getCategoryName()+",";
                k=resEntity.getId();
            }
            resList.setCategories(s.substring(0,s.length()-1));

        }
        if(resList!=null)
        {
            resJson.add(resList);
        }
        restaurantList.setRestaurants(resJson);
        return restaurantList;

    }
}
