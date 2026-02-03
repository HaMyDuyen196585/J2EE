package bai3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import bai3.model.Book;
import bai3.service.BookService;



@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    // Hiển thị form thêm sách
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    // Thêm sách mới
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.addBook(book);
        return "redirect:/books";
    }

    // Hiển thị form sửa sách
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        bookService.getBookById(id).ifPresent(book -> 
            model.addAttribute("book", book));
        return "edit-book";
    }

    // Cập nhật sách
    @PostMapping("/edit")
    public String updateBook(@ModelAttribute Book book) {
        bookService.updateBook(book);
        return "redirect:/books";
    }

    // Xóa sách
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}