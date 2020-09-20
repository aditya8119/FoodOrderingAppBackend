package com.upgrad.FoodOrderingApp.api.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;

@RestController
@RequestMapping("/")
public class CustomerController {
	
	@Autowired
    private CustomerBusinessService customerBusinessService;

	/**
	 * This end point is used for customer authentication. 
	 * Customer authenticates in the application and after successful authentication, JWT token is given to a customer.
	 * @parameters authorisation header
	 * @return LoginResponse
	 */
	
	
	
	//To-DO - Exceptions not getting displayed
	@RequestMapping(method = RequestMethod.POST , 
				path="/customer/login" ,
				produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) 
		throws AuthenticationFailedException {
		
		
		 //Check if authorisation header starts with Basic
        if (authorization == null || !authorization.startsWith("Basic ")) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        CustomerAuthTokenEntity customerAuthToken = customerBusinessService.authenticate(decodedArray[0] , decodedArray[1]);
        CustomerEntity customer = customerAuthToken.getCustomer();
        
        //Saving the customer details in db
        LoginResponse loginResponse = new LoginResponse().id(customer.getUuid()).firstName(customer.getFirstName())
                .lastName(customer.getLastName()).emailAddress(customer.getEmail()).contactNumber(customer.getContactNumber())
                .message("LOGGED IN SUCCESSFULLY");	
        
        //Returning the access token
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthToken.getAccessToken());
        headers.add("access-control-expose-headers", "access-token");
        
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
		
	}
}
