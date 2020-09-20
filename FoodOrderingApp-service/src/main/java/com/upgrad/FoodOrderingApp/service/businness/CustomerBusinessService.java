package com.upgrad.FoodOrderingApp.service.businness;

import java.time.ZonedDateTime;
import java.util.List;


import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomerBusinessService {
	
	@Autowired
    private CustomerDao customerDao;
	
	@Autowired
    private PasswordCryptographyProvider cryptographyProvider;
	
	@Transactional
    public CustomerAuthTokenEntity authenticate(final String contactNumber , final String password) throws AuthenticationFailedException {
      
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);
        if(customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

       
        final String encryptedPassword = cryptographyProvider.encrypt(password, customerEntity.getSalt());
       
        if(encryptedPassword.equals(customerEntity.getPassword())) {
          
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
           
            CustomerAuthTokenEntity customerAuthTokenEntity = new CustomerAuthTokenEntity();
            customerAuthTokenEntity.setCustomer(customerEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(customerAuthTokenEntity.getUuid(), now, expiresAt));

            customerAuthTokenEntity.setLoginAt(now);
            customerAuthTokenEntity.setExpiresAt(expiresAt);
            customerAuthTokenEntity.setUuid(customerEntity.getUuid());

           
            customerDao.createAuthToken(customerAuthTokenEntity);

          
            customerDao.updateCustomer(customerEntity);
            return customerAuthTokenEntity;

        }
        else{
            //throw exception
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String signOut(final String authorization) throws AuthenticationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(authorization);
        if (customerAuthTokenEntity == null) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthenticationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now()))
        {
            throw new AuthenticationFailedException("ATHR-003" , "Your session is expired. Log in again to access this endpoint.");

        }
        final ZonedDateTime now = ZonedDateTime.now();
        customerAuthTokenEntity.setLogoutAt(now);
        customerDao.setCustomerLogout(customerAuthTokenEntity);
        return  customerAuthTokenEntity.getCustomer().getUuid();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllSavedAddress(final String authorization) throws AuthenticationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(authorization);
        if (customerAuthTokenEntity == null) {
            throw new AuthenticationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthenticationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now()))
        {
            throw new AuthenticationFailedException("ATHR-003" , "Your session is expired. Log in again to access this endpoint.");

        }
        return  customerDao.getAllSavedAddress();
    }



}
