package com.familycashcard.cashcard;

public class CashCardDTO {
    private Double amount;
    private String owner;

    public CashCardDTO(Double amount, String owner) {
        this.amount = amount;
        this.owner = owner;
    }

    public CashCard convertToCashCard(Double amount, String owner) {
        return new CashCard(amount, owner);
    }

    public Double getAmount() {
        return amount;
    }

    public String getOwner() {
        return owner;
    }
}
