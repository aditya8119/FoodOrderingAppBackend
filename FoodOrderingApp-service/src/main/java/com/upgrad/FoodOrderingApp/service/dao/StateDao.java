package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class StateDao {

  @PersistenceContext
  private EntityManager entityManager;

  public StateEntity getStateByUuid(final String stateUuid) {
    try {
      return entityManager.createNamedQuery("stateByUuid", StateEntity.class).setParameter("uuid", stateUuid)
          .getSingleResult();
    } catch(NoResultException nre) {
      return null;
    }
  }

  public StateEntity getStateById(final Integer stateId) {
    try {
      return entityManager.createNamedQuery("stateById", StateEntity.class).setParameter("id", stateId)
          .getSingleResult();
    } catch(NoResultException nre) {
      return null;
    }
  }
}
