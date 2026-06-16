package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.VehicleRepository;
import org.hibernate.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Profile("jpa")
public class VehicleHibernateRepository implements VehicleRepository {

    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Vehicle> findAll() {
        return session.createQuery("FROM Vehicle", Vehicle.class).list();
    }

    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(session.get(Vehicle.class, id));
    }

    public Vehicle save(Vehicle vehicle) {
        return session.merge(vehicle);
    }

    public void deleteById(String id) {
        Vehicle vehicle = session.get(Vehicle.class, id);

        if (vehicle != null) {
            session.remove(vehicle);
        }
    }
}