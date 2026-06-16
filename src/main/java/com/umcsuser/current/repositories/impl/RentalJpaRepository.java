package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.Rental;
import com.umcsuser.current.repositories.RentalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class RentalJpaRepository implements RentalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Rental> findAll() {
        return entityManager.createQuery("FROM Rental", Rental.class).getResultList();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(entityManager.find(Rental.class, id));
    }

    @Override
    public Rental save(Rental rental) {
        return entityManager.merge(rental);
    }

    @Override
    public void deleteById(String id) {
        Rental rental = entityManager.find(Rental.class, id);
        if (rental != null) {
            entityManager.remove(rental);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        TypedQuery<Rental> query = entityManager.createQuery("""
                FROM Rental r
                WHERE r.vehicle.id = :vehicleId
                AND r.returnDateTime IS NULL
                """, Rental.class);

        query.setParameter("vehicleId", vehicleId);

        return query.getResultStream().findFirst();
    }
}