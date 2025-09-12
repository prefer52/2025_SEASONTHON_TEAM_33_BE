package com.goormthon.samsamejo.repository;

import com.goormthon.samsamejo.domain.Users;
import com.goormthon.samsamejo.domain.type.EProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByOauthIdAndOauthProvider(String oauthId, EProvider oauthProvider);
}
