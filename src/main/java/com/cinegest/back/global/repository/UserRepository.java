package com.cinegest.back.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinegest.back.global.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndTypeAndOriginID(String email, String type, Integer originID);
}
