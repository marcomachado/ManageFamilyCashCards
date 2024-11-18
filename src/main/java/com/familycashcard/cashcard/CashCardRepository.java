package com.familycashcard.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CashCardRepository extends CrudRepository<CashCard, Long> {
    Page<CashCard> findAllByOwner(String owner, Pageable pageable);
}
