# Product Requirements Document  
**Feature — Replace `findOrderById(Integer id)` RMI call with `GET /api/orders/{id}` REST endpoint**

---

## 1  Problem Statement  
Our Order Management platform still exposes core data via **Java RMI**, which:

* Fails through firewalls and load-balancers in containerised / cloud deployments.  
* Is opaque to observability tooling (APM, tracing) and hard to version.  
* Blocks third-party integrators who expect HTTP/JSON.

Migrating the high-traffic `findOrderById` lookup to **Spring Boot REST** is the first step in strangling the legacy RMI interface.

---

## 2  Scope

| In Scope | Out of Scope |
|----------|--------------|
| • New `@RestController` at path `/api/orders/{id}` returning `OrderDto` <br>• Strangler bridge — REST delegates to existing RMI service until parity proven <br>• Feature-flag `useRest_order` for gradual cut-over <br>• Parity tests comparing RMI vs REST JSON payload | • Any other RMI methods (handled in later iterations) <br>• Changes to Order persistence model <br>• UI changes; only backend interface is touched |

---

## 3  Success Metrics

| KPI | Target | Measurement Window |
|-----|--------|--------------------|
| **P95 latency (REST)** | ≤ 120 ms | 7 consecutive days in staging |
| **Error rate (HTTP 5xx)** | ≤ 0.1 % | Same window |
| **Traffic migration** | ≥ 95 % of `findOrderById` calls via REST | 30 days post-launch |
| **RMI decommission** | Legacy method removed from codebase | by **T + 60 days** |

---

## 4  User Stories

1. **As an external integrator**  
   I want to retrieve an order by ID over HTTPS/JSON  
   so that I can consume the API without custom RMI clients.

2. **As a backend Java developer**  
   I want the REST endpoint to proxy the existing RMI logic behind a feature flag  
   so I can ship incrementally and roll back if needed.

3. **As an SRE**  
   I need metrics and traces on the new endpoint  
   so I can verify performance and spot regressions.

---

## 5  Acceptance Criteria (Gherkin)

```gherkin
Feature: Retrieve order by ID over REST
  Background:
    Given an order with ID 42 exists in the system

  Scenario: Successful lookup
    When the client calls GET /api/orders/42
    Then the service responds with HTTP 200
    And the JSON body equals
      """
      {
        "id": 42,
        "status": "SHIPPED",
        "total": 123.45
      }
      """

  Scenario: Order not found
    When the client calls GET /api/orders/999
    Then the service responds with HTTP 404
    And the JSON body contains "error": "ORDER_NOT_FOUND"

  Scenario: Parity with legacy RMI
    When the client calls the RMI method findOrderById(42)
    And the client calls GET /api/orders/42
    Then both responses are identical JSON objects
