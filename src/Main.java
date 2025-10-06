package com.library;

import com.library.model.*;
import com.library.service.LibraryService;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        service.addStudent("Ali");
        service.addFaculty("Ayşe");

        service.addBook("Java Çalışma Notları", "Robert Martin", Category.STUDYBOOK);
        service.addBook("Bilim Dergisi", "TÜBİTAK", Category.JOURNAL);
        service.addBook("Magazin Dergisi", "SABAH", Category.MAGAZINE);

        while (true) {
            System.out.println("\n--- Kütüphane Menüsü ---");
            System.out.println("1) Kitap ekle");
            System.out.println("2) Kitap ara (ID)");
            System.out.println("3) Kitap ara (isim)");
            System.out.println("4) Kitap ara (yazar)");
            System.out.println("5) Kategoriye göre listele");
            System.out.println("6) Kitap güncelle");
            System.out.println("7) Kitap sil");
            System.out.println("8) Ödünç al");
            System.out.println("9) İade et");
            System.out.println("10) Kitabı kim aldı?");
            System.out.println("11) Tüm kitaplar");
            System.out.println("0) Çıkış");
            System.out.print("Seçiminiz: ");

            String secim = in.nextLine().trim();
            try {
                switch (secim) {
                    case "1" -> addBook();
                    case "2" -> searchById();
                    case "3" -> searchByTitle();
                    case "4" -> searchByAuthor();
                    case "5" -> listByCategory();
                    case "6" -> updateBook();
                    case "7" -> deleteBook();
                    case "8" -> borrow();
                    case "9" -> giveBack();
                    case "10" -> whoHas();
                    case "11" -> listAll();
                    case "0" -> { System.out.println("Çıkış yapılıyor..."); return; }
                    default -> System.out.println("Geçersiz seçim!");
                }
            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }
    }

    private static void addBook() {
        System.out.print("Başlık: "); String title = in.nextLine();
        System.out.print("Yazar: "); String author = in.nextLine();
        System.out.print("Kategori (JOURNAL, STUDYBOOK, MAGAZINE): ");
        Category c = Category.valueOf(in.nextLine().toUpperCase());
        Book b = service.addBook(title, author, c);
        System.out.println("Kitap eklendi, ID: " + b.getId());
    }

    private static void searchById() {
        System.out.print("ID: "); int id = Integer.parseInt(in.nextLine());
        Book b = service.findBookById(id);
        if (b == null) System.out.println("Bulunamadı.");
        else System.out.println(b.getId() + " - " + b.getTitle() + " (" + b.getAuthor().getName() + ") [" + b.getStatus() + "]");
    }

    private static void searchByTitle() {
        System.out.print("Aranacak başlık: "); String q = in.nextLine();
        ArrayList<Book> result = service.findBooksByTitle(q);
        if (result.isEmpty()) System.out.println("Sonuç yok.");
        for (Book b : result)
            System.out.println(b.getId() + " - " + b.getTitle() + " (" + b.getAuthor().getName() + ")");
    }

    private static void searchByAuthor() {
        System.out.print("Yazar adı: "); String q = in.nextLine();
        ArrayList<Book> result = service.findBooksByAuthor(q);
        if (result.isEmpty()) System.out.println("Sonuç yok.");
        for (Book b : result)
            System.out.println(b.getId() + " - " + b.getTitle());
    }

    private static void listByCategory() {
        System.out.print("Kategori (JOURNAL, STUDYBOOK, MAGAZINE): ");
        Category c = Category.valueOf(in.nextLine().toUpperCase());
        ArrayList<Book> result = service.listByCategory(c);
        if (result.isEmpty()) System.out.println("Sonuç yok.");
        for (Book b : result)
            System.out.println(b.getId() + " - " + b.getTitle());
    }

    private static void updateBook() {
        System.out.print("Kitap ID: "); int id = Integer.parseInt(in.nextLine());
        System.out.print("Yeni başlık (boş geçebilirsiniz): "); String t = in.nextLine();
        if (t.isBlank()) t = null;
        System.out.print("Yeni yazar (boş geçebilirsiniz): "); String a = in.nextLine();
        if (a.isBlank()) a = null;
        System.out.print("Yeni kategori (boş geçebilirsiniz): "); String cat = in.nextLine();
        Category c = cat.isBlank() ? null : Category.valueOf(cat.toUpperCase());

        boolean ok = service.updateBook(id, t, a, c);
        System.out.println(ok ? "Güncellendi" : "Bulunamadı");
    }

    private static void deleteBook() {
        System.out.print("Silinecek kitap ID: "); int id = Integer.parseInt(in.nextLine());
        System.out.println(service.deleteBook(id) ? "Silindi" : "Bulunamadı veya kitap ödünçte.");
    }

    private static void borrow() {
        System.out.print("Üye ID: "); int m = Integer.parseInt(in.nextLine());
        System.out.print("Kitap ID: "); int b = Integer.parseInt(in.nextLine());
        Invoice inv = service.borrowBook(m, b);
        if (inv == null) System.out.println("İşlem başarısız! (limit dolu / kitap meşgul / ID yanlış)");
        else System.out.println("Ödünç verildi. Fatura: +" + inv.amount() + " TL (" + inv.description() + ")");
    }

    private static void giveBack() {
        System.out.print("Kitap ID: "); int b = Integer.parseInt(in.nextLine());
        Invoice inv = service.returnBook(b);
        if (inv == null) System.out.println("Kitap zaten ödünçte değil.");
        else System.out.println("İade alındı. Fatura: " + inv.amount() + " TL (" + inv.description() + ")");
    }

    private static void whoHas() {
        System.out.print("Kitap ID: "); int b = Integer.parseInt(in.nextLine());
        Loan loan = service.whoHasBook(b);
        if (loan == null) System.out.println("Kitap kütüphanede (boşta).");
        else System.out.println("Kitap " + loan.getMember().getName() + " adlı üyede.");
    }

    private static void listAll() {
        for (Book b : service.allBooks())
            System.out.println(b.getId() + " - " + b.getTitle() + " [" + b.getStatus() + "]");
    }
}
