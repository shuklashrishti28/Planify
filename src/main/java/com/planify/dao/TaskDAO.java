package com.planify.dao;

import com.planify.model.Task;
import com.planify.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/** Contains JDBC operations for tasks and their project/member relationships. */
public class TaskDAO {

    /** Creates a task and assigns it to a team member. */
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

    /** Returns all tasks with display names for the related project and assignee. */
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

    /** Returns only tasks assigned to the given member. */
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

    /** Updates task status for a manager-authorized task. */
    public boolean updateTaskStatus(int taskId, String status) throws SQLException {
        String sql = "UPDATE Tasks SET status = ? WHERE task_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, taskId);

            return stmt.executeUpdate() > 0;
        }
    }

    /** Updates task status only when the task belongs to the given team member. */
    public boolean updateTaskStatus(int taskId, int userId, String status) throws SQLException {
        String sql = "UPDATE Tasks SET status = ? WHERE task_id = ? AND assigned_to = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, taskId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateTask(Task task) throws SQLException {
        String sql = "UPDATE Tasks SET project_id=?, assigned_to=?, title=?, description=?, status=?, due_date=? WHERE task_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, task.getProjectId());
            stmt.setInt(2, task.getAssignedTo());
            stmt.setString(3, task.getTitle());
            stmt.setString(4, task.getDescription());
            stmt.setString(5, task.getStatus());
            stmt.setDate(6, task.getDueDate());
            stmt.setInt(7, task.getTaskId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTask(int taskId) throws SQLException {
        String sql = "DELETE FROM Tasks WHERE task_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Returns counts for the three supported workflow states. */
    public Map<String, Integer> getStatusCounts() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("PENDING", 0);
        counts.put("IN_PROGRESS", 0);
        counts.put("COMPLETED", 0);
        String sql = "SELECT status, COUNT(*) AS task_count FROM Tasks GROUP BY status";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("task_count"));
            }
        }
        return counts;
    }

}
