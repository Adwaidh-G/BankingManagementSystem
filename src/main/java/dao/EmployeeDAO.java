package dao;

import db.DBConnection;
import model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee findById(int employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Employee findByUserId(int userId) {
        String sql = "SELECT * FROM employees WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Employee findByEmployeeCode(String employeeCode) {
        String sql = "SELECT * FROM employees WHERE employee_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Employee employee) {
        String sql = "INSERT INTO employees (user_id, employee_code, first_name, " +
                "last_name, position, department, hire_date, salary, branch_id, " +
                "manager_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, employee.getUserId());
            stmt.setString(2, employee.getEmployeeCode());
            stmt.setString(3, employee.getFirstName());
            stmt.setString(4, employee.getLastName());
            stmt.setString(5, employee.getPosition());
            stmt.setString(6, employee.getDepartment());
            stmt.setDate(7, Date.valueOf(employee.getHireDate()));
            stmt.setBigDecimal(8, employee.getSalary());
            stmt.setInt(9, employee.getBranchId());

            if (employee.getManagerId() != null) {
                stmt.setInt(10, employee.getManagerId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setEmployeeId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Employee employee) {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, " +
                "position = ?, department = ?, salary = ?, branch_id = ?, " +
                "manager_id = ? WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getPosition());
            stmt.setString(4, employee.getDepartment());
            stmt.setBigDecimal(5, employee.getSalary());
            stmt.setInt(6, employee.getBranchId());

            if (employee.getManagerId() != null) {
                stmt.setInt(7, employee.getManagerId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, employee.getEmployeeId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY employee_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public String generateEmployeeCode() {
        String sql = "SELECT MAX(employee_id) FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            return String.format("EMP%06d", nextId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "EMP000001";
        }
    }

    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setUserId(rs.getInt("user_id"));
        employee.setEmployeeCode(rs.getString("employee_code"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setPosition(rs.getString("position"));
        employee.setDepartment(rs.getString("department"));
        employee.setHireDate(rs.getDate("hire_date").toLocalDate());
        employee.setSalary(rs.getBigDecimal("salary"));
        employee.setBranchId(rs.getInt("branch_id"));

        int managerId = rs.getInt("manager_id");
        if (!rs.wasNull()) {
            employee.setManagerId(managerId);
        }

        return employee;
    }
}
