package com.umcsuser.current.services;

import com.umcsuser.current.db.HibernateConfig;
import com.umcsuser.current.models.Rental;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.impl.RentalHibernateRepository;
import com.umcsuser.current.repositories.impl.UserHibernateRepository;
import com.umcsuser.current.repositories.impl.VehicleHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalHibernateService implements RentalServiceInterface {

    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public RentalHibernateService(RentalHibernateRepository rentalRepo,
                                  VehicleHibernateRepository vehicleRepo,
                                  UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            setSession(session);

            try {
                boolean userHasActiveRental = rentalRepo.findAll().stream()
                        .anyMatch(r -> userId.equals(r.getUserId()) && r.isActive());

                if (userHasActiveRental) {
                    throw new IllegalStateException("You already have an active rental.");
                }

                Vehicle vehicle = vehicleRepo.findById(vehicleId)
                        .orElseThrow(() -> new IllegalArgumentException("No vehicle matches the provided id."));

                User user = userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("No user matches the povided id."));

                boolean vehicleIsRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();

                if (vehicleIsRented) {
                    throw new IllegalStateException("This vehicle is already rented.");
                }

                Rental rental = Rental.builder()
                        .id(UUID.randomUUID().toString())
                        .vehicle(vehicle)
                        .user(user)
                        .rentDateTime(LocalDateTime.now().toString())
                        .returnDateTime(null)
                        .build();

                Rental savedRental = rentalRepo.save(rental);

                tx.commit();
                return savedRental;

            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public Rental returnVehicle(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            setSession(session);

            try {
                Rental rental = rentalRepo.findAll().stream()
                        .filter(r -> userId.equals(r.getUserId()))
                        .filter(Rental::isActive)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No vehicles rented."));

                rental.setReturnDateTime(LocalDateTime.now().toString());
                Rental savedRental = rentalRepo.save(rental);

                tx.commit();
                return savedRental;

            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public Optional<Rental> findActiveRentalByUserId(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return rentalRepo.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId()))
                    .filter(Rental::isActive)
                    .findFirst();
        }
    }

    @Override
    public List<Rental> findAllRentals() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findAll();
        }
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return rentalRepo.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId()))
                    .toList();
        }
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepo.setSession(session);
        vehicleRepo.setSession(session);
        userRepo.setSession(session);
    }
}