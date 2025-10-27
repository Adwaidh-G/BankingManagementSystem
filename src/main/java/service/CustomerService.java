package service;

import dao.UserDAO;
import dao.CustomerDAO;
import model.User;
import model.User.UserType;
import model.Customer;
import util.PasswordUtil;
import util.ValidationUtil;

public class CustomerService {
    private UserDAO userDAO;
    private CustomerDAO customerDAO;

    public CustomerService() {
        this.userDAO = new UserDAO();
        this.customerDAO = new CustomerDAO();
    }

    public boolean registerCustomer(User user, Customer customer) {
        // Validate user data
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!ValidationUtil.isValidPhone(user.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        // Check if username already exists
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Hash password
        String hashedPassword = PasswordUtil.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setUserType(UserType.CUSTOMER);

        // Save user
        if (userDAO.save(user)) {
            customer.setUserId(user.getUserId());
            customer.setCustomerNumber(customerDAO.generateCustomerNumber());

            // Save customer
            return customerDAO.save(customer);
        }

        return false;
    }

    public boolean updateCustomerProfile(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        return customerDAO.update(customer);
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.findById(customerId);
    }

    public Customer getCustomerByUserId(int userId) {
        return customerDAO.findByUserId(userId);
    }

    public Customer getCustomerByCustomerNumber(String customerNumber) {
        return customerDAO.findByCustomerNumber(customerNumber);
    }

    public java.util.List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
}
