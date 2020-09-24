package com.upgrad.FoodOrderingApp.service.entity;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "customer_address")
@NamedQueries(
    {
        @NamedQuery(name = "customerAddressByAddressId", query = "select ca from CustomerAddressEntity ca where ca.id=:id"),
        @NamedQuery(name = "custAddressByCustIdAddressId", query = "select ca from CustomerAddressEntity ca where ca.customer=:customer and ca.address=:address"),
        @NamedQuery(name = "customerAddressesListByCustomerId", query = "select ca from CustomerAddressEntity ca where ca.customer = :customer")
    }
)

public class CustomerAddressEntity implements Serializable {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "CUSTOMER_ID")
  private CustomerEntity customer;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "ADDRESS_ID")
  private AddressEntity address;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public CustomerEntity getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerEntity customer) {
    this.customer = customer;
  }

  public AddressEntity getAddress() {
    return address;
  }

  public void setAddress(AddressEntity address) {
    this.address = address;
  }

}
