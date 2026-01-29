import java.util.*;
import java.util.stream.Collectors;

public class Main {
    // Khai báo Scanner và List là biến toàn cục (static) để dùng chung cho các hàm con
    static Scanner scanner = new Scanner(System.in);
    static List<Book> library = new ArrayList<>();

    public static void main(String[] args) {
        int choice = 0;
        do {
            printMenu(); // Gọi hàm in menu
            choice = scanner.nextInt();
            scanner.nextLine(); // Xóa bộ nhớ đệm

            switch (choice) {
                case 1 -> addBook();
                case 2 -> deleteBook();
                case 3 -> editBook();
                case 4 -> showAllBooks();
                case 5 -> findBookByTitle();
                case 6 -> filterByPrice();
                case 7 -> findByAuthorList();
                case 0 -> System.out.println("Đã thoát chương trình. Tạm biệt!");
                default -> System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại.");
            }
        } while (choice != 0);
    }

    // --- CÁC HÀM CHỨC NĂNG RIÊNG BIỆT ---

    private static void printMenu() {
        System.out.println("\n========== QUẢN LÝ THƯ VIỆN SÁCH ==========");
        System.out.println("1. Nhập thêm sách mới");
        System.out.println("2. Xóa sách theo mã ID");
        System.out.println("3. Cập nhật thông tin sách");
        System.out.println("4. Hiển thị toàn bộ danh sách");
        System.out.println("5. Tra cứu sách theo tên 'Lập trình'");
        System.out.println("6. Lọc sách theo giá (top K)");
        System.out.println("7. Tìm kiếm theo nhiều tác giả");
        System.out.println("0. Thoát");
        System.out.print(">> Mời bạn chọn: ");
    }

    // Chức năng 1
    private static void addBook() {
        System.out.println("--- THÊM SÁCH MỚI ---");
        Book b = new Book();
        b.input();
        library.add(b);
        System.out.println("-> Thêm thành công!");
    }

    // Chức năng 2
    private static void deleteBook() {
        System.out.print("Nhập ID sách muốn xóa: ");
        int id = scanner.nextInt();
        // Dùng removeIf nhưng viết kiểu block cho khác
        boolean isDeleted = library.removeIf(item -> {
            return item.getId() == id;
        });
        if(isDeleted) System.out.println("-> Đã xóa dữ liệu sách ID " + id);
        else System.out.println("-> Không tìm thấy ID này.");
    }

    // Chức năng 3
    private static void editBook() {
        System.out.print("Nhập ID sách cần sửa: ");
        int id = scanner.nextInt();

        // Dùng loop truyền thống kết hợp return để khác logic Stream của bài trước
        for (Book b : library) {
            if (b.getId() == id) {
                System.out.println("Tìm thấy sách! Mời nhập thông tin mới:");
                b.input(); // Ghi đè thông tin
                System.out.println("-> Cập nhật xong.");
                return; // Thoát hàm ngay khi sửa xong
            }
        }
        System.out.println("-> Không tìm thấy sách để sửa.");
    }

    // Chức năng 4
    private static void showAllBooks() {
        if (library.isEmpty()) {
            System.out.println("-> Thư viện đang trống.");
        } else {
            System.out.println("--- DANH SÁCH HIỆN CÓ ---");
            for (Book b : library) { // Dùng for-each thay vì method reference
                b.output();
            }
        }
    }

    // Chức năng 5
    private static void findBookByTitle() {
        System.out.println("--- KẾT QUẢ TÌM KIẾM 'LẬP TRÌNH' ---");
        List<Book> result = library.stream()
                .filter(b -> b.getTitle().toLowerCase().contains("lập trình"))
                .toList(); // Thu thập ra List trước rồi mới in (khác bài kia)

        if(result.isEmpty()) System.out.println("(Không có kết quả)");
        else result.forEach(Book::output);
    }

    // Chức năng 6
    private static void filterByPrice() {
        System.out.print("Nhập số lượng (K): ");
        int limitK = scanner.nextInt();
        System.out.print("Nhập giá trần (P): ");
        long maxPrice = scanner.nextLong();

        System.out.println("--- SÁCH GIÁ RẺ HƠN " + maxPrice + " ---");
        library.stream()
                .filter(x -> x.getPrice() <= maxPrice)
                .limit(limitK)
                .forEach(x -> x.output()); // Dùng lambda x -> x.output() thay vì Book::output
    }

    // Chức năng 7
    private static void findByAuthorList() {
        System.out.print("Nhập tên các tác giả (phân cách bằng dấu phẩy): ");
        String line = scanner.nextLine();

        // Xử lý chuỗi
        String[] arr = line.split(",");
        Set<String> targets = new HashSet<>();
        for (String s : arr) {
            targets.add(s.trim());
        }

        System.out.println("--- SÁCH CỦA TÁC GIẢ: " + targets + " ---");
        library.stream()
                .filter(book -> targets.contains(book.getAuthor()))
                .forEach(Book::output);
    }
}