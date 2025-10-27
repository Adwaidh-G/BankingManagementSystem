package service;

import dao.UserDAO;
import dao.EmployeeDAO;
import model.User;
import model.User.UserType;
import model.Employee;
import util.PasswordUtil;
import util.ValidationUtil;

public class EmployeeService {
    private UserDAO userDAO;
    private EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.userDAO = new UserDAO();
        this.employeeDAO = new EmployeeDAO();
    }

    public boolean addEmployee(User user, Employee employee, boolean isManager) {
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!ValidationUtil.isValidPhone(user.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hashedPassword = PasswordUtil.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setUserType(isManager ? UserType.MANAGER : UserType.EMPLOYEE);

        if (userDAO.save(user)) {
            employee.setUserId(user.getUserId());
            employee.setEmployeeCode(employeeDAO.generateEmployeeCode());

            return employeeDAO.save(employee);
        }

        return false;
    }

    public boolean updateEmployee(Employee employee) {
        if (employee.getFirstName() == null ||
                employee.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (employee.getLastName() == null ||
                employee.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        return employeeDAO.update(employee);
    }

    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.findById(employeeId);
    }

    public Employee getEmployeeByUserId(int userId) {
        return employeeDAO.findByUserId(userId);
    }

    public Employee getEmployeeByCode(String employeeCode) {
        return employeeDAO.findByEmployeeCode(employeeCode);
    }

    public java.util.List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }
}
