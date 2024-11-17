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

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping()
    public ResponseEntity<List<CashCard>> findAll(){
        return ResponseEntity.ok(cashCardRepository.findAll());
    }

    @GetMapping("/{id}")
    private ResponseEntity<CashCard> findById(@PathVariable Long id) {
        Optional<CashCard> optCashCard = getCashCardById(id);
        return optCashCard.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Optional<CashCard> getCashCardById(Long id) {
        return cashCardRepository.findById(id);
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

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId,
                                             @RequestBody CashCardDTO cashCardUpdateDTO) {
        Optional<CashCard> cashCard = getCashCardById(requestedId);
        if (cashCard.isPresent()) {
            CashCard updatedCashCard = new CashCard(requestedId, cashCardUpdateDTO.getAmount(),
                    cashCardUpdateDTO.getOwner());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id) {

        Optional<CashCard> cashCard = getCashCardById(id);
        if (cashCard.isPresent()) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
