package com.umcsuser.current.services;

import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.impl.UserHibernateRepository;
import com.umcsuser.current.db.HibernateConfig;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {

    private final UserHibernateRepository userRepo;

    public AuthHibernateService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register(String login, String rawPassword) {
        Transaction tx = null;

        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);

            if (userRepo.findByLogin(login).isPresent()) {
                return false;
            }

            User newUser = new User();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setLogin(login);
            newUser.setPasswordHash(rawPassword);

            userRepo.save(newUser);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);

            return userRepo.findByLogin(login)
                    .filter(user -> user.getPasswordHash().equals(rawPassword));
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}