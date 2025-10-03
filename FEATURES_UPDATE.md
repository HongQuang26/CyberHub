# Cập nhật Tính năng Mới - CyberHub

## Tổng quan
Bản cập nhật này bao gồm ba tính năng chính được yêu cầu:

1. ✅ **Sửa lỗi không thể mở máy cho khách**
2. ✅ **Thêm chức năng đặt món ăn cho khách, quản lý menu**
3. ✅ **Định dạng số tiền với dấu chấm phân cách**

---

## 1. Khách hàng có thể đăng nhập và sử dụng máy tính

### Trước đây
- Khách hàng không thể đăng nhập vào hệ thống
- Chỉ Admin mới có giao diện

### Hiện tại
- ✅ Khách hàng có thể đăng nhập với tài khoản của mình
- ✅ Giao diện riêng cho khách hàng (`CustomerDashboardFrame`)
- ✅ Khách hàng có thể xem và chọn máy tính có sẵn
- ✅ Máy được chọn tự động chuyển sang trạng thái "Đang dùng"

### Cách sử dụng
```
1. Đăng nhập với tài khoản khách hàng (ví dụ: customer1/pass123)
2. Vào tab "Chọn máy tính"
3. Click vào máy tính có màu xanh (Sẵn sàng)
4. Xác nhận để bắt đầu sử dụng
```

---

## 2. Hệ thống đặt món ăn

### Tính năng cho Admin
#### Quản lý Menu (Tab "Quản lý Thực đơn")
- ✅ **Thêm món mới**: Nhập tên món, giá, danh mục
- ✅ **Sửa món**: Cập nhật thông tin món, bật/tắt trạng thái có sẵn
- ✅ **Xóa món**: Xóa món khỏi menu (có xác nhận)
- ✅ **Xem danh sách**: Hiển thị tất cả món ăn kể cả không có sẵn

#### Dữ liệu mẫu
Menu mẫu đã được thêm vào database:
- Đồ ăn nhanh: Mì tôm (10.000đ), Xúc xích (15.000đ), Bánh mì (20.000đ)
- Cơm: Cơm rang (35.000đ)
- Nước giải khát: Coca (15.000đ), Pepsi (15.000đ), Nước suối (10.000đ), Trà đào (20.000đ)
- Đồ ăn vặt: Snack (10.000đ), Kẹo (5.000đ)

### Tính năng cho Khách hàng
#### Đặt món (Tab "Đặt đồ ăn")
- ✅ **Xem menu**: Hiển thị tất cả món có sẵn với giá được định dạng
- ✅ **Chọn món**: Click vào món trong bảng
- ✅ **Nhập số lượng**: Hệ thống tự động tính tổng tiền
- ✅ **Kiểm tra số dư**: Tự động kiểm tra trước khi đặt
- ✅ **Trừ tiền tự động**: Số dư được cập nhật ngay sau khi đặt món

#### Cách sử dụng
```
1. Vào tab "Đặt đồ ăn"
2. Click chọn món từ bảng
3. Nhấn nút "Đặt món"
4. Nhập số lượng
5. Xác nhận đặt món
6. Hệ thống tự động trừ tiền và ghi nhận đơn hàng
```

### Cơ sở dữ liệu
Đã thêm 2 bảng mới:
- **food_menu**: Lưu trữ thông tin món ăn
- **food_orders**: Theo dõi đơn hàng của khách

---

## 3. Định dạng số tiền với dấu chấm

### Mô tả
Tất cả số tiền trong hệ thống giờ đây được hiển thị với dấu chấm phân cách hàng nghìn theo chuẩn Việt Nam.

### Ví dụ
- `50000` → `50.000`
- `1000000` → `1.000.000`
- `15000` → `15.000`

### Áp dụng cho
- ✅ **Hiển thị số dư khách hàng** trong bảng quản lý
- ✅ **Nhập tiền nạp** cho khách hàng (tự động format khi gõ)
- ✅ **Hiển thị giá món ăn** trong menu
- ✅ **Nhập giá món ăn** khi thêm/sửa (tự động format khi gõ)
- ✅ **Thông báo xác nhận** về số tiền

### Công nghệ
- Sử dụng `MoneyFormatter` utility class
- `DocumentFilter` tự động format khi người dùng nhập liệu
- Parse ngược lại khi cần xử lý số

### Cách sử dụng
Khi nhập số tiền vào các trường:
1. Chỉ cần gõ số (ví dụ: `50000`)
2. Hệ thống tự động thêm dấu chấm (`50.000`)
3. Có thể tiếp tục nhập hoặc xóa, format tự động cập nhật

---

## File thay đổi

### Model Classes (Mới)
- `FoodItem.java` - Đại diện cho món ăn
- `FoodOrder.java` - Đại diện cho đơn hàng

### DAO Classes (Mới)
- `FoodDao.java` - Xử lý database cho món ăn và đơn hàng

### Utility Classes (Mới)
- `MoneyFormatter.java` - Format và parse số tiền

### View Classes
- `CustomerDashboardFrame.java` (Mới) - Giao diện cho khách hàng
- `LoginFrame.java` (Đã cập nhật) - Cho phép khách hàng đăng nhập
- `AdminDashboardFrame.java` (Đã cập nhật) - Thêm tab quản lý thực đơn và format tiền

### Database
- `database_schema.sql` (Đã cập nhật) - Thêm bảng food_menu và food_orders

---

## Hướng dẫn nâng cấp Database

Nếu bạn đã có database từ trước, chạy các lệnh SQL sau:

```sql
USE net_cafe_management;

-- Tạo bảng food_menu
CREATE TABLE IF NOT EXISTS food_menu (
    food_id INT PRIMARY KEY AUTO_INCREMENT,
    food_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng food_orders
CREATE TABLE IF NOT EXISTS food_orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    food_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (food_id) REFERENCES food_menu(food_id)
);

-- Thêm dữ liệu mẫu
INSERT INTO food_menu (food_name, price, category, available) VALUES 
    ('Mì tôm', 10000.00, 'Đồ ăn nhanh', TRUE),
    ('Xúc xích', 15000.00, 'Đồ ăn nhanh', TRUE),
    ('Bánh mì', 20000.00, 'Đồ ăn nhanh', TRUE),
    ('Cơm rang', 35000.00, 'Cơm', TRUE),
    ('Coca Cola', 15000.00, 'Nước giải khát', TRUE),
    ('Pepsi', 15000.00, 'Nước giải khát', TRUE),
    ('Nước suối', 10000.00, 'Nước giải khát', TRUE),
    ('Trà đào', 20000.00, 'Nước giải khát', TRUE),
    ('Snack', 10000.00, 'Đồ ăn vặt', TRUE),
    ('Kẹo', 5000.00, 'Đồ ăn vặt', TRUE);
```

---

## Kiểm thử

### Test Case 1: Khách hàng sử dụng máy
1. ✅ Đăng nhập với customer1/pass123
2. ✅ Vào tab "Chọn máy tính"
3. ✅ Click vào máy PC-01 (hoặc bất kỳ máy xanh nào)
4. ✅ Xác nhận → Máy chuyển sang màu đỏ (Đang dùng)

### Test Case 2: Đặt món ăn
1. ✅ Đăng nhập với customer1/pass123 (số dư: 50.000đ)
2. ✅ Vào tab "Đặt đồ ăn"
3. ✅ Chọn "Mì tôm" (10.000đ)
4. ✅ Nhập số lượng: 2
5. ✅ Xác nhận → Tổng: 20.000đ, số dư còn: 30.000đ

### Test Case 3: Quản lý menu (Admin)
1. ✅ Đăng nhập với admin/admin123
2. ✅ Vào tab "Quản lý Thực đơn"
3. ✅ Thêm món mới: "Phở" - giá 40000 - danh mục "Cơm"
4. ✅ Sửa món: Đổi giá "Coca Cola" thành 18000
5. ✅ Xóa món không cần thiết

### Test Case 4: Định dạng tiền
1. ✅ Admin nạp tiền cho khách hàng
2. ✅ Nhập: `100000` → Hiển thị: `100.000`
3. ✅ Thêm món mới với giá `25000` → Hiển thị: `25.000`
4. ✅ Số dư trong bảng hiển thị có dấu chấm

---

## Lưu ý kỹ thuật

1. **Tương thích ngược**: Code mới tương thích với database cũ (chỉ cần chạy script SQL để thêm bảng mới)

2. **Validation**: Tất cả input đều được validate trước khi lưu database

3. **Số tiền**: Sử dụng BigDecimal để tránh lỗi làm tròn

4. **Thread-safe**: DocumentFilter hoạt động an toàn với Swing EDT

5. **Database**: Foreign key constraints đảm bảo tính toàn vẹn dữ liệu

---

## Tương lai

Các tính năng có thể mở rộng:
- [ ] Thống kê doanh thu từ đồ ăn
- [ ] Lịch sử đơn hàng cho khách
- [ ] Tính thời gian sử dụng máy
- [ ] In hóa đơn
- [ ] Thông báo khi đồ ăn sẵn sàng
