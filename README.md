# Order-Service — RMI ➜ REST Migration Demo  
*AI-assisted workflow with **ChatGPT**, an **SWE LLM** for task breakdown, and **Windsurf IDE***

---

## 1 · Project Overview
We start with a Spring Boot service whose `findOrderById(Integer id)` method is still exposed through Java RMI.  
The goal is to replace that call with a modern HTTP/JSON endpoint `GET /api/orders/{id}` and showcase how AI can accelerate **spec → tasks → code**.

```
src/
 ├─ main/java/com/example/order
 │    ├─ adapter/out/rmi/OrderRmiService.java   # legacy façade
 │    ├─ domain/Order.java
 │    ├─ repository/OrderRepository.java
 │    └─ usecase/OrderService.java              # delegates to RMI for now
 └─ test/…                                      # will host Gherkin & tests
```

---

## 2 · Demo Flow (no Task Master)

| Phase | What you do | Tool / prompt | Result |
|-------|-------------|---------------|--------|
| **1. PRD generation** | Ask ChatGPT to draft the migration PRD. | **Prompt below** | `prd/order-migration.md` |
| **2. Task breakdown** | Feed the PRD to an **SWE LLM** (e.g. `gpt-4o-swe`) to create check‑box tasks. | **Prompt below** | `features/001_order_migration_tasks.md` |
| **3. Implementation** | Use Windsurf chat to scaffold code & tests, run Maven gates, and tick boxes as you go. | **Prompt cheatsheet below** | Production‑ready code + green CI |

### 2.1 ChatGPT prompt (PRD)

```text
You are a senior product manager. Draft a one-page PRD for migrating
the RMI method findOrderById(Integer id) to GET /api/orders/{id} in a
Spring Boot service. Include problem statement, scope, success metrics,
user stories, and Gherkin acceptance criteria. Return Markdown.
```

### 2.2 SWE‑model prompt (task generation)

```text
Act as a software engineering lead. Break the PRD at prd/order-migration.md
into sequential implementation tasks following our layered architecture
(adapter.in.rest → usecase → adapter.out.rmi).  
Output Markdown to be saved as features/001_order_migration_tasks.md.  
For every step include an empty checkbox “- [ ] ...”.
```

*Expected excerpt*:

```markdown
### Story 1 — OrderController (adapter.in.rest)
- [ ] Scaffold `OrderDto`
- [ ] Implement `OrderController#getOrder(@PathVariable id)`
- [ ] Unit‑test controller with mocked `OrderService`
```

### 2.3 Windsurf prompt cheatsheet (implementation)

| Goal | Windsurf chat prompt |
|------|----------------------|
| **Scaffold controller** | `Generate a @RestController at GET /api/orders/{id} returning OrderDto; validate @Positive id; map OrderService.` |
| **Create service bridge** | `Generate OrderService in usecase that calls OrderRmiService.findOrderById and toggles by feature flag useRest_order.` |
| **Unit test** | `Create JUnit 5 test for OrderController mocking OrderService; expect 200 & JSON body.` |
| **Parity test stub** | `Create a Cucumber step definition comparing RMI vs REST payload for order ID 42.` |

---

## 3 · Implementation Spotlights with AI

Leveraging AI accelerated the RMI-to-REST migration while ensuring adherence to company standards. Here are concrete examples of how AI-assisted development improved quality and productivity:

### Layered Architecture Compliance

AI automatically implemented the proper layered architecture pattern, following company best practices:

```java
// Layer: adapter.in.rest - Clean separation of REST adapter from domain logic
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    // Constructor injection and REST mapping with proper validation
}

// Layer: usecase - Feature flag to control gradual migration
public class OrderService {
    @Value("${feature.flag.useRest_order:false}")
    private boolean useRestOrder;
    // Service orchestration with metrics collection
}

// Layer: adapter.out - Encapsulates legacy RMI implementation
@Service
public class OrderRmiServiceImpl implements OrderRmiService {
    // Legacy RMI implementation with proper deprecation
}
```

### Testing Standards & Best Practices

AI automatically incorporated company testing standards:

1. **Unit Tests**: Controller tests with mocked dependencies using Mockito
2. **Parity Testing**: Cucumber-based parity tests comparing RMI vs REST responses
3. **Spring Configuration**: Proper test context configuration with `@CucumberContextConfiguration`

```java
// Example: Cucumber Spring configuration for proper context setup
@CucumberContextConfiguration
@SpringBootTest(
    classes = MigrationApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CucumberSpringConfiguration {}
```

### Observability & Migration Safeguards

AI implemented extensive monitoring and feature flags without prompting:

```java
// Metrics collection for tracking migration progress
MeterRegistry registry;
Counter rmiUsageCounter = Counter.builder("rmi.usage.count")
    .description("Count of calls to legacy RMI method")
    .register(registry);

// Feature flag implementation for gradual rollout
if (!useRestOrder) {
    log.info("Using legacy RMI implementation");
    rmiUsageCounter.increment();
    return orderRmiService.findOrderById(id);
} else {
    log.info("Using new Repository implementation");
    return orderRepository.findById(id);
}
```

### Task Tracking

Open **features/001-order-migration.md** in Windsurf's Markdown preview to monitor progress.  
Windsurf automatically ticks each task (☑️) as the corresponding code implementation or test passes, enabling real-time tracking of migration progress.

---

## 4 · Success Criteria
* REST endpoint meets latency ≤ 120 ms & 5xx ≤ 0.1 % for 7 days.  
* RMI traffic drops ≥ 95 %; method marked `@Deprecated(forRemoval = true)`.  
* README migration table updated; CI green.
