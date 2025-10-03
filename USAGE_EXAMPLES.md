# Ví dụ Sử dụng - CyberHub

## Kịch bản 1: Khách hàng sử dụng dịch vụ quán net

### Bước 1: Đăng nhập
```
Tên đăng nhập: customer1
Mật khẩu: pass123
```
→ Nhấn "Đăng nhập"
→ Hiển thị thông báo: "Đăng nhập thành công! Chào mừng Nguyen Van A"
→ Mở giao diện khách hàng với 3 tab

### Bước 2: Chọn máy tính
1. Click vào tab "Chọn máy tính"
2. Xem các máy có sẵn (màu xanh lá):
   - PC-01: Sẵn sàng (màu xanh)
   - PC-02: Sẵn sàng (màu xanh)
   - PC-03: Đang dùng (màu đỏ) - không thể chọn
   - PC-04: Sẵn sàng (màu xanh)
   - PC-05: Bảo trì (màu xám) - không thể chọn
3. Click vào "PC-01"
4. Xác nhận: "Bạn muốn sử dụng PC-01?"
5. Nhấn "Yes"
6. Thông báo: "Đã chọn PC-01 thành công! Chúc bạn có trải nghiệm tốt!"
7. PC-01 chuyển sang màu đỏ (Đang dùng)

### Bước 3: Đặt đồ ăn
1. Click vào tab "Đặt đồ ăn"
2. Xem menu có sẵn:
   ```
   ID | Tên món      | Giá (VND) | Danh mục
   1  | Mì tôm       | 10.000    | Đồ ăn nhanh
   2  | Xúc xích     | 15.000    | Đồ ăn nhanh
   3  | Bánh mì      | 20.000    | Đồ ăn nhanh
   4  | Cơm rang     | 35.000    | Cơm
   5  | Coca Cola    | 15.000    | Nước giải khát
   ...
   ```
3. Click chọn "Mì tôm" (10.000 VND)
4. Nhấn nút "Đặt món"
5. Nhập số lượng: `2`
6. Xác nhận:
   ```
   Xác nhận đặt món:
   Món: Mì tôm
   Số lượng: 2
   Tổng tiền: 20.000 VND
   ```
7. Nhấn "Yes"
8. Thông báo: "Đặt món thành công! Món ăn sẽ được giao đến bạn sớm."
9. Số dư tự động giảm từ 50.000 → 30.000 VND

### Bước 4: Kiểm tra tài khoản
1. Click vào tab "Tài khoản"
2. Xem thông tin:
   ```
   Tên đăng nhập: customer1
   Số dư: 30.000 VND
   ```

---

## Kịch bản 2: Admin quản lý thực đơn

### Bước 1: Đăng nhập Admin
```
Tên đăng nhập: admin
Mật khẩu: admin123
```
→ Mở giao diện Admin với 4 tab

### Bước 2: Thêm món mới vào menu
1. Click vào tab "Quản lý Thực đơn"
2. Nhấn nút "Thêm món"
3. Nhập thông tin:
   ```
   Tên món: Phở bò
   Giá (VND): 45000  → tự động hiển thị: 45.000
   Danh mục: Cơm
   ```
4. Nhấn "OK"
5. Thông báo: "Thêm món thành công!"
6. Món mới xuất hiện trong bảng:
   ```
   ID | Tên món  | Giá (VND) | Danh mục | Có sẵn
   11 | Phở bò   | 45.000    | Cơm      | Có
   ```

### Bước 3: Sửa thông tin món
1. Chọn "Coca Cola" trong bảng
2. Nhấn nút "Sửa món"
3. Thay đổi giá từ 15.000 → 18.000
4. Bỏ tick "Có sẵn" (tạm hết hàng)
5. Nhấn "OK"
6. Thông báo: "Sửa món thành công!"
7. Thông tin cập nhật trong bảng:
   ```
   5 | Coca Cola | 18.000 | Nước giải khát | Không
   ```

### Bước 4: Xóa món không cần thiết
1. Chọn món "Kẹo" trong bảng
2. Nhấn nút "Xóa món"
3. Xác nhận: "Bạn có chắc chắn muốn xóa món: Kẹo"
4. Nhấn "Yes"
5. Thông báo: "Xóa món thành công!"
6. Món biến mất khỏi bảng

---

## Kịch bản 3: Admin nạp tiền cho khách hàng (với định dạng tiền)

### Bước 1: Chọn khách hàng
1. Click vào tab "Quản lý Khách hàng"
2. Xem danh sách khách hàng với số dư đã được format:
   ```
   ID | Tên đăng nhập | Họ và Tên    | Số dư (VND)
   2  | customer1     | Nguyen Van A | 30.000
   3  | customer2     | Tran Thi B   | 100.000
   4  | customer3     | Le Van C     | 75.000
   ```
3. Click chọn "customer1" (số dư hiện tại: 30.000)

### Bước 2: Nạp tiền với định dạng tự động
1. Nhấn nút "Nạp tiền"
2. Dialog hiện lên với trường nhập có ghi chú: "(Số tiền sẽ tự động được định dạng)"
3. Bắt đầu nhập: `1`
   - Hiển thị: `1`
4. Tiếp tục nhập: `0` → `10`
   - Hiển thị: `10`
5. Tiếp tục nhập: `0` → `100`
   - Hiển thị: `100`
6. Tiếp tục nhập: `0` → `1000`
   - Hiển thị tự động: `1.000`
7. Tiếp tục nhập: `0` → `10000`
   - Hiển thị tự động: `10.000`
8. Tiếp tục nhập: `0` → `100000`
   - Hiển thị tự động: `100.000`
9. Nhấn "OK"
10. Thông báo: "Nạp tiền thành công! Số tiền: 100.000 VND"
11. Bảng cập nhật:
    ```
    2 | customer1 | Nguyen Van A | 130.000
    ```

---

## Kịch bản 4: Khách hàng không đủ tiền đặt món

### Tình huống
- Khách hàng: customer1
- Số dư hiện tại: 30.000 VND
- Muốn đặt: Cơm rang (35.000 VND)

### Các bước
1. Đăng nhập với customer1/pass123
2. Vào tab "Đặt đồ ăn"
3. Chọn "Cơm rang" (35.000 VND)
4. Nhấn "Đặt món"
5. Nhập số lượng: `1`
6. Nhấn "Yes" để xác nhận
7. Hệ thống hiển thị lỗi:
   ```
   Số dư không đủ!
   Tổng tiền: 35.000 VND
   Số dư hiện tại: 30.000 VND
   ```
8. Khách hàng cần liên hệ Admin để nạp thêm tiền

---

## Kịch bản 5: Thêm khách hàng mới với validation

### Trường hợp thành công
1. Admin đăng nhập
2. Tab "Quản lý Khách hàng" → Nhấn "Thêm khách hàng"
3. Nhập:
   ```
   Tên đăng nhập: newcustomer
   Mật khẩu: pass1234
   Họ và Tên: Pham Van D
   ```
4. Nhấn "OK"
5. Thông báo: "Thêm khách hàng thành công!"
6. Khách hàng mới xuất hiện với số dư 0

### Trường hợp lỗi - Username không hợp lệ
1. Nhập username: `ab` (quá ngắn, cần 3-50 ký tự)
2. Lỗi: "Tên đăng nhập phải có 3-50 ký tự và chỉ chứa chữ cái, số và dấu gạch dưới."

### Trường hợp lỗi - Username đã tồn tại
1. Nhập username: `customer1` (đã có)
2. Thông báo: "Thêm khách hàng thất bại (Tên đăng nhập có thể đã tồn tại)."

---

## Lưu ý quan trọng

### Định dạng tiền tự động
- **Chỉ nhập số**: Không cần nhập dấu chấm, hệ thống tự động thêm
- **Ví dụ**: Nhập `50000` → Tự động hiển thị `50.000`
- **Xóa cũng tự động**: Xóa ký tự nào, format cũng tự động cập nhật

### Màu sắc máy tính
- **Xanh lá (Green)**: Máy sẵn sàng, có thể chọn
- **Đỏ/Hồng (Red/Pink)**: Máy đang được sử dụng, không chọn được
- **Xám (Gray)**: Máy đang bảo trì, không sử dụng được

### Trạng thái món ăn
- **Có sẵn**: Hiển thị trong menu của khách hàng
- **Không có sẵn**: Chỉ Admin thấy, khách không thấy trong menu đặt hàng

### Validation
- **Username**: 3-50 ký tự, chỉ chữ cái, số, gạch dưới
- **Password**: Tối thiểu 6 ký tự
- **Họ tên**: 2-100 ký tự
- **Số tiền**: Dương, tối đa 100 triệu

---

## Tips & Tricks

### Cho Admin
1. **Nạp tiền nhanh**: Để nạp 100k, chỉ cần gõ `100000` (6 số 0)
2. **Tắt món tạm thời**: Dùng "Sửa món" và bỏ tick "Có sẵn" thay vì xóa
3. **Danh mục**: Đặt tên danh mục giống nhau để nhóm món lại

### Cho Khách hàng
1. **Kiểm tra số dư trước**: Vào tab "Tài khoản" xem số dư trước khi đặt món
2. **Đặt nhiều món**: Có thể chọn và đặt nhiều lần
3. **Chọn máy yêu thích**: Các máy có số thứ tự, có thể chọn máy mình thích nếu còn trống

### Khắc phục lỗi
- **Không thấy món mới**: Nhấn nút "Làm mới" ở tab đó
- **Số tiền không đúng**: Kiểm tra xem có nhập đúng không, hệ thống chỉ nhận số
- **Không chọn được máy**: Đảm bảo máy có màu xanh (sẵn sàng)
