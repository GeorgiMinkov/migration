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

## 3 · Running the Demo

```bash
# ① generate PRD (ChatGPT) and save to prd/
# ② generate tasks (SWE model) and save to features/
# ② execute tasks (Claude 3.7) and save to src/

# ③ implement:
./mvnw spotless:apply    # auto‑format
./mvnw test              # unit tests
./mvnw verify            # integration + quality gates
```

### Task Tracking

Open **features/001-order-migration.md** in Windsurf's Markdown preview to monitor progress.  
Windsurf will automatically tick each task (☑️) as the corresponding code implementation or test passes.

Each completed task represents a verified milestone in the migration process from RMI to REST.

---

## 4 · Success Criteria
* REST endpoint meets latency ≤ 120 ms & 5xx ≤ 0.1 % for 7 days.  
* RMI traffic drops ≥ 95 %; method marked `@Deprecated(forRemoval = true)`.  
* README migration table updated; CI green.
