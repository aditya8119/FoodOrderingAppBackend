package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get All Restaurant Details
    public List<RestaurantCategoryEntity> getAllRestaurants(){
        try {
            return entityManager.createNamedQuery("allRestaurants", RestaurantCategoryEntity.class).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }


}
