package com.shoppersapp.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.factory.InterestIssueFactory;

/**
 * Represents an interest issue in the system.
 * The interest issue is a special class. The entire repository functionality is
 * handled in this service.
 */
@Deprecated
public class InterestIssueService {
    // Constant for the current interest rate offered by the bank.
    private static final BigDecimal INTEREST_RATE = new BigDecimal(5.0);
    private Connection connection;

    public InterestIssueService(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Database connection cannot be null.");
        }
        this.connection = connection;
    }

    /**
     * Creates a new interest issue.
     * Uses the constant defined in this class for the bank's current interest rate.
     *
     * @return the new interest issue object.
     * @throws SQLException if a database access error occurs.
     */
    public InterestIssue createInterestIssue() throws SQLException {
        InterestIssue newIssue = InterestIssueFactory.createInterestIssue(INTEREST_RATE);
        return updateInterestRate(newIssue);
    }

    /**
     * Do not use this method. Use the repository.
     */
    @Deprecated
    private InterestIssue updateInterestRate(InterestIssue interestIssue) throws SQLException {
        String sql = "INSERT INTO interestissues (interest_rate) VALUES (?) RETURNING interest_issue_id, interest_rate";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, interestIssue.getInterestRate());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("interest_issue_id");
                    BigDecimal rate = rs.getBigDecimal("interest_rate");
                    System.out.println("Inserted InterestIssue: ID = " + id + ", Rate = " + rate);
                    return InterestIssueFactory.createInterestIssue(id, rate);
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL Error during interest issue update: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * * Do not use this method. Use the repository.
     */
    @Deprecated
    public InterestIssue getLatestInterestIssue() throws SQLException { // No longer static
        String sql = "SELECT interest_issue_id, interest_rate FROM interestissues ORDER BY interest_issue_id DESC LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Integer id = rs.getInt("interest_issue_id");
                BigDecimal rate = rs.getBigDecimal("interest_rate");
                return InterestIssueFactory.createInterestIssue(id, rate);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error getting latest interest issue: " + e.getMessage());
            throw e;
        }
        return null;
    }
}
