package com.example.electronics_store.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.electronics_store.Model.ReviewReaction;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {

    Optional<ReviewReaction> findByReviewIdAndActorKeyAndReactionType(
            Long reviewId, String actorKey, String reactionType);

    long countByReviewIdAndReactionType(Long reviewId, String reactionType);
}