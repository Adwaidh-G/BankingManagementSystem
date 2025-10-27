package dao;

import db.DBConnection;
import model.Customer;
import model.Customer.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public Customer findById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findByUserId(int userId) {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findByCustomerNumber(String customerNumber) {
        String sql = "SELECT * FROM customers WHERE customer_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Customer customer) {
        String sql = "INSERT INTO customers (user_id, customer_number, first_name, " +
                "last_name, date_of_birth, gender, address_line1, address_line2, " +
                "city, state, postal_code, country, occupation, annual_income, " +
                "identity_type, identity_number, kyc_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customer.getUserId());
            stmt.setString(2, customer.getCustomerNumber());
            stmt.setString(3, customer.getFirstName());
            stmt.setString(4, customer.getLastName());
            stmt.setDate(5, Date.valueOf(customer.getDateOfBirth()));
            stmt.setString(6, customer.getGender() != null ?
                    customer.getGender().name() : null);
            stmt.setString(7, customer.getAddressLine1());
            stmt.setString(8, customer.getAddressLine2());
            stmt.setString(9, customer.getCity());
            stmt.setString(10, customer.getState());
            stmt.setString(11, customer.getPostalCode());
            stmt.setString(12, customer.getCountry());
            stmt.setString(13, customer.getOccupation());
            stmt.setBigDecimal(14, customer.getAnnualIncome());
            stmt.setString(15, customer.getIdentityType().name());
            stmt.setString(16, customer.getIdentityNumber());
            stmt.setString(17, customer.getKycStatus().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, " +
                "address_line1 = ?, address_line2 = ?, city = ?, state = ?, " +
                "postal_code = ?, occupation = ?, annual_income = ?, " +
                "kyc_status = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getAddressLine1());
            stmt.setString(4, customer.getAddressLine2());
            stmt.setString(5, customer.getCity());
            stmt.setString(6, customer.getState());
            stmt.setString(7, customer.getPostalCode());
            stmt.setString(8, customer.getOccupation());
            stmt.setBigDecimal(9, customer.getAnnualIncome());
            stmt.setString(10, customer.getKycStatus().name());
            stmt.setInt(11, customer.getCustomerId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public String generateCustomerNumber() {
        String sql = "SELECT MAX(customer_id) FROM customers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            return String.format("CUST%08d", nextId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "CUST00000001";
        }
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setUserId(rs.getInt("user_id"));
        customer.setCustomerNumber(rs.getString("customer_number"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());

        String gender = rs.getString("gender");
        if (gender != null) {
            customer.setGender(Gender.valueOf(gender));
        }

        customer.setAddressLine1(rs.getString("address_line1"));
        customer.setAddressLine2(rs.getString("address_line2"));
        customer.setCity(rs.getString("city"));
        customer.setState(rs.getString("state"));
        customer.setPostalCode(rs.getString("postal_code"));
        customer.setCountry(rs.getString("country"));
        customer.setOccupation(rs.getString("occupation"));
        customer.setAnnualIncome(rs.getBigDecimal("annual_income"));
        customer.setIdentityType(IdentityType.valueOf(rs.getString("identity_type")));
        customer.setIdentityNumber(rs.getString("identity_number"));
        customer.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        customer.setKycStatus(KYCStatus.valueOf(rs.getString("kyc_status")));

        return customer;
    }
}
