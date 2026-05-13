package lostandfound.dao;

import lostandfound.model.Claim;
import lostandfound.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaimDAO {

    public boolean submitClaim(int itemId, int userId, String proof) {
        if (hasPending(itemId, userId)) return false;
        String sql = "INSERT INTO claims (item_id,claimed_by,proof_details,status) VALUES (?,?,?,'pending')";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, itemId); ps.setInt(2, userId); ps.setString(3, proof);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[ClaimDAO] submitClaim: " + e.getMessage()); }
        return false;
    }

    public boolean approveClaim(int claimId, int itemId, int adminId, String notes) {
        Connection c = DBConnection.getConnection();
        try {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE claims SET status='approved',admin_notes=?,reviewed_by=?,date_reviewed=NOW() WHERE id=?")) {
                ps.setString(1, notes); ps.setInt(2, adminId); ps.setInt(3, claimId); ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE items SET status='claimed' WHERE id=?")) {
                ps.setInt(1, itemId); ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE claims SET status='rejected',admin_notes='Another claim was approved for this item.',reviewed_by=?,date_reviewed=NOW() WHERE item_id=? AND id<>? AND status='pending'")) {
                ps.setInt(1, adminId); ps.setInt(2, itemId); ps.setInt(3, claimId); ps.executeUpdate();
            }
            c.commit(); return true;
        } catch (SQLException e) {
            try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("[ClaimDAO] approveClaim: " + e.getMessage());
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
        return false;
    }

    public boolean rejectClaim(int claimId, int adminId, String notes) {
        String sql = "UPDATE claims SET status='rejected',admin_notes=?,reviewed_by=?,date_reviewed=NOW() WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, notes); ps.setInt(2, adminId); ps.setInt(3, claimId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[ClaimDAO] rejectClaim: " + e.getMessage()); }
        return false;
    }

    public boolean completeClaim(int claimId, int itemId, int adminId) {
        Connection c = DBConnection.getConnection();
        try {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE claims SET status='completed',reviewed_by=?,date_reviewed=NOW() WHERE id=?")) {
                ps.setInt(1, adminId); ps.setInt(2, claimId); ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE items SET status='completed' WHERE id=?")) {
                ps.setInt(1, itemId); ps.executeUpdate();
            }
            c.commit(); return true;
        } catch (SQLException e) {
            try { c.rollback(); } catch (SQLException ignored) {}
            System.err.println("[ClaimDAO] completeClaim: " + e.getMessage());
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignored) {}
        }
        return false;
    }

    public List<Claim> getPending()  { return query(base("WHERE c.status='pending' ORDER BY c.date_claimed DESC"), null); }
    public List<Claim> getAll()      { return query(base("ORDER BY c.date_claimed DESC"), null); }
    public List<Claim> getByUser(int uid) { return query(base("WHERE c.claimed_by=? ORDER BY c.date_claimed DESC"), uid); }

    public int countPending() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM claims WHERE status='pending'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    private boolean hasPending(int itemId, int userId) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id FROM claims WHERE item_id=? AND claimed_by=? AND status='pending'")) {
            ps.setInt(1, itemId); ps.setInt(2, userId); return ps.executeQuery().next();
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    private String base(String whereOrder) {
        return "SELECT c.*,i.item_name," +
               "u.full_name claimer_name,u.student_id claimer_sid," +
               "u.college claimer_college,u.program claimer_program," +
               "u.year_level claimer_year,u.section claimer_section," +
               "a.full_name reviewer_name " +
               "FROM claims c JOIN items i ON c.item_id=i.id " +
               "JOIN users u ON c.claimed_by=u.id " +
               "LEFT JOIN users a ON c.reviewed_by=a.id " + whereOrder;
    }

    private List<Claim> query(String sql, Integer param) {
        List<Claim> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[ClaimDAO] query: " + e.getMessage()); }
        return list;
    }

    private Claim map(ResultSet rs) throws SQLException {
        Claim cl = new Claim();
        cl.setId(rs.getInt("id"));
        cl.setItemId(rs.getInt("item_id")); cl.setItemName(rs.getString("item_name"));
        cl.setClaimedBy(rs.getInt("claimed_by")); cl.setClaimerName(rs.getString("claimer_name"));
        cl.setClaimerStudentId(rs.getString("claimer_sid"));
        cl.setClaimerCollege(rs.getString("claimer_college")); cl.setClaimerProgram(rs.getString("claimer_program"));
        int yr = rs.getInt("claimer_year"); String sec = rs.getString("claimer_section");
        cl.setClaimerYearSection(yr > 0 ? yr + "-" + sec : "");
        cl.setProofDetails(rs.getString("proof_details")); cl.setStatus(rs.getString("status"));
        cl.setAdminNotes(rs.getString("admin_notes")); cl.setReviewedBy(rs.getInt("reviewed_by"));
        cl.setReviewerName(rs.getString("reviewer_name"));
        cl.setDateClaimed(rs.getTimestamp("date_claimed")); cl.setDateReviewed(rs.getTimestamp("date_reviewed"));
        return cl;
    }

    public List<Claim> getClaimsByUser(int id) {
        return query(base("WHERE c.claimed_by=? ORDER BY c.date_claimed DESC"), id);
    }

    public List<Claim> getPendingClaims() {
        return query(base("WHERE c.status='pending' ORDER BY c.date_claimed DESC"), null);
    }

    public List<Claim> getAllClaims() {
        return query(base("ORDER BY c.date_claimed DESC"), null);
    }
}