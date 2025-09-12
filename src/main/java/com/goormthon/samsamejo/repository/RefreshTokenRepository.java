package com.goormthon.samsamejo.repository;

import com.goormthon.samsamejo.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
