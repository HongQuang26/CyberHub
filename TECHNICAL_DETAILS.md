# Chi tiết Kỹ thuật - CyberHub Updates

## Kiến trúc Hệ thống

### Tổng quan
```
┌─────────────────────────────────────────────────────────┐
│                   CyberHub Application                   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐      ┌──────────────────────────┐    │
│  │  LoginFrame  │──────│  User Authentication     │    │
│  └──────────────┘      └──────────────────────────┘    │
│         │                                                │
│         ├─────── Admin? ─────┐                          │
│         │                     │                          │
│         ▼                     ▼                          │
│  ┌─────────────────┐   ┌─────────────────┐             │
│  │ CustomerDashboard│   │  AdminDashboard │             │
│  └─────────────────┘   └─────────────────┘             │
│         │                     │                          │
│    ┌────┴────┬────────┐      ├────┬────┬────┐          │
│    ▼         ▼        ▼       ▼    ▼    ▼    ▼          │
│  Select   Order   Account   PC  Cust Food Stats        │
│  Computer  Food   Info      Mgmt Mgmt Mgmt             │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## Luồng Dữ liệu (Data Flow)

### 1. Customer Login Flow
```
LoginFrame
    │
    ├─> UserDao.login(username, password)
    │       │
    │       ├─> Database Query: SELECT * FROM users WHERE username = ? AND password = ?
    │       │
    │       └─> Return Customer object
    │
    └─> Create CustomerDashboardFrame(customer)
            │
            ├─> Load computers (ComputerDao)
            ├─> Load food menu (FoodDao)
            └─> Display customer info
```

### 2. Food Ordering Flow
```
CustomerDashboardFrame (Food Tab)
    │
    ├─> Select food item from table
    │
    ├─> Click "Đặt món" button
    │
    ├─> Input quantity dialog
    │
    ├─> Calculate: totalPrice = price × quantity
    │
    ├─> Validate: customer.balance >= totalPrice
    │   ├─> NO: Show error "Số dư không đủ"
    │   └─> YES: Continue
    │
    ├─> Confirm order dialog
    │
    ├─> FoodDao.createFoodOrder(userId, foodId, quantity, totalPrice)
    │       │
    │       └─> Database: INSERT INTO food_orders ...
    │
    ├─> UserDao.updateBalance(userId, newBalance)
    │       │
    │       └─> Database: UPDATE users SET balance = newBalance ...
    │
    └─> Success message + Refresh account info
```

### 3. Money Formatting Flow
```
User Input in TextField
    │
    ├─> MoneyDocumentFilter intercepts
    │
    ├─> Remove all non-digit characters
    │
    ├─> Parse to long value
    │
    ├─> Format with DecimalFormat
    │   Example: 50000 → "50.000"
    │
    └─> Display formatted text in TextField
```

---

## Cấu trúc Database

### Existing Tables
```sql
users (user_id, username, password, full_name, role, balance, created_at)
computers (computer_id, computer_name, status, created_at)
sessions (session_id, user_id, computer_id, start_time, end_time, total_cost)
transactions (transaction_id, user_id, amount, transaction_type, transaction_date, description)
```

### New Tables
```sql
-- Food Menu
food_menu
├── food_id (PK, AUTO_INCREMENT)
├── food_name (VARCHAR(100), NOT NULL)
├── price (DECIMAL(10,2), NOT NULL)
├── category (VARCHAR(50), NOT NULL)
├── available (BOOLEAN, DEFAULT TRUE)
└── created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

-- Food Orders
food_orders
├── order_id (PK, AUTO_INCREMENT)
├── user_id (FK → users.user_id, NOT NULL)
├── food_id (FK → food_menu.food_id, NOT NULL)
├── quantity (INT, NOT NULL, DEFAULT 1)
├── total_price (DECIMAL(10,2), NOT NULL)
├── status (ENUM('PENDING','DELIVERED','CANCELLED'), DEFAULT 'PENDING')
└── order_time (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
```

### Relationships
```
users (1) ──< (N) food_orders
food_menu (1) ──< (N) food_orders
```

---

## Class Diagram

### Model Layer
```
User (abstract)
├── userId: int
├── username: String
├── password: String
├── fullName: String
├── role: String
└── methods...

Admin extends User
└── (no additional fields)

Customer extends User
├── balance: BigDecimal
└── methods...

Computer
├── computerId: int
├── computerName: String
├── status: String (AVAILABLE, IN_USE, MAINTENANCE)
└── methods...

FoodItem (NEW)
├── foodId: int
├── foodName: String
├── price: BigDecimal
├── category: String
├── available: boolean
└── methods...

FoodOrder (NEW)
├── orderId: int
├── userId: int
├── foodId: int
├── quantity: int
├── totalPrice: BigDecimal
├── status: String (PENDING, DELIVERED, CANCELLED)
├── orderTime: Timestamp
└── methods...
```

### DAO Layer
```
UserDao
├── login(username, password): User
├── getAllCustomers(): List<Customer>
├── addCustomer(username, password, fullName): boolean
├── updateBalance(userId, newBalance): boolean
└── deleteCustomer(userId): boolean

ComputerDao
├── getAllComputers(): List<Computer>
└── updateComputerStatus(computerId, status): boolean

FoodDao (NEW)
├── getAllFoodItems(): List<FoodItem>
├── getAllFoodItemsIncludingUnavailable(): List<FoodItem>
├── addFoodItem(name, price, category): boolean
├── updateFoodItem(id, name, price, category, available): boolean
├── deleteFoodItem(foodId): boolean
├── createFoodOrder(userId, foodId, quantity, totalPrice): boolean
└── getOrdersByUser(userId): List<FoodOrder>
```

### View Layer
```
LoginFrame
├── usernameField: JTextField
├── passwordField: JPasswordField
├── loginButton: JButton
└── handleLogin(): void

AdminDashboardFrame
├── computerPanel: JPanel
├── customerPanel: JPanel
├── foodMenuPanel: JPanel (NEW)
├── transactionPanel: JPanel
└── methods for each panel...

CustomerDashboardFrame (NEW)
├── computerPanel: JPanel
├── foodPanel: JPanel
├── accountPanel: JPanel
├── refreshComputerStatus(): void
├── refreshFoodMenu(): void
├── refreshAccountInfo(): void
├── selectComputer(computer): void
└── showOrderDialog(): void
```

### Utility Layer
```
InputValidator
├── isValidUsername(username): boolean
├── isValidPassword(password): boolean
├── isValidFullName(fullName): boolean
├── isValidAmount(amount): boolean
└── get*ErrorMessage(): String methods

MoneyFormatter (NEW)
├── format(BigDecimal): String
├── format(long): String
├── parse(String): BigDecimal
├── applyMoneyFormat(JTextField): void
└── MoneyDocumentFilter (inner class)
    ├── insertString(...): void
    ├── remove(...): void
    ├── replace(...): void
    └── helper methods
```

---

## MoneyFormatter Implementation Details

### Format Process
```java
Input: BigDecimal(50000)
    ↓
DecimalFormat with Vietnamese locale
- Grouping separator: '.'
- Decimal separator: ','
- Pattern: "#,##0"
    ↓
Output: "50.000"
```

### Parse Process
```java
Input: "50.000"
    ↓
Remove dots: "50000"
    ↓
Parse to BigDecimal
    ↓
Output: BigDecimal(50000)
```

### DocumentFilter Logic
```
User types: '5'
    ↓ MoneyDocumentFilter.insertString()
    ↓ Extract digits: "5"
    ↓ Format: "5"
    ↓ Display: "5"

User types: '0'
    ↓ Current: "5", New char: '0'
    ↓ Combined digits: "50"
    ↓ Format: "50"
    ↓ Display: "50"

User types: '0'
    ↓ Current: "50", New char: '0'
    ↓ Combined digits: "500"
    ↓ Format: "500"
    ↓ Display: "500"

User types: '0'
    ↓ Current: "500", New char: '0'
    ↓ Combined digits: "5000"
    ↓ Format: "5.000"
    ↓ Display: "5.000"

User types: '0'
    ↓ Current: "5.000" → digits "5000", New char: '0'
    ↓ Combined digits: "50000"
    ↓ Format: "50.000"
    ↓ Display: "50.000"
```

---

## Security Considerations

### Current Implementation
- ✅ SQL injection prevention (PreparedStatement)
- ✅ Input validation before database operations
- ✅ Foreign key constraints for data integrity
- ⚠️ Passwords stored in plain text (not recommended for production)

### Recommendations for Production
1. **Password Hashing**: Implement PasswordHasher utility
   ```java
   String hashedPassword = PasswordHasher.hashPassword(password);
   ```
2. **Session Management**: Add user session tracking
3. **Audit Logging**: Log all transactions and changes
4. **Role-based Access Control**: More granular permissions

---

## Performance Considerations

### Database Operations
- All queries use PreparedStatement (compiled once, execute multiple times)
- Connection pooling recommended for production
- Indexes on frequently queried columns (user_id, food_id, status)

### GUI Performance
- Swing EDT used properly (all UI updates on Event Dispatch Thread)
- DocumentFilter efficient for real-time formatting
- Table models set to non-editable to prevent accidental changes

### Memory Management
- Database connections properly closed with try-with-resources
- No memory leaks in listeners or filters
- BigDecimal used for precision (no floating-point errors)

---

## Testing Scenarios

### Unit Testing (Recommended)
```java
// Example test cases
@Test
public void testMoneyFormat() {
    assertEquals("1.000", MoneyFormatter.format(new BigDecimal("1000")));
    assertEquals("50.000", MoneyFormatter.format(new BigDecimal("50000")));
    assertEquals("1.000.000", MoneyFormatter.format(new BigDecimal("1000000")));
}

@Test
public void testMoneyParse() throws ParseException {
    assertEquals(new BigDecimal("1000"), MoneyFormatter.parse("1.000"));
    assertEquals(new BigDecimal("50000"), MoneyFormatter.parse("50.000"));
}

@Test
public void testValidateAmount() {
    assertTrue(InputValidator.isValidAmount(new BigDecimal("1000")));
    assertFalse(InputValidator.isValidAmount(new BigDecimal("-100")));
    assertFalse(InputValidator.isValidAmount(new BigDecimal("200000000")));
}
```

### Integration Testing
1. **Login Flow**: Test both admin and customer login
2. **Computer Selection**: Test selecting available computers
3. **Food Ordering**: Test with sufficient and insufficient balance
4. **Menu Management**: Test CRUD operations
5. **Money Formatting**: Test input and display

### Manual Testing Checklist
- [ ] Admin login successful
- [ ] Customer login successful
- [ ] Computer status displays correctly
- [ ] Computer selection works
- [ ] Food menu loads in customer view
- [ ] Food ordering with balance check
- [ ] Balance updates after order
- [ ] Admin can add food items
- [ ] Admin can edit food items
- [ ] Admin can delete food items
- [ ] Money formatting in all input fields
- [ ] Money display with dots in all tables
- [ ] Top-up with formatted input
- [ ] Validation messages display correctly

---

## Known Limitations

1. **Database Connection**: Requires MySQL server running
2. **No Session Timeout**: Users stay logged in until manual logout
3. **Single Instance**: No multi-user session management
4. **Time Tracking**: Computer usage time not tracked yet
5. **Order Status**: Food order status not updateable (always PENDING)
6. **Transaction History**: Not fully implemented yet

---

## Future Enhancements

### Short-term (Easy to add)
1. Session timeout after inactivity
2. Confirm dialog when closing application
3. Remember last logged-in user (optional)
4. Export reports to CSV/PDF
5. Search/filter functionality in tables

### Medium-term (Moderate effort)
1. Time-based billing for computer usage
2. Order status management (PENDING → DELIVERED)
3. Order history view for customers
4. Transaction history with filtering
5. Revenue statistics and charts

### Long-term (Major features)
1. Real-time synchronization (WebSocket)
2. Mobile app integration
3. Printer integration for receipts
4. Membership tiers and discounts
5. Automated backup system

---

## Deployment Guide

### Prerequisites
1. Java JDK 17 or higher
2. MySQL Server 5.7 or higher
3. MySQL Connector/J 8.0.33

### Installation Steps
1. Clone repository
2. Run database_schema.sql
3. Configure database connection (database.properties)
4. Compile: `./com/compile.sh`
5. Run: `./com/run.sh`

### Configuration
```properties
# database.properties
db.url=jdbc:mysql://localhost:3306/net_cafe_management
db.username=root
db.password=yourpassword
```

---

## Support and Maintenance

### Troubleshooting
- **Connection Error**: Check MySQL service is running
- **Compilation Error**: Verify JDK version (17+)
- **Format Not Working**: Check locale settings
- **Balance Not Updating**: Check database foreign key constraints

### Logging
- Enable logging in DatabaseConnector for debugging
- SQLException stack traces printed to console
- Add log4j/slf4j for production logging

### Backup
```bash
# Backup database
mysqldump -u root -p net_cafe_management > backup.sql

# Restore database
mysql -u root -p net_cafe_management < backup.sql
```
