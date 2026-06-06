package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CardTransaction extends Transaction {
    private final String cardId;
    private final String merchantId;
    private final boolean online;

    public CardTransaction(String id, String accountId, BigDecimal amount, Location location,
                           String cardId, String merchantId, boolean online) {
        super(id, accountId, amount, location);
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.online = online;
    }

    public CardTransaction(String id, String accountId, BigDecimal amount, Location location,
                           LocalDateTime timestamp,
                           String cardId, String merchantId, boolean online) {
        super(id, accountId, amount, location, timestamp);
        this.cardId = cardId;
        this.merchantId = merchantId;
        this.online = online;
    }

    public String getCardId() { return cardId; }
    public String getMerchantId() { return merchantId; }
    public boolean isOnline() { return online; }

    @Override
    public String getType() {
        return "CARD";
    }
}