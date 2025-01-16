<p align="center">
  <img src="https://th.bing.com/th/id/R.053d85b2b7f3e38adc75cfafdb496d06?rik=bxak%2f%2bZy%2f40KNA&pid=ImgRaw&r=0" alt="Banner Description" width="100%">
</p>

# Đề tài quản lý bán hàng với Android Java
- <b>Người thực hiện: </b>
    - Nguyễn Vũ Duy
    - Nguyễn Hoàng Dung
    - Đặng Nguyễn Tiền Hậu
- <b>Thời gian: </b>27/11/2024 - Tuần 9 (Báo cáo giữa kỳ) - Báo cáo cuối kỳ
-----------------------
### Tổng quan về dự án
- Dự án là sản phẩm dành cho báo cáo học phần "Lập trình trên thiết bị di động", thuộc khoa Thông tin & Truyền thông, trường Đại học Kiên Giang
- Các công nghệ được sử dụng:
    - Java
    - SQLite
    - Android >= 7.0
### Các yêu cầu chính
- Chức năng 1: Đăng nhập cho người dùng nhập tài khoản và mật khẩu. <img style="width: 30px" src="https://th.bing.com/th/id/R.f880f7df792210e7ac3371fe0bfa47e1?rik=bWF8%2fmUdbqltnQ&riu=http%3a%2f%2fclipart-library.com%2fimages_k%2fgreen-check-mark-icon-transparent-background%2fgreen-check-mark-icon-transparent-background-23.png&ehk=Dkj1%2fxqHSU4%2bHd5aUo30w4PfwUWaS0TG9iksB7lQtAI%3d&risl=&pid=ImgRaw&r=0">
    + Nếu người dùng nhập đúng thì cho vào danh sách "Đơn đặt hàng". 
    + Nếu người dùng nhập sai thông báo “Thông tin đăng nhập không đúng vui lòng nhập lại!” 
- Chức năng 2: Đăng ký tài khoản. Kiểm tra tài khoản đã tồn tại không cho phép đăng ký nữa. <img style="width: 30px" src="https://th.bing.com/th/id/R.f880f7df792210e7ac3371fe0bfa47e1?rik=bWF8%2fmUdbqltnQ&riu=http%3a%2f%2fclipart-library.com%2fimages_k%2fgreen-check-mark-icon-transparent-background%2fgreen-check-mark-icon-transparent-background-23.png&ehk=Dkj1%2fxqHSU4%2bHd5aUo30w4PfwUWaS0TG9iksB7lQtAI%3d&risl=&pid=ImgRaw&r=0">
- Chức năng 3: Thêm/Sửa/Xóa và hiển thị danh sách loại sản phẩm: bao gồm thuộc tính mã loại sản phẩm, tên sản phẩm. <img style="width: 30px" src="https://th.bing.com/th/id/R.f880f7df792210e7ac3371fe0bfa47e1?rik=bWF8%2fmUdbqltnQ&riu=http%3a%2f%2fclipart-library.com%2fimages_k%2fgreen-check-mark-icon-transparent-background%2fgreen-check-mark-icon-transparent-background-23.png&ehk=Dkj1%2fxqHSU4%2bHd5aUo30w4PfwUWaS0TG9iksB7lQtAI%3d&risl=&pid=ImgRaw&r=0">
- Chức năng 4: Thêm/Sửa/Xóa và hiển thị danh sách sản phẩm: bao gồm thuộc tính mã sản phẩm, tên sản phẩm, loại sản phẩm(lấy từ danh sách loại sản phẩm), đơn giá, mô tả… <img style="width: 30px" src="https://th.bing.com/th/id/R.f880f7df792210e7ac3371fe0bfa47e1?rik=bWF8%2fmUdbqltnQ&riu=http%3a%2f%2fclipart-library.com%2fimages_k%2fgreen-check-mark-icon-transparent-background%2fgreen-check-mark-icon-transparent-background-23.png&ehk=Dkj1%2fxqHSU4%2bHd5aUo30w4PfwUWaS0TG9iksB7lQtAI%3d&risl=&pid=ImgRaw&r=0">
- Chức năng 5: Đơn đặt hàng: cho người dùng thêm/ sửa/ xóa thông tin đơn hàng. Đơn hàng quá khứ không được sửa/xóa.
    + Đơn hàng: mã đơn hàng, tổng tiền = tổng thành tiền các đơn hàng chi tiết, ngày đặt hàng, ngày giao hàng, người tạo đơn hàng(là tài khoản đăng nhập), ghi chú.
    + Đơn hàng chi tiết: mã chi tiết đơn hàng, mã đơn hàng, mã sản phẩm(lấy từ danh sách sản phẩm), đơn giá(lấy theo mã sản phẩm chọn), số lượng, thành tiền = đơn giá * số lượng.
- Chức năng 6: Thống kê những đơn hàng bán chạy nhất trong ngày/tuần/tháng/năm.
---------------------------------------------------------------
<p align="center">
    <img src="https://static.vecteezy.com/system/resources/previews/000/450/367/original/android-vector-icon.jpg" width="200" alt="React Native Logo" style="margin-right: 100px;" />
    <img src="https://th.bing.com/th/id/OIP.rpClliXp1N5A17N3fGgMZwHaDL?rs=1&pid=ImgDetMain" width="200" alt="Next Logo" style="margin-bottom: 45px;" />
</p>