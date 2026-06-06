package util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {
    private static final AtomicLong customerCounter = new AtomicLong(1000);
    private static final AtomicLong accountCounter = new AtomicLong(2000);
    private static final AtomicLong cardCounter = new AtomicLong(4000_0000_0000_0001L);
    private static final AtomicLong transactionCounter = new AtomicLong(1);
    private static final AtomicLong ruleCounter = new AtomicLong(1);
    private static final AtomicLong alertCounter = new AtomicLong(1);
    private static final AtomicLong merchantCounter = new AtomicLong(100);
    private static final AtomicLong analystCounter = new AtomicLong(1);

    private IdGenerator() {}

    public static String nextCustomerId()    { return "CUST-" + customerCounter.getAndIncrement(); }
    public static String nextAccountId()     { return "ACC-" + accountCounter.getAndIncrement(); }
    public static String nextCardId()        { return String.valueOf(cardCounter.getAndIncrement()); }
    public static String nextTransactionId() { return "TX-" + transactionCounter.getAndIncrement(); }
    public static String nextRuleId()        { return "RULE-" + ruleCounter.getAndIncrement(); }
    public static String nextAlertId()       { return "ALRT-" + alertCounter.getAndIncrement(); }
    public static String nextMerchantId()    { return "MRC-" + merchantCounter.getAndIncrement(); }
    public static String nextAnalystId()     { return "ANL-" + analystCounter.getAndIncrement(); }
}