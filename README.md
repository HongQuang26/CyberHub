# CyberHub
Simple tool for managing users and PCs in an internet cafe.

## Tính năng (Features)
- 🔐 Đăng nhập cho Admin và Khách hàng
- 👥 Quản lý khách hàng (thêm, xem, nạp tiền)
- 💻 Quản lý máy tính (xem trạng thái)
- 💰 Quản lý số dư tài khoản
- 📊 Theo dõi giao dịch

## Yêu cầu hệ thống (Requirements)
- Java Development Kit (JDK) 17 trở lên
- MySQL Server 5.7 trở lên
- MySQL Connector/J 8.0.33 (đã bao gồm trong thư mục `com/lib/`)

## Cài đặt (Installation)

### 1. Cài đặt cơ sở dữ liệu
1. Cài đặt MySQL Server nếu chưa có
2. Chạy script SQL để tạo database và tables:
   ```bash
   mysql -u root -p < database_schema.sql
   ```
   Hoặc mở MySQL Workbench và chạy file `database_schema.sql`

3. Script sẽ tự động tạo:
   - Database: `net_cafe_management`
   - Tài khoản admin mặc định:
     - Username: `admin`
     - Password: `admin123`
   - 3 khách hàng mẫu (customer1, customer2, customer3 - password: pass123)
   - 10 máy tính mẫu

### 2. Cấu hình kết nối database
Mở file `com/src/com/yourcompany/cyberhub/util/DatabaseConnector.java` và cập nhật thông tin kết nối:
```java
private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/net_cafe_management";
private static final String MYSQL_USER = "root";
private static final String MYSQL_PASSWORD = "your_password"; // Thay đổi password của bạn
```

### 3. Biên dịch và chạy ứng dụng

**Cách 1: Sử dụng command line**
```bash
cd com

# Biên dịch
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d out $(find src -name "*.java")

# Chạy
java -cp "out:lib/mysql-connector-j-8.0.33.jar" com.yourcompany.cyberhub.Main
```

**Trên Windows:**
```bash
cd com

# Biên dịch
javac -cp "lib\mysql-connector-j-8.0.33.jar" -d out src\com\yourcompany\cyberhub\**\*.java

# Chạy
java -cp "out;lib\mysql-connector-j-8.0.33.jar" com.yourcompany.cyberhub.Main
```

**Cách 2: Sử dụng IntelliJ IDEA**
1. Mở thư mục `com` như một project
2. Thêm `lib/mysql-connector-j-8.0.33.jar` vào Project Libraries
   - File → Project Structure → Libraries → + → Java → Chọn file jar
3. Chạy `Main.java`

## Sử dụng (Usage)

### Đăng nhập Admin
- Username: `admin`
- Password: `admin123`

Sau khi đăng nhập, bạn có thể:
- **Tab "Quản lý Máy tính"**: Xem trạng thái các máy tính (Sẵn sàng/Đang sử dụng/Bảo trì)
- **Tab "Quản lý Khách hàng"**: 
  - Xem danh sách khách hàng
  - Thêm khách hàng mới
  - Nạp tiền cho khách hàng
  - Làm mới danh sách
- **Tab "Lịch sử & Thống kê"**: Đang phát triển

## Cấu trúc dự án (Project Structure)
```
CyberHub/
├── database_schema.sql          # Database setup script
├── README.md                     # This file
└── com/
    ├── lib/
    │   └── mysql-connector-j-8.0.33.jar
    └── src/
        └── com/yourcompany/cyberhub/
            ├── Main.java                    # Entry point
            ├── model/                       # Data models
            │   ├── User.java
            │   ├── Admin.java
            │   ├── Customer.java
            │   ├── Computer.java
            │   ├── Session.java
            │   └── Transaction.java
            ├── dao/                         # Data access layer
            │   ├── UserDao.java
            │   └── ComputerDao.java
            ├── view/                        # GUI (Swing)
            │   ├── LoginFrame.java
            │   └── AdminDashboardFrame.java
            └── util/                        # Utilities
                └── DatabaseConnector.java
```

## Lưu ý bảo mật (Security Notes)
⚠️ **Cảnh báo**: Phiên bản hiện tại lưu mật khẩu dạng plain text trong database. Trong môi trường production, nên:
- Sử dụng BCrypt hoặc SHA-256 để hash mật khẩu
- Thêm validation đầu vào
- Sử dụng prepared statements (đã có) để tránh SQL injection
- Thêm session management và timeout

## Đóng góp (Contributing)
Mọi đóng góp đều được hoan nghênh! Hãy tạo issue hoặc pull request.

## License
MIT License
