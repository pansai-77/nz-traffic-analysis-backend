package io.github.pansai.traffic.dao;

import io.github.pansai.traffic.entity.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationTokenRepo extends JpaRepository<UserActivationToken, Long> {

    UserActivationToken findByTokenInfo(String tokenInfo);

    UserActivationToken findByUserId(Long userId);
}
