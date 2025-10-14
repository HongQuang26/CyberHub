-- =================================================================================
-- SCRIPT TẠO CƠ SỞ DỮ LIỆU CHO ỨNG DỤNG QUẢN LÝ QUÁN NET (CYBERHUB)
-- Tác giả: Gemini
-- Ngày tạo: 14/10/2025
-- =================================================================================

-- Bước 1: Tạo cơ sở dữ liệu nếu nó chưa tồn tại.
CREATE DATABASE IF NOT EXISTS cyberhub_management;

-- Bước 2: Chọn cơ sở dữ liệu vừa tạo để thực hiện các lệnh tiếp theo.
USE cyberhub_management;

-- -----------------------------------------------------
-- Bảng: users - Lưu trữ thông tin người dùng (admin và khách hàng)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    balance DECIMAL(10, 2) DEFAULT 0.00,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL
) ENGINE=InnoDB COMMENT='Lưu thông tin tài khoản và số dư của người dùng.';

-- -----------------------------------------------------
-- Bảng: computers - Lưu trữ danh sách và trạng thái các máy tính
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS computers (
    computer_id INT AUTO_INCREMENT PRIMARY KEY,
    computer_name VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('AVAILABLE', 'IN_USE', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE'
) ENGINE=InnoDB COMMENT='Quản lý trạng thái của tất cả máy tính trong quán.';

-- -----------------------------------------------------
-- Bảng: sessions - Ghi lại lịch sử các phiên chơi của khách hàng
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    computer_id INT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    total_cost DECIMAL(10, 2),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (computer_id) REFERENCES computers(computer_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Lịch sử các lần mở máy, bao gồm thời gian và chi phí.';

-- -----------------------------------------------------
-- Bảng: transactions - Ghi lại lịch sử giao dịch (nạp tiền, thanh toán)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_type ENUM('TOP_UP', 'PAYMENT') NOT NULL COMMENT 'TOP_UP: Nạp tiền, PAYMENT: Thanh toán (giờ chơi, đồ ăn)',
    transaction_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='Ghi lại tất cả các dòng tiền ra vào của khách hàng.';

-- -----------------------------------------------------
-- Bảng: menu_items - Danh sách các món ăn, đồ uống
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category ENUM('Đồ uống', 'Đồ ăn vặt', 'Món chính') NOT NULL,
    is_available BOOLEAN DEFAULT TRUE COMMENT 'TRUE = Đang bán, FALSE = Tạm ẩn'
) ENGINE=InnoDB COMMENT='Menu các dịch vụ, đồ ăn, thức uống của quán.';

-- -----------------------------------------------------
-- Bảng: orders - Lưu thông tin các đơn hàng dịch vụ
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id INT,
    order_time DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    is_paid BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='Mỗi đơn hàng dịch vụ của khách hàng.';

-- -----------------------------------------------------
-- Bảng: order_details - Chi tiết các món trong một đơn hàng
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    item_id INT,
    quantity INT NOT NULL,
    price_per_item DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='Chi tiết các món ăn/đồ uống trong một đơn hàng.';

-- =================================================================================
-- THÊM DỮ LIỆU MẪU BAN ĐẦU
-- =================================================================================

-- 1. Thêm tài khoản Admin mặc định
-- Mật khẩu mặc định là 'admin'. Trong thực tế nên được mã hóa.
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', 'admin', 'Quản trị viên', 'ADMIN')
ON DUPLICATE KEY UPDATE password = 'admin'; -- Nếu tài khoản admin đã tồn tại, chỉ cập nhật lại mật khẩu.

-- 2. Thêm một vài khách hàng mẫu
INSERT INTO users (username, password, full_name, balance, role) VALUES
('khach1', '123', 'Nguyễn Văn A', 50000.00, 'CUSTOMER'),
('khach2', '123', 'Trần Thị B', 25000.00, 'CUSTOMER')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name); -- Tránh lỗi nếu chạy lại script.

-- 3. Thêm một vài máy tính mẫu
-- INSERT IGNORE sẽ bỏ qua việc thêm nếu tên máy đã tồn tại, tránh gây lỗi.
INSERT IGNORE INTO computers (computer_name) VALUES
('MAY 01'), ('MAY 02'), ('MAY 03'), ('MAY 04'), ('MAY 05'),
('MAY 06'), ('MAY 07'), ('MAY 08'), ('MAY 09'), ('MAY 10');

-- 4. Thêm một vài món ăn mẫu vào menu
INSERT IGNORE INTO menu_items (name, price, category) VALUES
('Sting Dâu', 15000, 'Đồ uống'),
('Coca-Cola', 15000, 'Đồ uống'),
('Bim bim Oishi', 10000, 'Đồ ăn vặt'),
('Mì Hảo Hảo trứng', 20000, 'Món chính'),
('Nước suối', 8000, 'Đồ uống');

-- =================================================================================
-- KẾT THÚC SCRIPT
-- =================================================================================