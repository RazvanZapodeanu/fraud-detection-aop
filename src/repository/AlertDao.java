package repository;

import exception.EntityNotFoundException;
import model.Alert;
import model.AlertStatus;
import model.Severity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AlertDao extends GenericDao<Alert> {

    private static AlertDao instance;

    private AlertDao() {
        super();
    }

    public static synchronized AlertDao getInstance() {
        if (instance == null) {
            instance = new AlertDao();
        }
        return instance;
    }

    @Override
    public void insert(Alert alert) {
        String sql = """
                INSERT INTO alerts
                (id, transaction_id, account_id, rule_id, severity, message, timestamp, status,
                 assigned_analyst_id, resolved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, alert.getId());
            stmt.setString(2, alert.getTransactionId());
            stmt.setString(3, alert.getAccountId());
            stmt.setString(4, alert.getRuleId());
            stmt.setString(5, alert.getSeverity().name());
            stmt.setString(6, alert.getMessage());
            stmt.setTimestamp(7, Timestamp.valueOf(alert.getTimestamp()));
            stmt.setString(8, alert.getStatus().name());

            if (alert.getAssignedAnalystId() != null) {
                stmt.setString(9, alert.getAssignedAnalystId());
            } else {
                stmt.setNull(9, Types.VARCHAR);
            }

            if (alert.getResolvedAt() != null) {
                stmt.setTimestamp(10, Timestamp.valueOf(alert.getResolvedAt()));
            } else {
                stmt.setNull(10, Types.TIMESTAMP);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert alert", e);
        }
    }

    @Override
    public Alert findById(String id) {
        String sql = "SELECT * FROM alerts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntityNotFoundException("Alert not found: " + id);
                }
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find alert", e);
        }
    }

    @Override
    public List<Alert> findAll() {
        String sql = """
        SELECT * FROM alerts
        ORDER BY
            CASE severity
                WHEN 'CRITICAL' THEN 1
                WHEN 'HIGH'     THEN 2
                WHEN 'MEDIUM'   THEN 3
                WHEN 'LOW'      THEN 4
            END,
            timestamp DESC
        """;
        List<Alert> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list alerts", e);
        }
        return result;
    }

    public List<Alert> findByAccount(String accountId) {
        String sql = "SELECT * FROM alerts WHERE account_id = ? ORDER BY timestamp DESC";
        List<Alert> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query alerts by account", e);
        }
        return result;
    }

    @Override
    public void update(Alert alert) {
        String sql = "UPDATE alerts SET status = ?, assigned_analyst_id = ?, resolved_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, alert.getStatus().name());

            if (alert.getAssignedAnalystId() != null) {
                stmt.setString(2, alert.getAssignedAnalystId());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            if (alert.getResolvedAt() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(alert.getResolvedAt()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setString(4, alert.getId());
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new EntityNotFoundException("Alert not found: " + alert.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update alert", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM alerts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new EntityNotFoundException("Alert not found: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete alert", e);
        }
    }

    private Alert mapRow(ResultSet rs) throws SQLException {
        Timestamp resolvedTimestamp = rs.getTimestamp("resolved_at");
        return new Alert(
                rs.getString("id"),
                rs.getString("transaction_id"),
                rs.getString("account_id"),
                rs.getString("rule_id"),
                Severity.valueOf(rs.getString("severity")),
                rs.getString("message"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                AlertStatus.valueOf(rs.getString("status")),
                rs.getString("assigned_analyst_id"),
                resolvedTimestamp == null ? null : resolvedTimestamp.toLocalDateTime()
        );
    }
}