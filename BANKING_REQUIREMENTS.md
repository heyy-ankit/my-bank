# Java Console Banking Application - Detailed Requirements

## 1. Purpose & Scope
A small, single-user (initially) Java console application to practice OOP, collections, control flow, and exception handling. The app simulates basic banking operations: creating customers, managing accounts, depositing, withdrawing, transferring, viewing histories, and persisting simple data (optional phases). Designed for incremental enhancement.

## 2. Assumptions
- Runs with Java 17+ (no external frameworks required initially).
- Console (STDIN/STDOUT) only; no GUI or web.
- Single process; no concurrency required in Phase 1.
- In-memory storage first; optional file-based persistence later (JSON / serialized / CSV / simple custom).
- Monetary values stored as `BigDecimal` (best practice) or `double` (learning phase - but highlight rounding issues). Recommend `BigDecimal`.
- Dates use `java.time` API (e.g., `LocalDateTime`).
- One currency (e.g., USD) in Phase 1.
- Authentication optional (add in later phase).

## 3. High-Level Feature List (Phased)
### Phase 1 (Core)
- Create customer
- Open account (Checking / Savings)
- List accounts
- Deposit / Withdraw
- Transfer between own accounts
- View account balance
- View transaction history
- Exit application safely

### Phase 2 (Enhancements)
- Interest calculation for Savings
- Overdraft protection or rejection logic
- Persistent storage (load/save on start/exit)
- Basic authentication (login with PIN/password)
- Search customer by ID
- Close account (with rules)

### Phase 3 (Advanced / Stretch)
- Multiple users session (simple user switch)
- External transfers (between customers)
- Scheduled transactions
- Statement export to file
- Currency conversion (fixed rates)
- Role-based admin operations

## 4. Domain Model Overview
```
+----------------+        +----------------+        +------------------+
|   Customer     | 1    * |    Account     | 1    * |   Transaction    |
+----------------+        +----------------+        +------------------+
| id             |        | accountNumber  |        | id               |
| name           |        | type           |        | timestamp        |
| email          |        | balance        |        | type             |
| dateCreated    |        | status         |        | amount           |
| accounts (List)|        | owner (Customer)|       | balanceAfter     |
+----------------+        | transactions   |        | description      |
                          +----------------+        +------------------+
```

## 5. Class Specifications
### 5.1 `BankApplication` (Main Entrypoint)
Responsibility: Start program, show main loop, delegate to service layer.
Key Methods:
- `public static void main(String[] args)`
- `private void run()` – contains main loop.
- `private void showMainMenu()`
- `private void handleUserSelection(int choice)`
- `private int readInt(String prompt)` / `private String readLine(String prompt)` / `private BigDecimal readAmount(String prompt)` (input helpers with validation).

### 5.2 `Customer`
Fields:
- `private final String id` (UUID string)
- `private String name`
- `private String email`
- `private LocalDateTime dateCreated`
- `private List<Account> accounts`
Methods:
- Getters/Setters (no setter for id/dateCreated)
- `public void addAccount(Account account)`
- `public Optional<Account> findAccountByNumber(String accountNumber)`
- `public String toString()` (summarized view)

### 5.3 `Account` (Abstract Base or Concrete with enum Type)
Option A: Abstract class + subclasses `CheckingAccount`, `SavingsAccount`.
Option B (simpler): Single class with `AccountType type` enum.
Fields:
- `private final String accountNumber` (e.g., sequential or UUID substring)
- `private AccountType type`
- `private BigDecimal balance`
- `private AccountStatus status` (ACTIVE, CLOSED, FROZEN)
- `private Customer owner`
- `private List<Transaction> transactions`
Methods:
- `public void deposit(BigDecimal amount)` (validations)
- `public void withdraw(BigDecimal amount)` (validations / overdraft rule)
- `public void addTransaction(Transaction t)` (package-private)
- `public BigDecimal getBalance()`
- `public List<Transaction> getTransactions()` (unmodifiable copy)
- `public String toString()`

### 5.4 `Transaction`
Fields:
- `private final String id`
- `private LocalDateTime timestamp`
- `private TransactionType type` (DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN, INTEREST)
- `private BigDecimal amount`
- `private BigDecimal balanceAfter`
- `private String description`
Methods:
- Getters only (immutable)
- `public String toString()`
Factory: Static factory `of(type, amount, balanceAfter, description)`.

### 5.5 `AccountType` (enum)
Values: `CHECKING`, `SAVINGS`

### 5.6 `AccountStatus` (enum)
Values: `ACTIVE`, `CLOSED`, `FROZEN`

### 5.7 `TransactionType` (enum)
Values: `DEPOSIT`, `WITHDRAWAL`, `TRANSFER_IN`, `TRANSFER_OUT`, `INTEREST`

### 5.8 `BankService`
Responsibility: Core business operations.
Fields:
- `private Map<String, Customer> customersById`
- `private Map<String, Account> accountsByNumber`
Methods:
- `public Customer createCustomer(String name, String email)`
- `public Account openAccount(String customerId, AccountType type)`
- `public Optional<Customer> findCustomer(String customerId)`
- `public Optional<Account> findAccount(String accountNumber)`
- `public void deposit(String accountNumber, BigDecimal amount)`
- `public void withdraw(String accountNumber, BigDecimal amount)`
- `public void transfer(String fromAccount, String toAccount, BigDecimal amount)`
- `public List<Account> listAccounts(String customerId)`
- `public List<Transaction> getTransactions(String accountNumber)`
- Validation helpers (private): `validateAmount`, `requireAccount`, etc.

### 5.9 `PersistenceService` (Phase 2)
Interface:
- `void saveAll(Collection<Customer> customers)`
- `List<Customer> loadAll()`
Implementations: `FilePersistenceService` (JSON or serialized objects).

### 5.10 `InterestCalculator` (Phase 2+)
- `BigDecimal calculate(Account account)` (simple fixed annual rate / monthly accrual)

### 5.11 `MenuNavigator` (Optional Helper)
Encapsulate menu rendering & input parsing.

### 5.12 `InputValidator`
Static utility: `isValidEmail(String)`, `parseBigDecimal(String)`, etc.

## 6. Console Menu Design
### 6.1 Main Menu (Phase 1)
```
===== BANK MENU =====
1. Create Customer
2. Open Account
3. List Customer Accounts
4. Deposit
5. Withdraw
6. Transfer Between Accounts
7. View Account Balance
8. View Transaction History
9. Exit
Select option: _
```
Error: If invalid number -> "Invalid choice. Try again." (loop)

### 6.2 Option Behaviors
1. Create Customer:
   - Prompt: Name, Email
   - Validate non-empty; email simple contains '@'
   - Output: New Customer ID
2. Open Account:
   - Prompt: Customer ID, Account Type (1=Checking, 2=Savings)
   - Output: Account number
3. List Customer Accounts:
   - Prompt: Customer ID
   - Output: Table-like list: Number | Type | Balance
4. Deposit:
   - Prompt: Account Number, Amount
   - Validations: amount > 0
   - Output: New balance
5. Withdraw:
   - Prompt: Account Number, Amount
   - Validations: amount > 0; sufficient balance (or overdraft rule)
   - Output: New balance
6. Transfer Between Accounts:
   - Prompt: From Account, To Account, Amount
   - Validations: distinct accounts; same owner (Phase 1); funds available
   - Output: Both new balances (or summary)
7. View Account Balance:
   - Prompt: Account Number
   - Output: Balance with 2 decimal places
8. View Transaction History:
   - Prompt: Account Number
   - Output: Chronological lines: Time | Type | Amount | BalanceAfter | Description
9. Exit:
   - Confirm (Y/N). If Y -> terminate loop; if N -> return to menu.

### 6.3 Input Validation & Re-Prompting
- All numeric parse errors: show message "Invalid number. Please try again." and re-prompt.
- Amount zero or negative -> "Amount must be positive." re-prompt.
- Unknown IDs -> "Not found." return to main menu.

## 7. Functional Requirements (Phase 1)
FR1 Create customer with unique ID.
FR2 Open account for existing customer only.
FR3 Deposit increases balance and records transaction.
FR4 Withdraw decreases balance if sufficient funds; else show error and do not create transaction.
FR5 Transfer withdraws from source then deposits to target atomically (if source fails, target not affected).
FR6 View balance returns current accurate balance.
FR7 View transaction history returns ordered list (oldest to newest OR newest to oldest - choose and document; pick oldest->newest).
FR8 Application runs until Exit chosen.
FR9 All errors produce user-friendly messages; application never crashes on bad input.

## 8. Non-Functional Requirements
NFR1 Code style: meaningful names, small methods (<25 lines ideally).
NFR2 Use exceptions for error control, not return codes.
NFR3 No global mutable state outside service layer singletons.
NFR4 Handle up to 100 customers / 500 accounts comfortably in memory.
NFR5 Startup time < 1s (simple main method).
NFR6 Use `BigDecimal` for monetary arithmetic to avoid floating errors.
NFR7 Unit-testable design (service methods side-effect free except domain state changes).

## 9. Validation Rules
- Name: not blank, length <= 60.
- Email: contains '@' and '.' after '@' (simple check).
- Amount: > 0, scale 2 (enforce with `setScale(2, RoundingMode.HALF_UP)`).
- Transfer: accounts must exist and not identical; both ACTIVE.
- Withdrawal: balance >= amount (Phase 1). Savings/Checking identical rule initially.

## 10. Error Handling Strategy
Use custom unchecked exceptions (optional):
- `CustomerNotFoundException`
- `AccountNotFoundException`
- `InsufficientFundsException`
- `ValidationException`
Catch in UI layer, print message, return to menu.

## 11. Transaction Recording Rules
- Every successful deposit/withdraw/transfer creates two transactions (transfer out + transfer in).
- Store `balanceAfter` snapshot.
- Description examples:
  - Deposit: "Deposit"
  - Withdrawal: "Withdrawal"
  - Transfer Out: "Transfer to {toAccount}"
  - Transfer In: "Transfer from {fromAccount}"

## 12. Sample Console Flow
```
===== BANK MENU =====
1. Create Customer
...
Select option: 1
Enter name: Alice Smith
Enter email: alice@example.com
Customer created. ID: C-8f9a2d

Select option: 2
Enter customer ID: C-8f9a2d
Select account type (1=Checking, 2=Savings): 1
Account created. Number: A-100001

Select option: 4
Enter account number: A-100001
Enter amount to deposit: 250
Deposit successful. New balance: 250.00

Select option: 5
Enter account number: A-100001
Enter amount to withdraw: 400
Error: Insufficient funds.

Select option: 7
Enter account number: A-100001
Balance: 250.00

Select option: 8
Enter account number: A-100001
2025-09-14T10:12:05 | DEPOSIT | 250.00 | 250.00 | Deposit

Select option: 9
Are you sure you want to exit? (Y/N): Y
Goodbye!
```

## 13. Edge Cases & Scenarios
- Deposit 0 or negative -> reject.
- Withdraw more than balance -> reject.
- Transfer to same account -> reject.
- Create customer with duplicate email (optional rule: allow or reject; choose: allow in Phase 1)
- Large amounts (use BigDecimal, no overflow expected in practice).
- No transactions yet -> show "No transactions found." message.
- Empty customer ID -> validation error.

## 14. Persistence Design (Phase 2 Option)
Simple JSON file structure example:
```
{
  "customers": [
    {
      "id": "C-8f9a2d",
      "name": "Alice Smith",
      "email": "alice@example.com",
      "dateCreated": "2025-09-14T10:10:00",
      "accounts": [
        {
          "accountNumber": "A-100001",
          "type": "CHECKING",
          "status": "ACTIVE",
          "balance": 250.00,
          "transactions": [
            { "id": "T-1", "timestamp": "2025-09-14T10:12:05", "type": "DEPOSIT", "amount": 250.00, "balanceAfter": 250.00, "description": "Deposit" }
          ]
        }
      ]
    }
  ]
}
```
On startup: read file if exists. On exit: write file.

## 15. Interest Calculation (Phase 2)
- Add annual rate constant (e.g., 3% APY) for savings.
- Monthly interest = balance * (rate / 12).
- Apply by menu option: "Apply Monthly Interest" (adds INTEREST transaction).

## 16. Testing Guidance
- Unit test `BankService` methods: deposit, withdraw, transfer with JUnit.
- Test insufficient funds scenario.
- Test transaction history ordering.
- Test that transfer creates two transactions with correct amounts.
- Test email validation utility.

## 17. Implementation Roadmap
1. Define enums & domain classes (`Customer`, `Account`, `Transaction`).
2. Implement `BankService` with in-memory maps.
3. Build console UI loop in `BankApplication`.
4. Add validation & exception handling.
5. Add transaction history viewing.
6. (Optional) Introduce persistence layer.
7. Add interest logic & additional features.
8. Write tests for service layer.

## 18. Stretch Enhancements (Ideas)
- PIN-based login per customer.
- Masked input for PIN (not trivial in pure console; can skip).
- Sorting / filtering transactions by date range.
- Export statement to `statement-<account>.txt`.
- Multi-currency with `Currency` enum and conversion table.
- Command history & undo (complex).
- Scheduled interest auto-apply (timer thread).

## 19. Suggested Package Structure
```
com.mybank
  |-- Main.java (or BankApplication.java)
  |-- model
  |     |-- Customer.java
  |     |-- Account.java
  |     |-- Transaction.java
  |     |-- AccountType.java
  |     |-- AccountStatus.java
  |     |-- TransactionType.java
  |-- service
  |     |-- BankService.java
  |     |-- PersistenceService.java
  |     |-- FilePersistenceService.java (phase 2)
  |-- util
  |     |-- InputValidator.java
  |-- exception
        |-- ValidationException.java
        |-- InsufficientFundsException.java
        |-- AccountNotFoundException.java
        |-- CustomerNotFoundException.java
```

## 20. Completion Criteria (Phase 1)
- All Phase 1 menu options implemented & functioning.
- No uncaught exceptions on invalid user input.
- Transactions recorded correctly.
- Service layer unit tests passing (if added).
- Code compiled & runs via `javac` / `java`.

---
Use this document as a blueprint. Start small: implement Phase 1 fully before adding persistence or interest. Good luck and enjoy building it!

## 21. Method-Level Specifications (Detailed)

Format Legend:
Purpose – What the method is for.
Parameters – Name: type – meaning / constraints.
Validations – Checks performed (throw `ValidationException` unless otherwise specified).
Returns – Object/value returned.
Exceptions – Custom or standard exceptions that may be thrown.
Side Effects – Mutations of state.
Example – Illustrative usage.

### 21.1 BankService

#### createCustomer(String name, String email)
Purpose: Register a new customer and store in memory.
Parameters:
- name: String – non-blank, length ≤ 60.
- email: String – must contain '@' and a '.' after '@'.
Validations: Reject blank name, invalid email.
Process:
1. Generate ID: e.g., "C-" + short UUID or increment counter.
2. Create `Customer` instance with empty account list, `dateCreated = now`.
3. Put into `customersById` map.
Returns: The created `Customer`.
Exceptions: `ValidationException` if invalid input.
Side Effects: Mutates internal map.
Example: `Customer c = bankService.createCustomer("Alice Smith", "alice@example.com");`

#### openAccount(String customerId, AccountType type)
Purpose: Create a new account for an existing customer.
Parameters:
- customerId: String – must exist.
- type: AccountType – not null.
Validations: Customer exists; type not null.
Process:
1. Fetch customer; if absent throw `CustomerNotFoundException`.
2. Generate account number (e.g., "A-" + sequence).
3. Create account with balance 0, status ACTIVE.
4. Append to customer.accounts and maps.
Returns: The created `Account`.
Exceptions: `CustomerNotFoundException`, `ValidationException`.
Side Effects: Updates two maps and customer list.
Example: `Account a = bankService.openAccount(c.getId(), AccountType.CHECKING);`

#### findCustomer(String customerId)
Purpose: Lookup a customer.
Parameters: customerId – non-null.
Returns: Optional containing customer or empty.
Exceptions: (None; returns Optional).
Side Effects: None.
Example: `bankService.findCustomer(id).ifPresent(System.out::println);`

#### findAccount(String accountNumber)
Purpose: Lookup an account.
Returns: Optional<Account>.
Side Effects: None.

#### deposit(String accountNumber, BigDecimal amount)
Purpose: Increase balance and record transaction.
Parameters:
- accountNumber – must exist.
- amount – > 0, scale normalized to 2.
Validations: Amount positive; account ACTIVE.
Process:
1. Retrieve account.
2. Normalize amount = amount.setScale(2, HALF_UP).
3. account.deposit(amount) (may internally add transaction or caller adds transaction).
4. Create Transaction(type=DEPOSIT, amount, balanceAfter, description="Deposit").
Exceptions: `AccountNotFoundException`, `ValidationException`.
Side Effects: Mutates account balance & transactions list.
Example: `bankService.deposit("A-100001", new BigDecimal("50"));`

#### withdraw(String accountNumber, BigDecimal amount)
Purpose: Decrease balance if sufficient funds.
Validations: Amount > 0; balance >= amount; status ACTIVE.
Process:
1. Retrieve account.
2. Check funds; if insufficient throw `InsufficientFundsException`.
3. Subtract amount; record transaction (WITHDRAWAL).
Exceptions: `AccountNotFoundException`, `InsufficientFundsException`, `ValidationException`.
Side Effects: Balance decreases; transaction appended.
Example: `bankService.withdraw("A-100001", new BigDecimal("25.75"));`

#### transfer(String fromAccount, String toAccount, BigDecimal amount)
Purpose: Move funds internally between two accounts (same owner Phase 1).
Validations: Distinct account numbers; both exist & ACTIVE; amount > 0; from has sufficient funds.
Process (Atomic Intention):
1. Load both accounts.
2. Validate owner equality (Phase 1).
3. Check balance.
4. Subtract from source; add to target.
5. Create two transactions: TRANSFER_OUT (source) & TRANSFER_IN (target).
Atomicity Note: If any validation fails before mutation, no state changes. Ensure debit then credit happens sequentially; if credit fails (should not in Phase 1), rollback debit (could be manual add-back or perform all checks prior to mutation).
Exceptions: `AccountNotFoundException`, `ValidationException`, `InsufficientFundsException`.
Side Effects: Two balances and two transaction lists modified.
Example: `bankService.transfer("A-1", "A-2", new BigDecimal("10.00"));`

#### listAccounts(String customerId)
Purpose: Retrieve all accounts for a customer.
Returns: List<Account> (unmodifiable copy recommended).
Exceptions: `CustomerNotFoundException` if id unknown (design choice; or return empty).
Side Effects: None.
Example: `for(Account a: bankService.listAccounts(c.getId())) { ... }`

#### getTransactions(String accountNumber)
Purpose: Retrieve transaction history ordered oldest->newest.
Returns: List<Transaction> (unmodifiable copy).
Exceptions: `AccountNotFoundException`.
Side Effects: None.

### 21.2 Account (if concrete) / Account Base

#### deposit(BigDecimal amount)
Purpose: Add funds to balance.
Validations: amount > 0; status ACTIVE.
Process: balance = balance.add(amount); record transaction externally or internally.
Returns: void.
Exceptions: `ValidationException`.
Side Effects: Balance mutated.

#### withdraw(BigDecimal amount)
Purpose: Remove funds.
Validations: amount > 0; balance >= amount; status ACTIVE.
Process: balance = balance.subtract(amount).
Returns: void.
Exceptions: `InsufficientFundsException`, `ValidationException`.
Side Effects: Balance mutated.

#### addTransaction(Transaction t)
Purpose: Append transaction to list.
Validations: t not null.
Side Effects: transactions list grows.

#### getTransactions()
Purpose: Access history safely.
Returns: Unmodifiable List (e.g., `Collections.unmodifiableList`).
Side Effects: None.

### 21.3 Customer

#### addAccount(Account account)
Purpose: Associate new account to customer.
Validations: account not null; account.owner == this (optionally enforce).
Side Effects: accounts list mutated.

#### findAccountByNumber(String accountNumber)
Purpose: Search in customer accounts.
Returns: Optional<Account>.
Side Effects: None.

### 21.4 Transaction (Factory)

#### static Transaction of(TransactionType type, BigDecimal amount, BigDecimal balanceAfter, String description)
Purpose: Centralized creation ensuring immutability & consistent timestamp/id.
Validations: type != null; amount >= 0; balanceAfter != null; description not null (allow empty).
Process: Generate id ("T-" + sequence/UUID); set timestamp=now.
Returns: Transaction.
Exceptions: `ValidationException`.
Side Effects: None.

### 21.5 PersistenceService (Phase 2)

#### saveAll(Collection<Customer> customers)
Purpose: Persist domain graph to storage medium.
Validations: customers not null.
Process: Serialize customers & nested accounts & transactions.
Returns: void.
Exceptions: `IOException` (if chosen to be checked) or custom `PersistenceException`.
Side Effects: Writes file.

#### loadAll()
Purpose: Rehydrate domain objects from storage.
Returns: List<Customer> (never null; empty if file missing).
Exceptions: `IOException` / `PersistenceException`.
Side Effects: Reads file system.

### 21.6 InterestCalculator

#### calculate(Account account)
Purpose: Determine interest to credit based on policy.
Validations: account.type == SAVINGS; status ACTIVE.
Returns: BigDecimal interestAmount (scale 2) or zero if below threshold.
Exceptions: `ValidationException` if wrong account type.
Side Effects: None (calculation only).

### 21.7 InputValidator (Utility)

#### isValidEmail(String email)
Returns: boolean – simple pattern check.

#### parseBigDecimal(String raw)
Purpose: Convert user input to BigDecimal scale 2.
Validations: numeric and >= 0.
Returns: BigDecimal.
Exceptions: `NumberFormatException` or wrap into `ValidationException`.

### 21.8 BankApplication (UI Helpers)

#### run()
Purpose: Control main loop until exit flag set.
Side Effects: Repeatedly reads input, writes output.

#### showMainMenu()
Purpose: Print menu header & options.
Returns: void.

#### handleUserSelection(int choice)
Purpose: Dispatch to appropriate UI action.
Validations: choice within known range.
Side Effects: Calls service methods, prints results.

#### readInt(String prompt)
Purpose: Prompt & parse integer with re-try loop.
Returns: int.
Exceptions: (None to caller — loop until valid).

#### readLine(String prompt)
Purpose: Basic string input (trimmed).
Returns: String (may be empty; caller validates).

#### readAmount(String prompt)
Purpose: Acquire positive BigDecimal amount.
Process: loop until > 0.
Returns: BigDecimal scaled to 2.

### 21.9 Exception Classes
Each custom exception extends `RuntimeException` (Phase 1 simplicity).
- `ValidationException` – For invalid arguments/business rules.
- `CustomerNotFoundException` – When a customer lookup fails.
- `AccountNotFoundException` – When an account lookup fails.
- `InsufficientFundsException` – Withdrawal/transfer insufficiency.
Constructors: `(String message)` + optional `(String message, Throwable cause)`.

### 21.10 Method Interaction Summary
Typical flow for a transfer UI action:
1. UI gathers from, to, amount.
2. Calls `bankService.transfer(...)`.
3. Service validates accounts & funds.
4. Service updates balances and appends transactions.
5. UI prints new balances (via account getters or re-query).

### 21.11 Testing Notes per Method
- createCustomer: assert ID not null; list size increments.
- openAccount: balance zero; correct owner reference.
- deposit: balance increases exact; transaction appended with correct balanceAfter.
- withdraw: insufficient funds throws; successful updates balance.
- transfer: two transactions created; net zero sum across accounts.
- getTransactions: ordering stable; defensive copy (mod attempts throw).

### 21.12 Future Extension Hooks
- Add `closeAccount(String accountNumber)` (validate zero balance then status=CLOSED).
- Add `applyMonthlyInterest(String accountNumber)` using InterestCalculator then deposit interest as INTEREST transaction.
- Add pagination parameters to `getTransactions`.

## 22. Phase 2 Detailed Specifications (Enhancements)

### 22.1 Goals
Introduce persistence, interest logic, richer account lifecycle management, and basic authentication to simulate more realistic banking scenarios while reinforcing abstraction and separation of concerns.

### 22.2 New / Expanded Features
1. Persistence (Load/Save on Startup/Exit)
2. Interest Application for Savings Accounts
3. Account Status Management (CLOSE, FREEZE/UNFREEZE)
4. Overdraft Handling Rule (Reject or Limit)
5. Basic Customer Authentication (Optional PIN)
6. Extended Transaction History Filtering (by type, date range optionally)
7. Account Closure Rules

### 22.3 Persistence Details
Purpose: Allow session continuity between application runs.
Approach: JSON file `bank-data.json` (single file) or serialized object file.
Structure: See Section 14. Add root field `"schemaVersion": 1`.
Lifecycle:
- On startup: attempt read; if missing -> start empty.
- On exit: write snapshot (only ACTIVE + CLOSED accounts, FROZEN included with state).
Atomicity: Write to temp file then rename.
Error Handling: If load fails (malformed), log error (print) and continue with empty state.
Extensibility: Future versions increment `schemaVersion` and implement simple migration.

### 22.4 Interest Logic (Savings Only)
Configuration: Flat annual rate (e.g., 3%) constant in `InterestCalculator`.
Formula: monthlyInterest = balance * (annualRate / 12). Round HALF_UP scale 2. Skip if < $0.01.
Application Methods:
- Manual menu option: "Apply Monthly Interest" (per account).
- (Optional) Batch apply for all savings accounts.
Transaction Recording: Type = INTEREST, description = "Monthly interest".
Validation: Only SAVINGS + ACTIVE accounts.

### 22.5 Account Status Lifecycle
Statuses: ACTIVE, FROZEN, CLOSED.
Operations Allowed:
- Deposit: ACTIVE (and optionally FROZEN if you choose) – default: FROZEN disallows withdrawals only.
- Withdraw/Transfer Source: ACTIVE only.
- Transfer Target: ACTIVE (optionally allow FROZEN deposit side – default: disallow for simplicity).
Freeze: Administrative or security action (Phase 2 menu) -> change to FROZEN.
Unfreeze: Back to ACTIVE.
Close: Preconditions: balance == 0, status ACTIVE or FROZEN (must unfreeze or allow direct). After CLOSE: No further transactions.

### 22.6 Overdraft Handling Rule
Phase 2 Option A (Simple): Reject if balance < amount (already Phase 1).
Phase 2 Option B (Introduce Limit): Add field `overdraftLimit` to Account (Checking only) default 0; designer sets maybe 100. Withdraw allowed if balance + overdraftLimit >= amount; resulting balance may be negative (store negative BigDecimal). Negative balance disallows interest accrual.
If using Option B: Add Transaction description: "Withdrawal (Overdraft)".

### 22.7 Basic Authentication (Optional)
Simple PIN per Customer:
- New field: `pinHash` (store hashed PIN) – hashing optional for learning; if plain, document risk.
Menu Flow:
1. At startup ask: "Enable authentication? (Y/N)".
2. If yes: user must login (enter Customer ID + PIN) before performing operations except Create Customer & Exit.
3. Failed login attempts tracked; 3 consecutive failures -> temporary lock (simulate by requiring restart or simple wait message).
Session Handling: Store current logged-in customer ID; restrict list/open operations to that user (unless Multi-user in Phase 3).

### 22.8 Extended Menu (Phase 2)
Add options (renumber or append after 9):
10. Apply Monthly Interest (Savings)
11. Freeze Account
12. Unfreeze Account
13. Close Account
14. Filter Transactions (by Type / Date Range)
15. Login (if auth enabled) / Switch User

### 22.9 Additional Validation Rules (Phase 2)
- Close Account: balance must be zero; status != CLOSED.
- Freeze/Unfreeze: Account must not be CLOSED.
- Interest: Only if balance > 0.
- Overdraft (if enabled): Negative balance cannot exceed -overdraftLimit.
- Auth: PIN must be 4–6 digits.

### 22.10 New/Adjusted Methods
Potential Additions to `BankService`:
- `public void applyMonthlyInterest(String accountNumber)`
- `public void freezeAccount(String accountNumber)`
- `public void unfreezeAccount(String accountNumber)`
- `public void closeAccount(String accountNumber)`
- `public List<Transaction> getTransactions(String accountNumber, TransactionType typeFilter, LocalDate from, LocalDate to)`
Authentication (if included) may go in separate `AuthService`:
- `public void setPin(String customerId, String rawPin)`
- `public boolean authenticate(String customerId, String rawPin)`

### 22.11 Phase 2 Acceptance Criteria
AC-P2-1: Persistence: Data created in a run is available next run.
AC-P2-2: Interest: Applying interest creates INTEREST transaction with correct amount & updated balance.
AC-P2-3: Freeze/Unfreeze: Frozen account rejects withdrawals and transfers as source.
AC-P2-4: Close: Closed account rejects all monetary operations.
AC-P2-5: Overdraft (if implemented): Balance allowed below zero but not past limit; transactions reflect negative balances.
AC-P2-6: Auth (if enabled): Protected operations require successful login.
AC-P2-7: Transaction filtering returns only matching records, unchanged ordering.

### 22.12 Data Migration Considerations
When adding new fields (e.g., `status`, `pinHash`), default missing to sensible values (ACTIVE, null). Implement simple version detection using `schemaVersion`.

## 23. Phase 3 Detailed Specifications (Advanced / Stretch)

### 23.1 Goals
Simulate richer banking domain: multiple users, cross-customer transfers, scheduling, reporting, currency support, and role-based administration—fostering design for scalability and extensibility.

### 23.2 New / Expanded Features
1. Multi-User Sessions (switch between logged-in customers)
2. External Transfers (customer A -> customer B)
3. Scheduled Transactions (execute at future timestamp)
4. Statement Export (TXT / CSV)
5. Multi-Currency Accounts (optional conversion)
6. Role-Based Operations (Admin vs Standard)
7. Performance & Metrics (basic counters)
8. Transaction Pagination

### 23.3 Multi-User Session Handling
Maintain current session ID. Menu adds: "Switch User" flow. Operations restricted to session's accounts except admin functions. Provide `logout` option.

### 23.4 External Transfers
Validation: Source != target; both accounts exist; ownership can differ; apply optional transfer fee (flat or percentage). Two transactions still recorded (TRANSFER_OUT / TRANSFER_IN) with description referencing other account.
Optional: Fee transaction separate with type FEE (add to `TransactionType`).

### 23.5 Scheduled Transactions
Data Structure: `List<ScheduledTransaction>` with fields: id, runAt (LocalDateTime), fromAccount, toAccount (nullable for deposit), amount, type.
Processing: On each loop iteration (or via a `Scheduler` tick method), check due items (runAt <= now), execute, log result, mark executed.
Edge Cases: If insufficient funds at execution time -> mark FAILED with reason.

### 23.6 Statement Export
Format: Plain text or CSV: header (AccountNumber, Owner, Period Start/End), then lines of Date, Type, Amount, BalanceAfter, Description.
File Name: `statement-<accountNumber>-YYYYMMDD-HHMM.txt`.
Trigger: Menu option.
Validation: Account exists & belongs to current user (unless admin).

### 23.7 Multi-Currency Support
Add `Currency` enum (USD, EUR, GBP...). Each Account has a currency field.
Transfers across currencies use fixed rate table (e.g., Map<Pair<Currency,Currency>, BigDecimal>). Rate applied to OUT -> converted amount credited IN. Record description: "Transfer from X (converted)".
Interest: Rate applied before interest; interest uses account's own currency.

### 23.8 Role-Based Access
Add field `role` to Customer: USER, ADMIN.
Admin Capabilities: Freeze/unfreeze any account, list all customers, run batch interest, export all statements.
Menu branch displays admin-only options if session user is ADMIN.

### 23.9 Performance & Metrics
Track counters: totalTransactions, totalFailedTransactions, lastInterestRunTime.
Add menu option: "View System Metrics" (admin).

### 23.10 Transaction Pagination
Enhance history retrieval with parameters: pageNumber, pageSize. Validate page boundaries (return empty list if beyond end).

### 23.11 Additional Methods (Possible)
- `public void scheduleTransfer(... )`
- `public List<ScheduledTransaction> listScheduled(String customerId)`
- `public void exportStatement(String accountNumber, LocalDate start, LocalDate end, Path outFile)`
- `public List<Transaction> getTransactions(String accountNumber, int page, int size)`
- `public void applyBatchInterest()` (admin)

### 23.12 Additional Validation Rules (Phase 3)
- Scheduled transaction time must be in future.
- Cross-currency conversion must have known rate.
- Admin-only actions require role check; else throw `AuthorizationException` (new).
- Fee must not push balance below overdraft limit (if enabled).

### 23.13 New/Extended Enums / Classes
- `TransactionType`: add FEE (optional), SCHEDULED_EXECUTION (optional for audit), FAILED (optional)
- `Role` enum: USER, ADMIN
- `ScheduledTransaction` class
- `AuthorizationException` (extends RuntimeException)

### 23.14 Phase 3 Acceptance Criteria
AC-P3-1: External transfer between two customers records correct transactions.
AC-P3-2: Scheduled transaction executes at or after scheduled time; creates appropriate transaction(s).
AC-P3-3: Statement export file created with correct line count and ordering.
AC-P3-4: Currency conversion transfers reflect correct converted amount using defined rate.
AC-P3-5: Admin-only menu options hidden from USER accounts.
AC-P3-6: Pagination returns correct segment with stable ordering.
AC-P3-7: Metrics reflect cumulative counts after operations.

### 23.15 Non-Functional Enhancements
- Document complexity & potential refactoring points (e.g., service grows large -> modularization).
- Consider introducing interfaces: `AccountRepository`, `TransactionRepository` for testability.
- Introduce simple logging abstraction for audit trail (persist logs optionally).

### 23.16 Suggested Refactors Before Phase 3
- Extract repository layer.
- Implement `TransactionFactory` and `InterestPolicy` (if not yet done).
- Convert validation to fluent builder or centralized `Validator` class.


