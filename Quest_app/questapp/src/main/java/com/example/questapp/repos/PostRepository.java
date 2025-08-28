package com.example.questapp.repos;

import com.example.questapp.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>{

    List<Post> findByUserIdOrderByCreateDateDesc(Long userId); // üste getirmek için girilen kod
    @Query(value = "select id from post where user_id = ?1 order by create_date desc limit 5" , nativeQuery = true)
    List<Long> findTopByUserId(@Param("userId") Long userId);

    List<Post> findByUserId(Long aLong);

    List<Post> findAllByOrderByCreateDateDesc();
}
