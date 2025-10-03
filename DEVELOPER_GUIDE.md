# CyberHub Developer Guide

## Cấu trúc dự án (Project Structure)

```
CyberHub/
├── README.md                          # User documentation
├── DEVELOPER_GUIDE.md                 # This file
├── database_schema.sql                # Database setup script
└── com/
    ├── lib/                           # External libraries (gitignored)
    │   └── mysql-connector-j-8.0.33.jar
    ├── out/                           # Compiled classes (gitignored)
    ├── setup.sh/bat                   # Initial setup scripts
    ├── compile.sh/bat                 # Compilation scripts
    ├── run.sh/bat                     # Run application scripts
    └── src/
        └── com/yourcompany/cyberhub/
            ├── Main.java              # Application entry point
            ├── model/                 # Data models (POJOs)
            │   ├── User.java          # Abstract user class
            │   ├── Admin.java         # Admin user
            │   ├── Customer.java      # Customer user
            │   ├── Computer.java      # Computer model
            │   ├── Session.java       # Session model (future use)
            │   └── Transaction.java   # Transaction model (future use)
            ├── dao/                   # Data Access Objects
            │   ├── UserDao.java       # User database operations
            │   └── ComputerDao.java   # Computer database operations
            ├── view/                  # GUI (Swing)
            │   ├── LoginFrame.java    # Login window
            │   └── AdminDashboardFrame.java  # Admin dashboard
            └── util/                  # Utility classes
                ├── DatabaseConnector.java    # Database connection
                ├── InputValidator.java       # Input validation
                └── PasswordHasher.java       # Password hashing
```

## Kiến trúc (Architecture)

### Mô hình MVC (Model-View-Controller)
- **Model**: Package `model` - Các class đại diện cho dữ liệu
- **View**: Package `view` - Giao diện người dùng (Swing)
- **Controller/DAO**: Package `dao` - Logic truy cập dữ liệu

### Kết nối Database
- Sử dụng JDBC với MySQL Connector
- Connection pooling: Chưa implement (có thể thêm HikariCP)
- Prepared statements để tránh SQL injection

## Hướng dẫn phát triển (Development Guide)

### 1. Setup môi trường phát triển

**Yêu cầu:**
- JDK 17 hoặc cao hơn
- MySQL Server 5.7+
- IDE: IntelliJ IDEA, Eclipse, hoặc VS Code với Java Extension Pack

**Bước setup:**
```bash
# Clone repository
git clone https://github.com/HongQuang26/CyberHub.git
cd CyberHub

# Setup database
mysql -u root -p < database_schema.sql

# Download dependencies và compile
cd com
./setup.sh    # Linux/Mac
setup.bat     # Windows

# Update database credentials
# Edit: com/src/com/yourcompany/cyberhub/util/DatabaseConnector.java
```

### 2. Compile và chạy

**Command line:**
```bash
cd com
./compile.sh && ./run.sh  # Linux/Mac
compile.bat && run.bat    # Windows
```

**IntelliJ IDEA:**
1. Open `com` folder as project
2. Add `lib/mysql-connector-j-8.0.33.jar` to Project Libraries
3. Run `Main.java`

### 3. Coding Standards

**Package naming:**
- All packages use `com.yourcompany.cyberhub` prefix
- Keep package structure clean and organized

**Class naming:**
- Model classes: Singular nouns (User, Computer)
- DAO classes: ModelNameDao (UserDao, ComputerDao)
- View classes: PurposeFrame (LoginFrame, AdminDashboardFrame)

**Code style:**
- Use camelCase for methods and variables
- Use PascalCase for classes
- Keep methods short and focused (< 50 lines)
- Add comments for complex logic
- Use meaningful variable names

**Error handling:**
- Always use try-catch for database operations
- Show user-friendly error messages
- Log errors to console for debugging

### 4. Database Operations

**Adding new DAO methods:**
```java
public boolean methodName(parameters) {
    String sql = "SQL QUERY HERE";
    try (Connection conn = DatabaseConnector.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        // Set parameters
        pstmt.setString(1, param);
        
        // Execute
        ResultSet rs = pstmt.executeQuery();
        // or
        int rows = pstmt.executeUpdate();
        
        // Process results
        return success;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

**Best practices:**
- Always use PreparedStatement (never concatenate SQL)
- Use try-with-resources for auto-closing connections
- Return boolean for success/failure
- Return objects/lists for queries

### 5. Adding new Views

**Creating a new window:**
```java
public class NewFrame extends JFrame {
    // Components
    private JButton button;
    private JTextField field;
    
    public NewFrame() {
        // Setup frame
        setTitle("Title");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Setup layout and components
        setupUI();
    }
    
    private void setupUI() {
        // Add components
        // Add listeners
    }
}
```

### 6. Input Validation

Always validate user input using `InputValidator`:

```java
if (!InputValidator.isValidUsername(username)) {
    JOptionPane.showMessageDialog(this, 
        InputValidator.getUsernameErrorMessage(), 
        "Lỗi", JOptionPane.ERROR_MESSAGE);
    return;
}
```

Available validators:
- `isValidUsername(String)` - 3-50 chars, alphanumeric + underscore
- `isValidPassword(String)` - Min 6 chars
- `isValidFullName(String)` - 2-100 chars
- `isValidAmount(BigDecimal)` - Positive, max 100M

### 7. Password Hashing (Optional)

To enable password hashing:

**When adding user:**
```java
String hashedPassword = PasswordHasher.hashPassword(plainPassword);
userDao.addCustomer(username, hashedPassword, fullName);
```

**When verifying login:**
```java
String hashedInput = PasswordHasher.hashPassword(password);
// Compare hashedInput with stored hash
if (hashedInput.equals(storedHash)) {
    // Login success
}
```

**Migration script for existing users:**
```sql
-- Backup first!
UPDATE users SET password = SHA2(password, 256) WHERE user_id > 0;
```

## Testing

### Manual Testing Checklist

**Login:**
- [ ] Valid admin login works
- [ ] Invalid credentials show error
- [ ] Empty fields show validation error
- [ ] Invalid username format shows error

**Customer Management:**
- [ ] Add customer with valid data
- [ ] Add customer with invalid data (validation)
- [ ] Top-up balance with valid amount
- [ ] Top-up balance with invalid amount
- [ ] Delete customer (with confirmation)
- [ ] Refresh customer list

**Computer Management:**
- [ ] View computer status
- [ ] Refresh computer list
- [ ] Status colors display correctly

### Unit Testing (Future)
- Consider adding JUnit tests for:
  - InputValidator methods
  - PasswordHasher methods
  - DAO methods (with H2 in-memory database)

## Common Issues & Solutions

### Issue: "MySQL JDBC Driver not found"
**Solution:** Run setup script to download the driver
```bash
cd com && ./setup.sh
```

### Issue: "Access denied for user"
**Solution:** Update database credentials in `DatabaseConnector.java`

### Issue: Compilation errors
**Solution:** Make sure you're using JDK 17+ and MySQL connector is in lib/

### Issue: UI doesn't update
**Solution:** Call `revalidate()` and `repaint()` after updating components

## Future Enhancements

### High Priority
1. **Session Management**: Track customer computer usage
2. **Transaction History**: Display and search transactions
3. **Customer Dashboard**: Login interface for customers
4. **Password Hashing**: Enable by default (currently optional)

### Medium Priority
5. **Reports**: Daily/monthly revenue reports
6. **Pricing System**: Configurable hourly rates
7. **Auto Logout**: Automatic session timeout
8. **Backup System**: Database backup functionality

### Low Priority
9. **Multi-language**: Support English and Vietnamese
10. **Themes**: Light/dark mode
11. **Email Notifications**: Low balance alerts
12. **API**: REST API for mobile app integration

## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature-name`
3. Make changes and test thoroughly
4. Commit: `git commit -m "Add feature-name"`
5. Push: `git push origin feature-name`
6. Create Pull Request

## Resources

- [Java Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Java Security Best Practices](https://www.oracle.com/java/technologies/javase/seccodeguide.html)

## License
MIT License - See LICENSE file for details

## Contact
For questions or issues, please open an issue on GitHub or contact the maintainer.
