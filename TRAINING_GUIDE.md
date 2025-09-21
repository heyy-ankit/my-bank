# Java Banking Application – Training Guide

This guide supplements `BANKING_REQUIREMENTS.md` and is designed to help a trainee deliberately practice core and intermediate Java concepts while building the console banking application.

---
## 1. Learning Objectives Mapping
| Area | Concept | Where Applied | Outcome Skill |
|------|---------|--------------|---------------|
| OOP Basics | Classes/Objects | Customer, Account, Transaction | Model real-world entities |
| OOP | Encapsulation | Private fields + getters | Protect internal state |
| OOP | Inheritance vs Composition | (Optional) Account hierarchy | Design trade‑offs |
| OOP | Polymorphism | Potential future: Account-specific fees | Dynamic behavior |
| OOP | Abstraction | Service layer vs UI | Separation of concerns |
| Data Types | Primitives vs Objects | Amounts (BigDecimal), IDs (String) | Correct type choice |
| Collections | List / Map | Accounts list, maps in BankService | CRUD & iteration |
| Generics | Collections API | Unmodifiable lists | Type safety |
| Exceptions | Custom runtime exceptions | Validation & business rules | Error signaling |
| Immutability | Transaction object | Safer history tracking | Side-effect control |
| Enums | AccountType, TransactionType | Branching logic | Enum-driven design |
| Date/Time API | LocalDateTime | Transaction timestamps | Modern date handling |
| BigDecimal | Money arithmetic | Deposit, withdraw, transfer | Precision & rounding |
| Static Utility | InputValidator | Reusable validation logic | DRY principle |
| Layering | UI vs Service vs Model | BankApplication & BankService | Maintainability |
| Testing | JUnit (conceptual) | Service methods | Unit test design |
| Persistence (Phase 2) | File IO / JSON | Saving state | Serialization awareness |
| Refactoring | Patterns (Strategy, Factory) | Interest, Transaction creation | Improve design |
| Streams (Later) | Aggregations | Totals, filtering history | Declarative style |
| Concurrency (Stretch) | Synchronization | Thread-safe service | Advanced control |

---
## 2. Implementation Pathway (Recommended Order)
Difficulty tags: (B)=Beginner, (I)=Intermediate, (A)=Advanced.
1. (B) Create enums: AccountType, AccountStatus, TransactionType.
2. (B) Implement immutable `Transaction` with factory method.
3. (B) Implement `Customer` (no logic yet beyond storing accounts).
4. (B) Implement `Account` with deposit/withdraw (no transfer yet).
5. (B) Implement exceptions (ValidationException, etc.).
6. (B) Implement `BankService` minimally: createCustomer, openAccount, find methods.
7. (B) Add deposit & withdraw operations; record transactions.
8. (B) Implement console loop with menu options 1–5 only.
9. (B) Add transfer operation (atomic logic) + menu option.
10. (B) Implement transaction history view (ordering).
11. (I) Add input validation utilities (centralize logic).
12. (I) Add test cases for deposit/withdraw/transfer & failure paths.
13. (I) Refactor any duplication (DRY pass).
14. (I) Add persistence (save/load JSON or simple serialization) on exit/start.
15. (I) Add savings interest feature & `InterestCalculator`.
16. (I) Add account closing & status handling.
17. (A) Introduce `TransactionFactory` class (replace scattered creation).
18. (A) Introduce `InterestPolicy` interface (Strategy pattern variants).
19. (A) Introduce filtering (date range, type) for transaction history using Streams.
20. (A) Add external transfers between customers & optional transfer fee.
21. (A) Add export statement to file (CSV or text).
22. (A) Add basic authentication (customer PIN) + session.
23. (A) Make service minimally thread-safe (synchronized methods or locks).

---
## 3. Core Test Matrix (Phase 1)
| Feature | Scenario | Precondition | Action | Expected Result |
|---------|----------|--------------|--------|-----------------|
| Create Customer | Valid input | N/A | createCustomer | New ID, stored, accounts empty |
| Create Customer | Blank name | N/A | createCustomer | ValidationException |
| Open Account | Valid | Customer exists | openAccount | Account in maps, balance=0 |
| Open Account | Customer missing | N/A | openAccount | CustomerNotFoundException |
| Deposit | Valid amount | Account active | deposit | Balance increased; DEPOSIT txn |
| Deposit | Negative amount | Account active | deposit | ValidationException |
| Withdraw | Exact funds | Balance=amount | withdraw | Balance=0; WITHDRAWAL txn |
| Withdraw | Insufficient funds | Balance < amount | withdraw | InsufficientFundsException |
| Transfer | Valid | Both active, funds ok | transfer | Two balances changed; 2 txns |
| Transfer | Same account | Account exists | transfer | ValidationException |
| Transfer | Insufficient funds | Source < amount | transfer | InsufficientFundsException, no changes |
| History | No transactions | Fresh account | getTransactions | Empty list |
| History | After operations | Deposits made | getTransactions | Ordered oldest→newest |

Add negative tests: null account number, null amount, zero amount, closed status (when implemented).

---
## 4. Error Message Catalog
| Condition | Exception | User Message | Notes |
|-----------|-----------|-------------|-------|
| Customer ID not found | CustomerNotFoundException | Customer not found. | Do not leak raw ID if privacy concern |
| Account not found | AccountNotFoundException | Account not found. | |
| Amount <= 0 | ValidationException | Amount must be positive. | Normalize scale before compare |
| Insufficient funds | InsufficientFundsException | Insufficient funds. | No partial withdrawal |
| Same source & target | ValidationException | Source and target accounts must differ. | Transfer only |
| Owner mismatch (future) | ValidationException | Accounts must belong to same customer. | Phase 1 rule |
| Account closed/frozen | ValidationException | Account not active. | After status feature |
| Invalid email | ValidationException | Invalid email format. | Simple pattern only |

---
## 5. Refactoring & Pattern Exercises
| Goal | Pattern / Concept | Exercise |
|------|------------------|----------|
| Remove duplicated transaction creation | Factory | Introduce `TransactionFactory` with `createDeposit(...)`, etc. |
| Plug different interest rules | Strategy | `InterestPolicy` interface; FlatRatePolicy vs TieredPolicy |
| Conditional removal (type branching) | Polymorphism | Subclass `Account` for specialized logic |
| Centralize validation | Chain / Utility | Build `Validator` with fluent checks |
| Avoid long service class | Facade / Split | Split `TransferService`, `AccountService` from `BankService` |
| Undo capability (stretch) | Command Pattern | Commands for deposit/withdraw with inverse |

---
## 6. Extension & Challenge Tasks
Grouped by concept:
- Collections & Streams: Transaction filtering by amount range; Top N largest deposits.
- Persistence: Add version field; migration from v1 → v2 adding `currency`.
- Security: Add simple PIN hash (store SHA-256). Lock account after 3 failed attempts.
- Concurrency: Simulate two threads performing deposits – use synchronized blocks.
- Reporting: Generate end-of-day summary (total deposits, withdrawals, net flow).
- Internationalization: Support currency symbol configuration.
- Performance: Measure time for 10k random transactions; optimize list traversals with caching totals.

---
## 7. UML (ASCII Sketches)
### 7.1 Class Diagram
```
+------------------+        1    *   +------------------+
|   Customer       |---------------->|     Account      |
+------------------+                 +------------------+
| id               |                 | accountNumber    |
| name             |                 | type: AccountType|
| email            |                 | status           |
| dateCreated      |                 | balance: BigDec  |
| accounts: List   |                 | owner: Customer  |
+------------------+                 | transactions:List|
                                     +------------------+
                                              *
                                              |
                                              v
                                        +-------------+
                                        | Transaction |
                                        +-------------+
                                        | id          |
                                        | timestamp   |
                                        | type        |
                                        | amount      |
                                        | balanceAfter|
                                        | description |
                                        +-------------+
```

### 7.2 Sequence – Deposit
```
User -> UI(run loop): select Deposit
UI -> BankService: deposit(acct, amt)
BankService -> Account: deposit(amt)
Account -> Account: balance += amt
BankService -> Account: addTransaction(Txn)
UI <- BankService: (void)
UI -> User: print new balance
```

### 7.3 Sequence – Transfer
```
User -> UI: select Transfer
UI -> BankService: transfer(from, to, amt)
BankService -> Account(from): validate & withdraw path
Account(from) -> Account(from): balance -= amt
BankService -> Account(to): deposit path
Account(to) -> Account(to): balance += amt
BankService -> Account(from): addTransaction(TRANSFER_OUT)
BankService -> Account(to): addTransaction(TRANSFER_IN)
UI <- BankService: (void)
UI -> User: print both balances
```

### 7.4 Account Status State Diagram
```
   +---------+    freeze    +---------+
   | ACTIVE  | ----------> | FROZEN  |
   +---------+ <---------- +---------+
       |  close (balance=0)
       v
   +---------+
   | CLOSED  |
   +---------+
```
Rules: CLOSED is terminal. Cannot withdraw/deposit when FROZEN (maybe allow deposit—design choice). No reopening CLOSED.

---
## 8. Acceptance Criteria (Phase 1 Core)
| Feature | Criteria |
|---------|----------|
| Create Customer | Valid data yields unique ID; accessible via find; no exception thrown. |
| Open Account | New account has balance 0; appears in listAccounts; status ACTIVE. |
| Deposit | Balance increases exactly by amount; transaction recorded with correct balanceAfter. |
| Withdraw | Fails with exception if insufficient; otherwise reduces balance and records transaction. |
| Transfer | Either both balances change and 2 transactions exist OR no change on validation failure. |
| History | Returned list immutable; chronological order; reflects all successful operations. |
| Validation | Invalid inputs never mutate balances; clear error messages supplied. |
| Exit | Clean termination; (Phase 2) persists state if persistence added. |

---
## 9. Review & Quality Checklist
Before marking a milestone complete:
- [ ] All public methods have Javadoc or comments.
- [ ] No method > 25 lines (except trivial UI loops).
- [ ] No duplicated validation blocks (search for repeated amount > 0 logic).
- [ ] All BigDecimal operations avoid `==` and use `compareTo`.
- [ ] Collections returned are unmodifiable where appropriate.
- [ ] Exceptions include actionable messages.
- [ ] Test coverage: deposit, withdraw, transfer (success + failure) implemented.
- [ ] No System.out debug leftovers (replace with minimal Logger if introduced).
- [ ] Transaction timestamps appear reasonable (no future times).
- [ ] Domain invariants hold: balance >= 0, transaction balanceAfter matches account state.

---
## 10. Suggested Daily Practice Plan
Day 1: Enums, Transaction, Customer.  
Day 2: Account + deposit/withdraw + basic tests.  
Day 3: BankService core + menu skeleton.  
Day 4: Transfer + history + negative tests.  
Day 5: Refactoring + validation centralization.  
Day 6: Persistence + interest.  
Day 7: Patterns (Strategy/Factory) + extensions.

---
## 11. Reflection Prompts
After each feature:
- What invariants did I enforce? Any missed?
- Could I extract a reusable method?
- Did I couple UI and logic unnecessarily?
- How would this change if multi-threaded?
- What is the simplest failing test I can still add?

---
## 12. Stretch (Advanced) Thought Experiments
- Event Sourcing: Instead of storing balance, derive it from summing transactions – pros/cons?
- Optimistic Locking: How would you prevent lost updates across threads?
- Caching: Maintain running totals to avoid summing large histories – when premature?
- Domain-Driven Design Terms: Where would Aggregates, Value Objects, Entities map?

---
## 13. Getting Started Commands (Optional)
```
# Compile (after you create src structure)
javac -d out $(dir /s /b src\*.java)

# Run
java -cp out com.mybank.BankApplication
```
(Adjust for actual package & folder structure.)

---
## 14. Glossary
| Term | Definition |
|------|------------|
| Balance | Current amount of money in an account. |
| Transaction | Immutable record of a money movement affecting balance. |
| Account | Holder of balance & transaction history. |
| Customer | Owner aggregate referencing accounts. |
| Interest | Additional amount credited based on rate over time. |
| Invariant | Condition that must always hold true in the model. |

---
## 15. Next Steps After Completion
1. Port to a simple REST API using Spring (introduces frameworks).
2. Replace in-memory storage with an H2 database (introduces JDBC/JPA basics).
3. Add Maven/Gradle build with dependency management.
4. Introduce logging framework (SLF4J + Logback).
5. Add configuration file for rates and limits.

---
Happy coding. Treat this guide as a structured syllabus—focus on correctness first, then elegance, then extensibility.

---
## 16. Phase 2 Deep Dive (Enhancements)

### 16.1 Focus Areas
Persistence, interest calculation, account lifecycle (freeze/close), authentication, filtering. Reinforces file IO, object graphs, and extended validation.

### 16.2 Task Breakdown
1. Add `schemaVersion` to JSON output.
2. Implement `PersistenceService` + concrete `FilePersistenceService`.
3. Hook load at startup & save on exit.
4. Introduce `InterestCalculator` (flat annual rate constant) + menu option.
5. Implement account freeze/unfreeze & close operations (validate zero balance on close).
6. Optional: Add overdraft limit for checking accounts.
7. Optional: Add PIN-based auth (store hash or plain for simplicity, document risk).
8. Add filtered transaction retrieval (by type, date range).
9. Expand tests: persistence round-trip, interest math edge cases, closed account operations.

### 16.3 Additional Tests
| Feature | Scenario | Expected |
|---------|----------|----------|
| Persistence | Save & reload | Objects equal in key fields |
| Interest | Zero balance | No transaction created |
| Freeze | Withdraw attempt | ValidationException |
| Close | Non-zero balance | ValidationException |
| Overdraft | Within limit | Negative balance allowed |
| Overdraft | Beyond limit | InsufficientFundsException |
| Auth | Wrong PIN thrice | Account (or session) locked |

### 16.4 Design Reminders
- Keep persistence logic out of domain objects.
- Use defensive copying when reconstructing lists from file.
- Normalize BigDecimal scale before persisting.

### 16.5 Refactor Suggestions Before Phase 3
- Extract `TransactionFactory` for consistent creation.
- Encapsulate interest logic via interface to allow tiered policies later.

---
## 17. Phase 3 Deep Dive (Advanced)

### 17.1 Focus Areas
Multi-user workflow, external transfers, scheduling, statements, currency, roles, metrics, pagination.

### 17.2 Task Breakdown
1. Add `Role` enum (USER, ADMIN).
2. Add multi-user login & session switching.
3. Implement external transfers (optional fee & FEE transaction type).
4. Add scheduled transactions queue + executor tick on main loop.
5. Implement statement export (CSV or plain text).
6. Introduce currency support and conversion table.
7. Add pagination to transaction viewing.
8. Add metrics counters (totalTransactions, failedTransactions).
9. Admin menu options: batch interest, view metrics, export all statements.
10. Add `AuthorizationException` and enforce role checks.

### 17.3 Additional Tests
| Feature | Scenario | Expected |
|---------|----------|----------|
| External Transfer | Different owners | Two accounts updated correctly |
| External Transfer | With fee | Fee transaction recorded |
| Scheduled Txn | Due now | Executes & removed from schedule |
| Scheduled Txn | Insufficient funds | Marked failed, no balance change |
| Statement Export | Range filter | File contains only in-range txns |
| Currency | Conversion | Target credited with converted amount |
| Pagination | Page beyond end | Empty list |
| Authorization | Non-admin uses admin feature | AuthorizationException |

### 17.4 Performance Mini-Experiment
Generate 5,000 transactions and measure history retrieval time; refactor if > acceptable threshold (e.g., >50ms on typical machine) by precomputing indexes or using streams lazily.

### 17.5 Optional Advanced Refactors
- Introduce repository interfaces and mock them in tests.
- Replace direct lists with immutable wrappers or Value Objects for Money.
- Introduce configuration abstraction for rates & fees.

### 17.6 Graduation Challenge
Design and implement a rollback mechanism for failed multi-step operations (simulate partial failure & compensation). Document approach.

---
## 18. Phase Mapping Summary
| Phase | Core Concepts Reinforced |
|-------|--------------------------|
| 1 | OOP, collections, exceptions, BigDecimal basics |
| 2 | Persistence, lifecycle states, strategy intro, validation architecture |
| 3 | Advanced design, roles & auth, scheduling, reporting, performance tuning |

