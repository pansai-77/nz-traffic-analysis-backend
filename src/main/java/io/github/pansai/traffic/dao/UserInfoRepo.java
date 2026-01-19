package io.github.pansai.traffic.dao;

import io.github.pansai.traffic.entity.UserInfoEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfoEntity, Long> {

    boolean existsByUserEmail(String userEmail);

    UserInfoEntity findByUserEmail(String userEmail);

    UserInfoEntity findByUserId(Long userId);

}
