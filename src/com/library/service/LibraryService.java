package com.library.service;

import com.library.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LibraryService {

    private int bookSeq = 1000;
    private int memberSeq = 2000;
    private int loanSeq = 3000;
    private int invoiceSeq = 4000;

    private int nextBookId()   {
        return ++bookSeq;
    }
    private int nextMemberId() {
        return ++memberSeq;
    }
    private int nextLoanId()   {
        return ++loanSeq;
    }
    private int nextInvoiceId(){
        return ++invoiceSeq;
    }

    private final HashMap<Integer, Book> booksById = new HashMap<>();
    private final ArrayList<Book> books = new ArrayList<>();
    private final HashSet<String> categories = new HashSet<>();

    private final HashMap<Integer, Member> membersById = new HashMap<>();

    private final HashMap<Integer, Loan> activeLoansByBookId = new HashMap<>();
    private final HashMap<Integer, ArrayList<Loan>> loansByMemberId = new HashMap<>();

    private final ArrayList<Invoice> invoices = new ArrayList<>();

    private static final BigDecimal DEPOSIT = BigDecimal.valueOf(50);

    public Book addBook(String title, String authorName, Category category) {
        int id = nextBookId();
        Book b = new Book(id, title, new Author(authorName), category);
        booksById.put(id, b);
        books.add(b);
        categories.add(category.name());
        return b;
    }

    public Book findBookById(int id) { return booksById.get(id); }

    public ArrayList<Book> findBooksByTitle(String query) {
        ArrayList<Book> result = new ArrayList<>();
        if (query == null) return result;
        String q = query.toLowerCase();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(q)) result.add(b);
        }
        return result;
    }

    public ArrayList<Book> findBooksByAuthor(String authorName) {
        ArrayList<Book> result = new ArrayList<>();
        if (authorName == null) return result;
        for (Book b : books) {
            if (b.getAuthor().getName().equalsIgnoreCase(authorName)) result.add(b);
        }
        return result;
    }

    public ArrayList<Book> listByCategory(Category cat) {
        ArrayList<Book> result = new ArrayList<>();
        if (cat == null) return result;
        for (Book b : books) {
            if (b.getCategory() == cat) result.add(b);
        }
        return result;
    }

    public boolean updateBook(int id, String newTitle, String newAuthor, Category newCat) {
        Book b = booksById.get(id);
        if (b == null) return false;

        if (newTitle != null && !newTitle.isBlank()) b.setTitle(newTitle);
        if (newAuthor != null && !newAuthor.isBlank()) b.setAuthor(new Author(newAuthor));
        if (newCat != null) { b.setCategory(newCat); categories.add(newCat.name()); }
        return true;
    }

    public boolean deleteBook(int id) {
        if (activeLoansByBookId.containsKey(id)) return false;
        Book b = booksById.remove(id);
        if (b == null) return false;
        books.remove(b);
        return true;
    }

    public ArrayList<Book> allBooks() { return new ArrayList<>(books); }

    public Member addStudent(String name) {
        Member m = new Student(nextMemberId(), name);
        membersById.put(m.getId(), m);
        return m;
    }

    public Member addFaculty(String name) {
        Member m = new Faculty(nextMemberId(), name);
        membersById.put(m.getId(), m);
        return m;
    }

    public Member findMember(int id) { return membersById.get(id); }

    public Invoice borrowBook(int memberId, int bookId) {
        Member m = membersById.get(memberId);
        Book b = booksById.get(bookId);

        if (m == null || b == null) return null;
        if (!m.canBorrow()) return null;
        if (activeLoansByBookId.containsKey(bookId)) return null;

        Loan loan = new Loan(nextLoanId(), m, b);
        activeLoansByBookId.put(bookId, loan);

        loansByMemberId.computeIfAbsent(memberId, k -> new ArrayList<>()).add(loan);

        b.setStatus(Book.Status.BORROWED);
        m.incLoan();

        Invoice inv = new Invoice(nextInvoiceId(), memberId, loan.getId(),
                DEPOSIT, "Borrow deposit");
        invoices.add(inv);
        return inv;
    }

    public Invoice returnBook(int bookId) {
        Loan loan = activeLoansByBookId.remove(bookId);
        if (loan == null) return null;

        loan.close();

        Book b = loan.getBook();
        b.setStatus(Book.Status.AVAILABLE);

        Member m = loan.getMember();
        m.decLoan();

        Invoice refund = new Invoice(nextInvoiceId(), m.getId(), loan.getId(),
                DEPOSIT.negate(), "Refund on return");
        invoices.add(refund);
        return refund;
    }

    public Loan whoHasBook(int bookId) { return activeLoansByBookId.get(bookId); }

    public ArrayList<Invoice> invoicesOfMember(int memberId) {
        ArrayList<Invoice> result = new ArrayList<>();
        for (Invoice i : invoices) if (i.getMemberId() == memberId) result.add(i);
        return result;
    }
}
