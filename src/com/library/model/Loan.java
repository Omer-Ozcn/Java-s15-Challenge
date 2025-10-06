package com.library.model;

import java.time.LocalDate;

public class Loan {
    private final int id;
    private final Member member;
    private final Book book;
    private final LocalDate borrowDate;
    private LocalDate returnDate;

    public Loan(int id, Member member, Book book) {
        this.id = id;
        this.member = member;
        this.book = book;
        this.borrowDate = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void close() {
        this.returnDate = LocalDate.now();
    }
}
