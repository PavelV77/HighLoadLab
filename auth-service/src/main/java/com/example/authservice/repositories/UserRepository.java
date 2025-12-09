package com.example.authservice.repositories;

import com.example.authservice.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}



