package com.familycashcard.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping()
    public ResponseEntity<List<CashCard>> findAll(){
        return ResponseEntity.ok(cashCardRepository.findAll());
    }

    @GetMapping("/{id}")
    private ResponseEntity<CashCard> findById(@PathVariable Long id) {
        Optional<CashCard> optCashCard = cashCardRepository.findById(id);
        return optCashCard.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
