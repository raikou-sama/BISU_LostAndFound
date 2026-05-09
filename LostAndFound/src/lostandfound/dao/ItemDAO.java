package lostandfound.dao;

import lostandfound.model.Item;
import lostandfound.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (item_name,description,category,location,status,type,contact_info,reported_by) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getItemName()); ps.setString(2, item.getDescription());
            ps.setString(3, item.getCategory()); ps.setString(4, item.getLocation());
            ps.setString(5, "active");           ps.setString(6, item.getType());
            ps.setString(7, item.getContactInfo()); ps.setInt(8, item.getReportedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[ItemDAO] addItem: " + e.getMessage()); }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE items SET status=? WHERE id=?")) {
            ps.setString(1, status); ps.setInt(2, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[ItemDAO] updateStatus: " + e.getMessage()); }
        return false;
    }

    public boolean deleteItem(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM items WHERE id=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[ItemDAO] deleteItem: " + e.getMessage()); }
        return false;
    }

    public List<Item> getAll() {
        return query("SELECT i.*,u.full_name reporter FROM items i LEFT JOIN users u ON i.reported_by=u.id ORDER BY i.date_reported DESC", null, null);
    }

    public List<Item> getByType(String type) {
        return query("SELECT i.*,u.full_name reporter FROM items i LEFT JOIN users u ON i.reported_by=u.id WHERE i.type=? ORDER BY i.date_reported DESC", type, null);
    }

    public List<Item> getByUser(int userId) {
        return query("SELECT i.*,u.full_name reporter FROM items i LEFT JOIN users u ON i.reported_by=u.id WHERE i.reported_by=? ORDER BY i.date_reported DESC", null, userId);
    }

    public List<Item> getActiveFound() {
        return query("SELECT i.*,u.full_name reporter FROM items i LEFT JOIN users u ON i.reported_by=u.id WHERE i.type='found' AND i.status='active' ORDER BY i.date_reported DESC", null, null);
    }

    public List<Item> search(String kw) {
        String sql = "SELECT i.*,u.full_name reporter FROM items i LEFT JOIN users u ON i.reported_by=u.id WHERE i.item_name LIKE ? OR i.description LIKE ? OR i.category LIKE ? OR i.location LIKE ? ORDER BY i.date_reported DESC";
        List<Item> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String w = "%" + kw + "%";
            ps.setString(1,w); ps.setString(2,w); ps.setString(3,w); ps.setString(4,w);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[ItemDAO] search: " + e.getMessage()); }
        return list;
    }

    public int countByType(String type) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM items WHERE type=?")) {
            ps.setString(1, type); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    public int countByStatus(String status) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM items WHERE status=?")) {
            ps.setString(1, status); ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    private List<Item> query(String sql, String sp, Integer ip) {
        List<Item> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (sp != null) ps.setString(1, sp);
            if (ip != null) ps.setInt(1, ip);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[ItemDAO] query: " + e.getMessage()); }
        return list;
    }

    private Item map(ResultSet rs) throws SQLException {
        return new Item(
            rs.getInt("id"),            rs.getString("item_name"),
            rs.getString("description"),rs.getString("category"),
            rs.getString("location"),   rs.getString("status"),
            rs.getString("type"),       rs.getString("contact_info"),
            rs.getInt("reported_by"),   rs.getString("reporter"),
            rs.getTimestamp("date_reported")
        );
    }

    public List<Item> getActiveFoundItems() {
        return query("SELECT i.*,u.full_name reporter FROM items i LEFT JOIN users u ON i.reported_by=u.id WHERE i.type='found' AND i.status='active' ORDER BY i.date_reported DESC", null, null);
    }
}