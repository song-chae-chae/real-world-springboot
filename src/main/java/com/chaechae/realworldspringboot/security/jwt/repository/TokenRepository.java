package com.chaechae.realworldspringboot.security.jwt.repository;

import com.chaechae.realworldspringboot.security.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
