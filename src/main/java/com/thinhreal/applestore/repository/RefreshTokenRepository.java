package com.thinhreal.applestore.repository;

import com.thinhreal.applestore.model.entity.RefreshTokenEntity;
import com.thinhreal.applestore.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenIdAndRevokedFalse(String tokenId);

    void deleteByUser(UserEntity user);

    long deleteByExpiryDateBefore(Instant expiryDate);
}
