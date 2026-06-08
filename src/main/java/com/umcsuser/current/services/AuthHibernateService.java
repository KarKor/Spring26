package com.umcsuser.current.services;

import com.umcsuser.current.db.HibernateConfig;
import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {

    private final UserHibernateRepository userRepo;

    public AuthHibernateService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            userRepo.setSession(session);

            try {
                if (userRepo.findByLogin(login).isPresent()) {
                    return false;
                }

                Role assignedRole = userRepo.findAll().isEmpty() ? Role.ADMIN : Role.USER;
                String hashedPw = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

                User newUser = User.builder()
                        .id(UUID.randomUUID().toString())
                        .login(login)
                        .passwordHash(hashedPw)
                        .role(assignedRole)
                        .build();

                userRepo.save(newUser);

                tx.commit();
                return true;

            } catch (RuntimeException e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);

            Optional<User> userOpt = userRepo.findByLogin(login);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
                    return Optional.of(user.copy());
                }
            }
            return Optional.empty();
        }
    }
}