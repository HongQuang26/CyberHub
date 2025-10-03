# CyberHub
Simple tool for managing users and PCs in an internet cafe.

## Tính năng (Features)
- 🔐 Đăng nhập cho Admin và Khách hàng
- 👥 Quản lý khách hàng (thêm, xem, nạp tiền)
- 💻 Quản lý máy tính (xem trạng thái theo thời gian thực)
- 💰 Quản lý số dư tài khoản
- 📊 Theo dõi giao dịch
- ✅ Validation đầu vào
- 🔒 Hỗ trợ mã hóa mật khẩu (SHA-256)
- 🎨 Giao diện màu sắc trực quan

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

**Cách 1: Sử dụng scripts (Khuyến nghị)**

Linux/Mac:
```bash
cd com
./compile.sh    # Biên dịch
./run.sh        # Chạy ứng dụng
```

Windows:
```bash
cd com
compile.bat    # Biên dịch
run.bat        # Chạy ứng dụng
```

**Cách 2: Sử dụng command line thủ công**

Linux/Mac:
```bash
cd com
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d out $(find src -name "*.java")
java -cp "out:lib/mysql-connector-j-8.0.33.jar" com.yourcompany.cyberhub.Main
```

Windows:
```bash
cd com
javac -cp "lib\mysql-connector-j-8.0.33.jar" -d out src\com\yourcompany\cyberhub\**\*.java
java -cp "out;lib\mysql-connector-j-8.0.33.jar" com.yourcompany.cyberhub.Main
```

**Cách 3: Sử dụng IntelliJ IDEA**
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

## Cải tiến mới (Recent Improvements)

### Phiên bản hiện tại
- ✅ **Input Validation**: Thêm validation cho tất cả các trường nhập liệu
  - Username: 3-50 ký tự, chỉ chứa chữ cái, số và dấu gạch dưới
  - Password: Tối thiểu 6 ký tự
  - Họ tên: 2-100 ký tự
  - Số tiền: Phải dương và không vượt quá 100 triệu
  
- ✅ **Password Hashing**: Thêm tiện ích mã hóa mật khẩu SHA-256
  - Class `PasswordHasher` trong package `util`
  - Sẵn sàng để tích hợp vào quá trình đăng nhập
  
- ✅ **Improved UI**: Giao diện cải thiện
  - Nút làm mới cho cả tab máy tính và khách hàng
  - Hiển thị trạng thái máy bằng tiếng Việt
  - Màu sắc rõ ràng cho trạng thái máy tính
  - Thông báo thành công/lỗi rõ ràng hơn
  
- ✅ **Better Error Handling**: Xử lý lỗi tốt hơn
  - Try-catch cho các thao tác database
  - Thông báo lỗi chi tiết cho người dùng
  - Validation messages rõ ràng

## Lưu ý bảo mật (Security Notes)
⚠️ **Cảnh báo**: Phiên bản hiện tại lưu mật khẩu dạng plain text trong database. 

**Để kích hoạt mã hóa mật khẩu**:
1. Sử dụng class `PasswordHasher.hashPassword()` khi thêm user mới
2. Sử dụng `PasswordHasher.verifyPassword()` khi đăng nhập
3. Chạy script migration để hash tất cả mật khẩu hiện có

**Các cải tiến bảo mật khác nên có**:
- ✅ Prepared statements (đã có) để tránh SQL injection
- ✅ Input validation (đã có)
- ⚠️ Cần thêm session management và timeout
- ⚠️ Cần thêm HTTPS trong production
- ⚠️ Cần thêm audit logging

## Đóng góp (Contributing)
Mọi đóng góp đều được hoan nghênh! Hãy tạo issue hoặc pull request.

## License
MIT License
