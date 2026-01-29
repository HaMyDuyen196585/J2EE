import java.util.Scanner;

public class Book {
    // Giữ nguyên tên thuộc tính theo đề bài yêu cầu
    private int id;
    private String title;
    private String author;
    private long price;

    public Book() {
    }

    public Book(int id, String title, String author, long price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    // Getter & Setter (Viết gọn lại trên 1 dòng cho khác biệt)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    // Hàm nhập: Thay đổi câu dẫn một chút
    public void input() {
        Scanner sc = new Scanner(System.in);

        System.out.print(">> Nhập ID sách: ");
        this.id = Integer.parseInt(sc.nextLine());

        System.out.print(">> Nhập tên sách: ");
        this.title = sc.nextLine();

        System.out.print(">> Nhập tên tác giả: ");
        this.author = sc.nextLine();

        System.out.print(">> Nhập giá bán: ");
        this.price = sc.nextLong();
    }

    // Hàm xuất: Dùng printf trực tiếp cho gọn, không khai báo biến trung gian
    public void output() {
        System.out.printf("Chi tiết: [ID: %d | Tên: %-20s | Tác giả: %-15s | Giá: %d]\n",
                id, title, author, price);
    }
}