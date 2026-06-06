package repository;

import exception.EntityNotFoundException;
import model.CardTransaction;
import model.Location;
import model.Transaction;
import model.TransferTransaction;
import model.WithdrawalTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao extends GenericDao<Transaction> {

    private static TransactionDao instance;

    private TransactionDao() {
        super();
    }

    public static synchronized TransactionDao getInstance() {
        if (instance == null) {
            instance = new TransactionDao();
        }
        return instance;
    }

    @Override
    public void insert(Transaction tx) {
        String sql = """
                INSERT INTO transactions
                (id, account_id, transaction_type, amount, timestamp, country, city,
                 card_id, merchant_id, is_online, destination_account_id, atm_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tx.getId());
            stmt.setString(2, tx.getAccountId());
            stmt.setString(3, tx.getType());
            stmt.setBigDecimal(4, tx.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(tx.getTimestamp()));
            stmt.setString(6, tx.getLocation().getCountry());
            stmt.setString(7, tx.getLocation().getCity());

            if (tx instanceof CardTransaction ct) {
                stmt.setString(8, ct.getCardId());
                stmt.setString(9, ct.getMerchantId());
                stmt.setBoolean(10, ct.isOnline());
                stmt.setNull(11, java.sql.Types.VARCHAR);
                stmt.setNull(12, java.sql.Types.VARCHAR);
            } else if (tx instanceof TransferTransaction tt) {
                stmt.setNull(8, java.sql.Types.VARCHAR);
                stmt.setNull(9, java.sql.Types.VARCHAR);
                stmt.setNull(10, java.sql.Types.BOOLEAN);
                stmt.setString(11, tt.getDestinationAccountId());
                stmt.setNull(12, java.sql.Types.VARCHAR);
            } else if (tx instanceof WithdrawalTransaction wt) {
                stmt.setNull(8, java.sql.Types.VARCHAR);
                stmt.setNull(9, java.sql.Types.VARCHAR);
                stmt.setNull(10, java.sql.Types.BOOLEAN);
                stmt.setNull(11, java.sql.Types.VARCHAR);
                stmt.setString(12, wt.getAtmId());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert transaction", e);
        }
    }

    @Override
    public Transaction findById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntityNotFoundException("Transaction not found: " + id);
                }
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction", e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY timestamp";
        List<Transaction> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list transactions", e);
        }
        return result;
    }

    public List<Transaction> findByAccount(String accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp";
        List<Transaction> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query transactions by account", e);
        }
        return result;
    }

    @Override
    public void update(Transaction tx) {
        throw new UnsupportedOperationException("Transactions are immutable historical events");
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new EntityNotFoundException("Transaction not found: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete transaction", e);
        }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String accountId = rs.getString("account_id");
        String type = rs.getString("transaction_type");
        BigDecimal amount = rs.getBigDecimal("amount");
        Location location = new Location(rs.getString("country"), rs.getString("city"));
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

        return switch (type) {
            case "CARD" -> new CardTransaction(
                    id, accountId, amount, location, timestamp,
                    rs.getString("card_id"), rs.getString("merchant_id"), rs.getBoolean("is_online"));
            case "TRANSFER" -> new TransferTransaction(
                    id, accountId, amount, location, timestamp,
                    rs.getString("destination_account_id"));
            case "WITHDRAWAL" -> new WithdrawalTransaction(
                    id, accountId, amount, location, timestamp,
                    rs.getString("atm_id"));
            default -> throw new IllegalStateException("Unknown transaction type: " + type);
        };
    }
}