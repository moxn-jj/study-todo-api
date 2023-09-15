package com.mun.todo.repository;

import com.mun.todo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByKey(String key);

    Long deleteRefreshTokensByModifiedAtBefore(LocalDateTime oneMinutesAgo);

    void deleteRefreshTokensByKey(String key);

}
