package com.sparklab.TAM.repositories;

import com.sparklab.TAM.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation,Long> {
    Optional<Conversation> findByReservationId(int reservationId);
}
