Feature: Order API Migration
  As a software engineer
  I want to migrate the Order RMI API to a REST API
  So that clients can access order data through modern HTTP interfaces

  Scenario: RMI and REST order endpoints should return identical data
    Given an order with ID 42 exists in the system
    When the order is retrieved via the RMI method findOrderById
    And the same order is retrieved via the REST endpoint GET /api/orders/42
    Then both responses should contain identical order data
