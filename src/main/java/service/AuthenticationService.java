package service;

import dao.UserDAO;
import dao.EmployeeDAO;
import dao.CustomerDAO;
import model.User;
import model.User.UserType;
import model.Employee;
import model.Customer;
import util.PasswordUtil;

public class AuthenticationService {
    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;
    private CustomerDAO customerDAO;
    private User currentUser;
    private Employee currentEmployee;
    private Customer currentCustomer;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();
        this.customerDAO = new CustomerDAO();
    }

    public boolean login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return false;
        }

        if (!user.isActive()) {
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        if (user.getPasswordHash().equals(hashedPassword)) {
            this.currentUser = user;

            // Load employee or customer data
            if (user.getUserType() == UserType.EMPLOYEE ||
                    user.getUserType() == UserType.MANAGER) {
                this.currentEmployee = employeeDAO.findByUserId(user.getUserId());
            } else if (user.getUserType() == UserType.CUSTOMER) {
                this.currentCustomer = customerDAO.findByUserId(user.getUserId());
            }

            return true;
        }

        return false;
    }

    public void logout() {
        this.currentUser = null;
        this.currentEmployee = null;
        this.currentCustomer = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isEmployee() {
        return currentUser != null &&
                (currentUser.getUserType() == UserType.EMPLOYEE ||
                        currentUser.getUserType() == UserType.MANAGER);
    }

    public boolean isManager() {
        return currentUser != null &&
                currentUser.getUserType() == UserType.MANAGER;
    }

    public boolean isCustomer() {
        return currentUser != null &&
                currentUser.getUserType() == UserType.CUSTOMER;
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }

        String hashedOldPassword = PasswordUtil.hashPassword(oldPassword);
        if (!currentUser.getPasswordHash().equals(hashedOldPassword)) {
            return false;
        }

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(currentUser.getUserId(), hashedNewPassword);
    }

    // Getters
    public User getCurrentUser() { return currentUser; }
    public Employee getCurrentEmployee() { return currentEmployee; }
    public Customer getCurrentCustomer() { return currentCustomer; }
}
