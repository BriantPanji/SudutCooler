package model;

public class TransactionItem {
    private int id;
    private int transactionId;
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private double subtotal;
    
    public TransactionItem() {
    }
    
    public TransactionItem(int id, int transactionId, int productId, int quantity, double subtotal) {
        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    
    public TransactionItem(int transactionId, int productId, int quantity, double subtotal) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public double getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    @Override
    public String toString() {
        return "TransactionItem{" +
                "id=" + id +
                ", transactionId=" + transactionId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}
