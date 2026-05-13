package lostandfound.dao;

import lostandfound.model.User;
import lostandfound.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { System.err.println("[UserDAO] login: " + e.getMessage()); }
        return null;
    }

    public boolean register(User u) {
        String sql = "INSERT INTO users (full_name,email,password,role,student_id,college,program,year_level,section) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getFullName()); ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword()); ps.setString(4, u.getRole());
            ps.setString(5, u.getStudentId()); ps.setString(6, u.getCollege());
            ps.setString(7, u.getProgram()); ps.setInt(8, u.getYearLevel());
            ps.setString(9, u.getSection());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("[UserDAO] register: " + e.getMessage()); }
        return false;
    }

    public boolean emailExists(String email) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id FROM users WHERE email=?")) {
            ps.setString(1, email); return ps.executeQuery().next();
        } catch (SQLException e) { System.err.println("[UserDAO] emailExists: " + e.getMessage()); }
        return false;
    }

    public List<User> getAllStudents() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='student' ORDER BY college,program,year_level,section,full_name";
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { System.err.println("[UserDAO] getAllStudents: " + e.getMessage()); }
        return list;
    }

    public int getTotalStudents() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM users WHERE role='student'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),           rs.getString("full_name"),
            rs.getString("email"),     rs.getString("password"),
            rs.getString("role"),      rs.getString("student_id"),
            rs.getString("college"),   rs.getString("program"),
            rs.getInt("year_level"),   rs.getString("section")
        );
    }
}