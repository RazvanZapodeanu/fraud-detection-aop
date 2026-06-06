package repository;

import exception.EntityNotFoundException;
import model.AmountRule;
import model.FrequencyRule;
import model.LocationRule;
import model.Rule;
import model.Severity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuleDao extends GenericDao<Rule> {

    private static RuleDao instance;

    private RuleDao() {
        super();
    }

    public static synchronized RuleDao getInstance() {
        if (instance == null) {
            instance = new RuleDao();
        }
        return instance;
    }

    @Override
    public void insert(Rule rule) {
        String sql = """
                INSERT INTO rules
                (id, name, rule_type, severity, enabled, threshold, max_transactions, window_minutes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rule.getId());
            stmt.setString(2, rule.getName());
            stmt.setString(3, ruleType(rule));
            stmt.setString(4, rule.getSeverity().name());
            stmt.setBoolean(5, rule.isEnabled());

            if (rule instanceof AmountRule ar) {
                stmt.setBigDecimal(6, ar.getThreshold());
                stmt.setNull(7, java.sql.Types.INTEGER);
                stmt.setNull(8, java.sql.Types.INTEGER);
            } else if (rule instanceof FrequencyRule fr) {
                stmt.setNull(6, java.sql.Types.NUMERIC);
                stmt.setInt(7, fr.getMaxTransactions());
                stmt.setInt(8, fr.getWindowMinutes());
            } else if (rule instanceof LocationRule lr) {
                stmt.setNull(6, java.sql.Types.NUMERIC);
                stmt.setNull(7, java.sql.Types.INTEGER);
                stmt.setInt(8, lr.getImpossibleTravelMinutes());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert rule", e);
        }
    }

    @Override
    public Rule findById(String id) {
        String sql = "SELECT * FROM rules WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntityNotFoundException("Rule not found: " + id);
                }
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find rule", e);
        }
    }

    @Override
    public List<Rule> findAll() {
        String sql = "SELECT * FROM rules ORDER BY id";
        List<Rule> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list rules", e);
        }
        return result;
    }

    @Override
    public void update(Rule rule) {
        String sql = "UPDATE rules SET name = ?, severity = ?, enabled = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rule.getName());
            stmt.setString(2, rule.getSeverity().name());
            stmt.setBoolean(3, rule.isEnabled());
            stmt.setString(4, rule.getId());
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new EntityNotFoundException("Rule not found: " + rule.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update rule", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM rules WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new EntityNotFoundException("Rule not found: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete rule", e);
        }
    }

    private String ruleType(Rule rule) {
        if (rule instanceof AmountRule) return "AMOUNT";
        if (rule instanceof FrequencyRule) return "FREQUENCY";
        if (rule instanceof LocationRule) return "LOCATION";
        throw new IllegalArgumentException("Unknown rule type: " + rule.getClass().getSimpleName());
    }

    private Rule mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String type = rs.getString("rule_type");
        Severity severity = Severity.valueOf(rs.getString("severity"));
        boolean enabled = rs.getBoolean("enabled");

        Rule rule = switch (type) {
            case "AMOUNT" -> new AmountRule(id, name, severity, rs.getBigDecimal("threshold"));
            case "FREQUENCY" -> new FrequencyRule(id, name, severity,
                    rs.getInt("max_transactions"), rs.getInt("window_minutes"));
            case "LOCATION" -> new LocationRule(id, name, severity, rs.getInt("window_minutes"));
            default -> throw new IllegalStateException("Unknown rule type: " + type);
        };

        if (!enabled) rule.disable();
        return rule;
    }
}