package com.familycashcard.cashcard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashCardRepository extends JpaRepository<CashCard, Long> {
    List<CashCard> findAllByOwner(String owner);
}
