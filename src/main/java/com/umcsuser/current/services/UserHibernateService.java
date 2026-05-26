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
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym id."));
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (id.equals(loggedUserId)) {
                throw new IllegalStateException("Nie możesz usunąć własnego konta.");
            }

            boolean hasActiveRentals = rentalRepo.findAll().stream()
                    .anyMatch(r -> id.equals(r.getUserId()) && r.isActive());

            if (hasActiveRentals) {
                throw new IllegalStateException("Nie można usunąć użytkownika, który posiada niezwrócone pojazdy.");
            }

            userRepo.deleteById(id);
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    private void setSession(Session session) {
        userRepo.setSession(session);
        rentalRepo.setSession(session);
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}