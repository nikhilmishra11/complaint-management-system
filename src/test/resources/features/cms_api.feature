Feature: Complaint Management System API

  Scenario: Authentication endpoints work
    Given the CMS API is running
    When I login as the seeded admin
    Then the latest response status is 200
    When I register a customer through the API
    Then the latest response status is 201
    When I reset the seeded customer password
    Then the latest response status is 200

  Scenario: Complaint lifecycle works end to end
    Given the CMS API is running
    When I create a complaint for that customer
    Then the latest response status is 201
    And the complaint can be viewed by id
    When I update the complaint details
    Then the latest response status is 200
    When I change the complaint status to in progress
    Then the latest response status is 200
    When I add customer comments and feedback
    Then the latest response status is 200
    When I assign the complaint to an employee
    Then the latest response status is 200
    And the customer can view the complaint
    And the employee can view the assigned complaint
    When the employee completes the complaint with remarks
    Then the latest response status is 200
    And pending completed date range and user reports are available
    And I can delete the complaint
