package com.umcsuser.current.services;

import com.umcsuser.current.db.HibernateConfig;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.impl.VehicleHibernateRepository;
import com.umcsuser.current.repositories.impl.RentalHibernateRepository;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class VehicleHibernateService implements VehicleServiceInterface {

    private final VehicleHibernateRepository vehicleRepo;
    private final RentalHibernateRepository rentalRepo;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepo, RentalHibernateRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return vehicleRepo.findAll().stream()
                    .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                    .toList();
        }
    }

    @Override
    public Vehicle findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            setSession(session);

            try {
                Vehicle savedVehicle = vehicleRepo.save(vehicle);

                tx.commit();
                return savedVehicle;
            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public void removeVehicle(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            setSession(session);

            try {
                boolean isRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
                if (isRented) {
                    throw new IllegalStateException("Cannot remove rented vehicle.");
                }

                vehicleRepo.deleteById(vehicleId);
                tx.commit();

            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        vehicleRepo.setSession(session);
        rentalRepo.setSession(session);
    }
}