package com.upgrad.FoodOrderingApp.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class CustomerDao {

  @PersistenceContext
  private EntityManager entityManager;

  // Gets the customer details from the database based on contact number
  public CustomerEntity getCustomerByContactNumber(final String customerContactNumber) {
    try {
      return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
          .setParameter("contactNumber", customerContactNumber)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  // Creates auth token by persisting the record in the database
  public CustomerAuthTokenEntity createAuthToken(
      final CustomerAuthTokenEntity customerAuthTokenEntity) {
    entityManager.persist(customerAuthTokenEntity);
    return customerAuthTokenEntity;
  }

  // Updates the customer details to the database
  public void updateCustomer(final CustomerEntity updatedCustomerEntity) {
    entityManager.merge(updatedCustomerEntity);
  }

  //Get Customer By AccessToken
  public CustomerAuthTokenEntity getCustomerAuthToken(final String accessToken) {
    try {
      return entityManager
          .createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class)
          .setParameter("accessToken", accessToken).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }

  }

  //To update user Log Out Time
  public void setCustomerLogout(final CustomerAuthTokenEntity customerAuthTokenEntity) {
    entityManager.merge(customerAuthTokenEntity);
    return;
  }

  public boolean userSignOutStatus(String authorizationToken) {
    CustomerAuthTokenEntity customerAuthTokenEntity = getCustomerAuthToken(authorizationToken);
    ZonedDateTime loggedOutStatus = customerAuthTokenEntity.getLogoutAt();
    ZonedDateTime loggedInStatus = customerAuthTokenEntity.getLoginAt();
    if (loggedOutStatus != null && loggedOutStatus.isAfter(loggedInStatus)) {
      return true;
    } else {
      return false;
    }
  }

  //To get All saved addresses
  public List<AddressEntity> getAllSavedAddress() {
    try {
      return entityManager.createNamedQuery("allSavedAddress", AddressEntity.class).getResultList();

    } catch (NoResultException nre) {
      return null;
    }
  }

  // Gets the customer details from the database based on uuid
  public CustomerEntity getCustomerByUuid(final String uuid) {
    try {
      return entityManager.createNamedQuery("customerByUuid", CustomerEntity.class)
          .setParameter("uuid", uuid)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }


}
