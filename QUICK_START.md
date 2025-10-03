# CyberHub - Quick Start Guide

## Hướng dẫn nhanh (5 phút)

### Bước 1: Cài đặt MySQL và tạo database

```bash
# Chạy MySQL shell
mysql -u root -p

# Hoặc import trực tiếp file SQL
mysql -u root -p < database_schema.sql
```

Thông tin tài khoản mặc định:
- **Admin**: username: `admin`, password: `admin123`
- **Customer 1**: username: `customer1`, password: `pass123`
- **Customer 2**: username: `customer2`, password: `pass123`
- **Customer 3**: username: `customer3`, password: `pass123`

### Bước 2: Cấu hình kết nối database

Mở file `com/src/com/yourcompany/cyberhub/util/DatabaseConnector.java`

Thay đổi dòng này:
```java
private static final String MYSQL_PASSWORD = "root"; // Thay bằng mật khẩu MySQL của bạn
```

### Bước 3: Download MySQL JDBC driver

**Linux/Mac:**
```bash
cd com
./setup.sh
```

**Windows:**
```bash
cd com
setup.bat
```

### Bước 4: Biên dịch và chạy

**Linux/Mac:**
```bash
cd com
./compile.sh
./run.sh
```

**Windows:**
```bash
cd com
compile.bat
run.bat
```

### Bước 5: Đăng nhập và sử dụng

1. Chạy ứng dụng sẽ mở cửa sổ đăng nhập
2. Nhập username: `admin`, password: `admin123`
3. Nhấn "Đăng nhập"
4. Bạn sẽ thấy dashboard với 4 tab:
   - **Quản lý Máy tính**: Xem trạng thái 10 máy mẫu
   - **Quản lý Khách hàng**: Thêm, xóa, nạp tiền cho khách hàng
   - **Quản lý Thực đơn**: Quản lý menu đồ ăn
   - **Lịch sử & Thống kê**: Đang phát triển

### Đăng nhập Khách hàng (MỚI)
1. Đăng nhập với `customer1/pass123`
2. Bạn sẽ thấy dashboard khách hàng với 3 tab:
   - **Chọn máy tính**: Click vào máy có sẵn (màu xanh) để sử dụng
   - **Đặt đồ ăn**: Chọn món, nhập số lượng, hệ thống tự động trừ tiền
   - **Tài khoản**: Xem thông tin và số dư

## Các tính năng chính

### Tab Quản lý Máy tính
- Xem trạng thái máy theo màu sắc:
  - 🟢 Xanh lá = Sẵn sàng (AVAILABLE)
  - 🔴 Đỏ = Đang sử dụng (IN_USE)
  - ⚫ Xám = Bảo trì (MAINTENANCE)
- Nút "Làm mới" để cập nhật trạng thái

### Tab Quản lý Khách hàng
- **Thêm khách hàng**: Nhập username, password, họ tên
  - Username: 3-50 ký tự, chỉ chữ cái, số, gạch dưới
  - Password: Tối thiểu 6 ký tự
  - Họ tên: 2-100 ký tự
- **Nạp tiền**: Chọn khách hàng, nhập số tiền (dương, tối đa 100 triệu)
  - Số tiền tự động được định dạng với dấu chấm (ví dụ: 50.000)
- **Xóa khách hàng**: Chọn khách hàng và xóa (có xác nhận)
- **Làm mới**: Cập nhật danh sách khách hàng
- Số dư hiển thị với định dạng có dấu chấm phân cách

### Tab Quản lý Thực đơn (MỚI)
- **Thêm món**: Nhập tên món, giá, danh mục
  - Giá tự động được định dạng khi nhập
- **Sửa món**: Chọn món để sửa thông tin hoặc đánh dấu có sẵn/không
- **Xóa món**: Xóa món khỏi thực đơn (có xác nhận)
- **Làm mới**: Cập nhật danh sách món ăn

## Khắc phục sự cố

### Lỗi: "MySQL JDBC Driver not found"
```bash
cd com
./setup.sh    # hoặc setup.bat trên Windows
```

### Lỗi: "Access denied for user 'root'"
- Kiểm tra mật khẩu MySQL trong `DatabaseConnector.java`
- Hoặc tạo user mới trong MySQL:
```sql
CREATE USER 'cyberhub'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON net_cafe_management.* TO 'cyberhub'@'localhost';
FLUSH PRIVILEGES;
```

### Lỗi: "Table doesn't exist"
- Chạy lại file SQL: `mysql -u root -p < database_schema.sql`

### Ứng dụng không khởi động
- Kiểm tra Java version: `java -version` (cần JDK 17+)
- Kiểm tra MySQL đang chạy: `mysql -u root -p`
- Xem log lỗi trong console

## Thử nghiệm nhanh

### Thử nghiệm với Admin
1. Đăng nhập với `admin/admin123`
2. Vào tab "Quản lý Khách hàng"
3. Thêm khách hàng mới:
   - Username: `testuser`
   - Password: `test123`
   - Họ tên: `Nguyen Test`
4. Chọn khách hàng vừa tạo
5. Nạp tiền: Nhập `50000` - sẽ tự động hiển thị là `50.000`
6. Xem số dư đã cập nhật với định dạng dấu chấm
7. Vào tab "Quản lý Thực đơn"
8. Thêm món mới hoặc sửa món có sẵn
9. Thử xóa khách hàng hoặc món ăn (sẽ có xác nhận)

### Thử nghiệm với Khách hàng (MỚI)
1. Đăng nhập với `customer1/pass123`
2. Vào tab "Chọn máy tính"
3. Click vào máy có màu xanh (Sẵn sàng) để sử dụng
4. Vào tab "Đặt đồ ăn"
5. Chọn món ăn và nhập số lượng
6. Xem số dư tự động được trừ sau khi đặt món
7. Vào tab "Tài khoản" để xem thông tin và số dư

## Tài liệu chi tiết

- **README.md**: Hướng dẫn đầy đủ
- **DEVELOPER_GUIDE.md**: Hướng dẫn phát triển
- **database_schema.sql**: Cấu trúc database

## Liên hệ & Hỗ trợ

Nếu gặp vấn đề:
1. Xem phần "Khắc phục sự cố" ở trên
2. Đọc README.md và DEVELOPER_GUIDE.md
3. Tạo issue trên GitHub
4. Liên hệ maintainer

---

**Chúc bạn sử dụng thành công! 🎉**
