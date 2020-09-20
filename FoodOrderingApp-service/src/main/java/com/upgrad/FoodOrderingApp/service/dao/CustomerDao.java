package com.upgrad.FoodOrderingApp.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;

@Repository
public class CustomerDao {
	
	@PersistenceContext
    private EntityManager entityManager;

	// Gets the customer details from the database based on contact number
    public CustomerEntity getCustomerByContactNumber(final String customerContactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class).setParameter("contactNumber", customerContactNumber)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
    
 // Creates auth token by persisting the record in the database
    public CustomerAuthTokenEntity createAuthToken(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }
    
 // Updates the customer details to the database
    public void updateCustomer(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }
}
