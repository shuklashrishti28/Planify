package com.planify.dao;

import com.planify.model.Project;
import com.planify.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Contains JDBC operations for projects. */
public class ProjectDAO {
    /** Creates a project and records its manager/admin creator. */
    public boolean createProject(Project project) throws SQLException {
        String sql = "INSERT INTO Projects (title, description, start_date, end_date, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getCreatedBy());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Returns projects newest first. */
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT project_id, title, description, start_date, end_date, created_by "
                + "FROM Projects ORDER BY project_id DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                projects.add(new Project(rs.getInt("project_id"), rs.getString("title"),
                        rs.getString("description"), rs.getDate("start_date"), rs.getDate("end_date"),
                        rs.getInt("created_by")));
            }
        }
        return projects;
    }

    /** Updates a project's editable fields. */
    public boolean updateProject(Project project) throws SQLException {
        String sql = "UPDATE Projects SET title=?, description=?, start_date=?, end_date=? WHERE project_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getProjectId());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Deletes a project; related tasks are removed by the database cascade. */
    public boolean deleteProject(int id) throws SQLException {
        String sql = "DELETE FROM Projects WHERE project_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean exists(int projectId) throws SQLException {
        String sql = "SELECT 1 FROM Projects WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
