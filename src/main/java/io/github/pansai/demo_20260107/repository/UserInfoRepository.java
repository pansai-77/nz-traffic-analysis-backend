package io.github.pansai.demo_20260107.repository;

import io.github.pansai.demo_20260107.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    UserInfo findByEmail(String email);

    boolean existsByEmail(String email);
}
