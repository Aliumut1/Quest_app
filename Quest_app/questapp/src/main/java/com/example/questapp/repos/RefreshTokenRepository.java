package com.example.questapp.repos;

import com.example.questapp.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

    RefreshToken findByUserId(Long userId);

    @Transactional
    void deleteByUser_Id(Long userId);
}