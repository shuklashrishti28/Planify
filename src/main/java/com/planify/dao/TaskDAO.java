package com.planify.dao;

import com.planify.model.Task;
import com.planify.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // 1. Naya Task Add Karna
    public boolean createTask(Task task) throws SQLException {
        String sql = "INSERT INTO Tasks (project_id, assigned_to, title, description, status, due_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, task.getProjectId());
            stmt.setInt(2, task.getAssignedTo());
            stmt.setString(3, task.getTitle());
            stmt.setString(4, task.getDescription());
            stmt.setString(5, task.getStatus());
            stmt.setDate(6, task.getDueDate());

            return stmt.executeUpdate() > 0;
        }
    }

    // 2. Saare Tasks Fetch Karna (Project Title aur User Name ke saath JOIN)
    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.*, p.title AS project_title, u.name AS user_name " +
                "FROM Tasks t " +
                "JOIN Projects p ON t.project_id = p.project_id " +
                "LEFT JOIN Users u ON t.assigned_to = u.user_id " +
                "ORDER BY t.task_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Task t = new Task(
                        rs.getInt("task_id"),
                        rs.getInt("project_id"),
                        rs.getInt("assigned_to"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getDate("due_date")
                );
                t.setProjectTitle(rs.getString("project_title"));
                t.setAssignedToName(rs.getString("user_name"));
                tasks.add(t);
            }
        }
        return tasks;
    }

    // 3. Ek Specific User ke Assigned Tasks Fetch Karna
    public List<Task> getTasksByUserId(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.*, p.title AS project_title FROM Tasks t " +
                "JOIN Projects p ON t.project_id = p.project_id " +
                "WHERE t.assigned_to = ? ORDER BY t.due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task t = new Task(
                            rs.getInt("task_id"),
                            rs.getInt("project_id"),
                            rs.getInt("assigned_to"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getDate("due_date")
                    );
                    t.setProjectTitle(rs.getString("project_title"));
                    tasks.add(t);
                }
            }
        }
        return tasks;
    }

    // 4. Task Status Update Karna
    public boolean updateTaskStatus(int taskId, String status) throws SQLException {
        String sql = "UPDATE Tasks SET status = ? WHERE task_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, taskId);

            return stmt.executeUpdate() > 0;
        }
    }
}
