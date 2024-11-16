package com.familycashcard.cashcard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CashCardController {

    @GetMapping("/cashcards")
    public List<CashCard> cashcards(){
        return List.of(
                new CashCard(1L, 11.2, "Mark"),
                new CashCard(2L, 22.1, "Jimmy")
        );
    }
}
