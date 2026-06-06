package model;

import java.time.LocalDate;

public class Card {
    private final String id;
    private final String accountId;
    private final String cardholderName;
    private final LocalDate expiryDate;
    private CardStatus status;

    public Card(String id, String accountId, String cardholderName, LocalDate expiryDate) {
        this.id = id;
        this.accountId = accountId;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.status = CardStatus.ACTIVE;
    }

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getCardholderName() { return cardholderName; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public CardStatus getStatus() { return status; }

    public void block() {
        this.status = CardStatus.BLOCKED;
    }

    public boolean isUsable() {
        return status == CardStatus.ACTIVE && expiryDate.isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Card " + maskNumber(id) + " [" + status + "]";
    }

    private String maskNumber(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}