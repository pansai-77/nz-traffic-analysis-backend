package io.github.pansai.traffic.dao;

import io.github.pansai.traffic.entity.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivationTokenRepo extends JpaRepository<UserActivationToken, Long> {

    UserActivationToken findByTokenInfo(String tokenInfo);

    @Query("select t from UserActivationToken t where t.expiresAt > CURRENT_TIMESTAMP and t.userId = :userId")
    UserActivationToken findValidTokenByUserId(Long userId);
}
