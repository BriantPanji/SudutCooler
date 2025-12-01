package model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private LocalDateTime tanggal;
    private double total;
    private String kasir;

    public Transaction() {
    }

    public Transaction(int id, LocalDateTime tanggal, double total, String kasir) {
        this.id = id;
        this.tanggal = tanggal;
        this.total = total;
        this.kasir = kasir;
    }

    public Transaction(LocalDateTime tanggal, double total, String kasir) {
        this.tanggal = tanggal;
        this.total = total;
        this.kasir = kasir;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDateTime tanggal) {
        this.tanggal = tanggal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getKasir() {
        return kasir;
    }

    public void setKasir(String kasir) {
        this.kasir = kasir;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", tanggal=" + tanggal +
                ", total=" + total +
                ", kasir='" + kasir + '\'' +
                '}';
    }
}
