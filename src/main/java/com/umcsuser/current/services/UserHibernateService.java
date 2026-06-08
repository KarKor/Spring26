package com.umcsuser.current.services;

import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.impl.UserHibernateRepository;
import com.umcsuser.current.repositories.impl.RentalHibernateRepository;
import com.umcsuser.current.db.HibernateConfig;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserHibernateService implements UserServiceInterface {

    private final UserHibernateRepository userRepo;
    private final RentalHibernateRepository rentalRepo;

    public UserHibernateService(UserHibernateRepository userRepo, RentalHibernateRepository rentalRepo) {
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<User> findAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return userRepo.findAll();
        }
    }

    @Override
    public User findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return userRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User with provided id not found."));
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            setSession(session);

            try {
                if (id.equals(loggedUserId)) {
                    throw new IllegalStateException("You cannot remove your own account.");
                }

                boolean hasActiveRentals = rentalRepo.findAll().stream()
                        .anyMatch(r -> id.equals(r.getUserId()) && r.isActive());

                if (hasActiveRentals) {
                    throw new IllegalStateException("Cannot remove user with unreturned rentals.");
                }

                userRepo.deleteById(id);
                tx.commit();

            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    private void setSession(Session session) {
        userRepo.setSession(session);
        rentalRepo.setSession(session);
    }
}