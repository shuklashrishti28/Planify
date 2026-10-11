package com.planify.dao;

import com.planify.model.Project;
import com.planify.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    // 1. Naya Project Create Karna
    public boolean createProject(Project project) throws SQLException {
        String sql = "INSERT INTO Projects (title, description, start_date, end_date, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getCreatedBy());

            return stmt.executeUpdate() > 0;
        }
    }

    // 2. Saare Projects Fetch Karna
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM Projects ORDER BY project_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Project p = new Project(
                        rs.getInt("project_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getInt("created_by")
                );
                projects.add(p);
            }
        }
        return projects;
    }
}