package com.chaechae.realworldspringboot.profile.repository;

import com.chaechae.realworldspringboot.profile.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    List<Follow> findByFollowerId(Long followerId);
}
