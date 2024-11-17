package com.familycashcard.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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

    @GetMapping("/getcards/{owner}")
    private ResponseEntity<List<CashCard>> findByOwner(@PathVariable String owner) {
        List<CashCard> allCards = cashCardRepository.findAllByOwner(owner);
        return ResponseEntity.ok(allCards);
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCardDTO cashCardDTO, UriComponentsBuilder ucb) {
        CashCard cashCard = new CashCard(cashCardDTO.getAmount(), cashCardDTO.getOwner());
        CashCard savedCashCard = cashCardRepository.save(cashCard);
        URI locationSavedCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.getId())
                .toUri();

        return ResponseEntity.created(locationSavedCashCard).build(); //return 201 and location
    }
}
