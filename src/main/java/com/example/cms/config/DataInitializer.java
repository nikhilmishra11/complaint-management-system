package com.example.cms.config;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.repository.ComplaintRepository;
import com.example.cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "cms.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    public DataInitializer(UserRepository userRepository, ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = createUser("Admin User", "admin@cms.com", "admin123", "9999999999", Role.ADMIN, true);
        User customer = createUser("Customer User", "customer@cms.com", "customer123", "8888888888", Role.CUSTOMER, true);
        User employeeOne = createUser("Employee One", "employee@cms.com", "employee123", "7777777777", Role.EMPLOYEE, false);
        User employeeTwo = createUser("Employee Two", "employee2@cms.com", "employee123", "6666666666", Role.EMPLOYEE, false);
        User priya = createUser("Priya Sharma", "priya@cms.com", "customer123", "8888800001", Role.CUSTOMER, true);
        User rahul = createUser("Rahul Verma", "rahul@cms.com", "customer123", "8888800002", Role.CUSTOMER, true);
        User ananya = createUser("Ananya Iyer", "ananya@cms.com", "customer123", "8888800003", Role.CUSTOMER, true);
        User employeeThree = createUser("Amit Technician", "amit.employee@cms.com", "employee123", "7777700003", Role.EMPLOYEE, true);
        User employeeFour = createUser("Sneha Support", "sneha.employee@cms.com", "employee123", "7777700004", Role.EMPLOYEE, true);

        createComplaint(
                "Water leakage in kitchen",
                "There is continuous water leakage near the kitchen sink and it needs urgent attention.",
                "Maintenance",
                LocalDate.of(2026, 6, 10),
                ComplaintStatus.PENDING,
                "Please visit after 10 AM.",
                null,
                null,
                customer,
                null
        );

        createComplaint(
                "Lobby light not working",
                "The main lobby light has stopped working and makes the entry area dark.",
                "Electrical",
                LocalDate.of(2026, 6, 11),
                ComplaintStatus.IN_PROGRESS,
                "This affects evening visitors.",
                "Replacement tube light has been ordered.",
                null,
                admin,
                employeeOne
        );

        createComplaint(
                "Air conditioner cooling issue",
                "Bedroom air conditioner is running but not cooling properly even after cleaning the filter.",
                "Appliance",
                LocalDate.of(2026, 6, 12),
                ComplaintStatus.COMPLETED,
                "Available on weekdays after 6 PM.",
                "Gas level was low. Refilled and tested successfully.",
                "Cooling is working fine now.",
                priya,
                employeeTwo
        );

        createComplaint(
                "Parking gate remote not working",
                "The parking gate remote is not opening the basement gate and needs reconfiguration.",
                "Security",
                LocalDate.of(2026, 6, 13),
                ComplaintStatus.PENDING,
                "Remote number is P-204.",
                null,
                null,
                rahul,
                null
        );

        createComplaint(
                "Noise complaint from upstairs",
                "There is repeated furniture dragging noise late at night from the apartment above.",
                "Community",
                LocalDate.of(2026, 5, 28),
                ComplaintStatus.IN_PROGRESS,
                "Please keep my name private.",
                "Notice has been sent to the resident.",
                null,
                ananya,
                employeeOne
        );

        createComplaint(
                "Garbage not collected",
                "Wet garbage has not been collected from the floor corridor for two days.",
                "Housekeeping",
                LocalDate.of(2026, 5, 30),
                ComplaintStatus.COMPLETED,
                "This is causing bad smell near the lift.",
                "Housekeeping team collected garbage and sanitized the area.",
                "Resolved quickly, thank you.",
                customer,
                employeeTwo
        );

        createComplaint(
                "Intercom dead line",
                "The apartment intercom has no dial tone and cannot connect to security desk.",
                "Communication",
                LocalDate.of(2026, 4, 18),
                ComplaintStatus.PENDING,
                "Flat number A-1203.",
                null,
                null,
                priya,
                null
        );

        createComplaint(
                "Lift display flickering",
                "The display panel inside lift B flickers and sometimes shows the wrong floor number.",
                "Lift",
                LocalDate.of(2026, 3, 7),
                ComplaintStatus.COMPLETED,
                "Issue happens mostly in the evening.",
                "Display connector was loose and has been fixed.",
                "Looks good after repair.",
                rahul,
                employeeTwo
        );

        createComplaint(
                "Water pressure low",
                "Water pressure in bathroom taps is very low during morning hours.",
                "Plumbing",
                LocalDate.of(2026, 2, 20),
                ComplaintStatus.IN_PROGRESS,
                "Please inspect between 8 AM and 9 AM.",
                "Pressure valve inspection is scheduled.",
                null,
                ananya,
                employeeOne
        );

        createComplaint(
                "Garden sprinkler broken",
                "One sprinkler near the children play area is broken and wasting water.",
                "Landscaping",
                LocalDate.of(2026, 1, 15),
                ComplaintStatus.COMPLETED,
                "Observed near the east lawn.",
                "Sprinkler head replaced.",
                "No leakage now.",
                admin,
                employeeTwo
        );
    }

    private User createUser(String name, String email, String password, String phone, Role role, boolean available) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole(role);
        user.setAvailable(available);
        return userRepository.save(user);
    }

    private Complaint createComplaint(String title,
                                      String description,
                                      String category,
                                      LocalDate complaintDate,
                                      ComplaintStatus status,
                                      String comments,
                                      String employeeRemarks,
                                      String feedback,
                                      User customer,
                                      User assignedEmployee) {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setCategory(category);
        complaint.setComplaintDate(complaintDate);
        complaint.setStatus(status);
        complaint.setComments(comments);
        complaint.setEmployeeRemarks(employeeRemarks);
        complaint.setFeedback(feedback);
        complaint.setCustomer(customer);
        complaint.setAssignedEmployee(assignedEmployee);
        return complaintRepository.save(complaint);
    }
}
