package bai3.service;

import org.springframework.stereotype.Service;
import bai3.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private List<Book> books = new ArrayList<>();
    private Long nextId = 1L;

    public List<Book> getAllBooks() {
        return books;
    }

    public void addBook(Book book) {
        book.setId(nextId.intValue());
        nextId++;
        books.add(book);
    }

    public Optional<Book> getBookById(Long id) {
        return books.stream().filter(book -> book.getId() == id.intValue()).findFirst();
    }

public void updateBook(Book updatedBook) {
books.stream()
.filter(book -> book.getId() == updatedBook.getId())
.findFirst()
.ifPresent(book -> {
book.setTitle(updatedBook.getTitle());
book.setAuthor(updatedBook.getAuthor());
});
}

    public void deleteBook(Long id) {
        books.removeIf(book -> book.getId() == id.intValue());
    }


    public void updateBook(int id, Book updatedBook) {
        books.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .ifPresent(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                });
    }

    public void deleteBook(int id) {
        books.removeIf(book -> book.getId() == id);
    }
}