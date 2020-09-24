package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;

@RestController
@RequestMapping("/")
public class AddressController {

  @Autowired
  private AddressService addressService;


  //Api for saving address of customer

  @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader(value = "authorization", required = true) String authorization,
      @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
      throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

    // Splits the Bearer authorization text as Bearer and bearerToken
    String[] bearerToken = authorization.split("Bearer ");

    // Adds all the address attributes provided to the address entity
    AddressEntity addressEntity = new AddressEntity();
    addressEntity.setCity(saveAddressRequest.getCity());
    addressEntity.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
    addressEntity.setLocality(saveAddressRequest.getLocality());
    addressEntity.setPincode(saveAddressRequest.getPincode());
    addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
    addressEntity.setUuid(UUID.randomUUID().toString());
    addressEntity.setActive(1);

    // Calls the saveAddress method of address service with the provided attributes
    AddressEntity savedAddressEntity = addressService.saveAddress(addressEntity, bearerToken[1]);

    // Loads the SaveAddressResponse with the uuid of the new address created and the respective status message
    SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(savedAddressEntity.getUuid())
        .status("ADDRESS SUCCESSFULLY REGISTERED");

    // Returns the SaveAddressResponse with resource created http status
    return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
  }

}
