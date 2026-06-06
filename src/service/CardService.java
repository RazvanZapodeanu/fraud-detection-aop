package service;

import exception.EntityNotFoundException;
import model.Account;
import model.Card;
import util.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CardService {
    private static final int DEFAULT_VALIDITY_YEARS = 3;

    private final Map<String, Card> cards = new HashMap<>();
    private final AccountService accountService;

    public CardService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Card issueCard(String accountId, String cardholderName) {
        Account account = accountService.findById(accountId);
        String cardId = IdGenerator.nextCardId();
        LocalDate expiry = LocalDate.now().plusYears(DEFAULT_VALIDITY_YEARS);
        Card card = new Card(cardId, account.getId(), cardholderName, expiry);
        cards.put(cardId, card);
        return card;
    }

    public void blockCard(String cardId) {
        findById(cardId).block();
    }

    public Card findById(String id) {
        Card card = cards.get(id);
        if (card == null) {
            throw new EntityNotFoundException("Card not found: " + id);
        }
        return card;
    }

    public Collection<Card> findAll() {
        return Collections.unmodifiableCollection(cards.values());
    }
}