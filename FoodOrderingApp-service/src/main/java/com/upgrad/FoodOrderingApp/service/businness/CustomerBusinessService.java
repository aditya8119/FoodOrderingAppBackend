package com.upgrad.FoodOrderingApp.service.businness;

import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;

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
	

}
