package ca.on.conestogac.clo.pizza_app;
import java.time.LocalDateTime;

public class PizzaOrder {
    private int id;
    private String size;
    private String base;
    private String toppings;
    private int spiceLevel;
    private String dressing;
    private String deliveryMethod;
    private String name;
    private String address;
    private String phone;
    private double total;
    private LocalDateTime orderTimestamp;

    // Constructor
    public PizzaOrder(String size, String base, String toppings, int spiceLevel, String dressing,
                      String deliveryMethod, String name, String address, String phone, double total) {
        this.size = size;
        this.base = base;
        this.toppings = toppings;
        this.spiceLevel = spiceLevel;
        this.dressing = dressing;
        this.deliveryMethod = deliveryMethod;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.total = total;
        this.orderTimestamp = LocalDateTime.now();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public String getBase() {
        return base;
    }

    public String getToppings() {
        return toppings;
    }

    public int getSpiceLevel() {
        return spiceLevel;
    }

    public String getDressing() {
        return dressing;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getOrderTimestamp() {
        return orderTimestamp;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setToppings(String toppings) {
        this.toppings = toppings;
    }

    public void setSpiceLevel(int spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public void setDressing(String dressing) {
        this.dressing = dressing;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setOrderTimestamp(LocalDateTime orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }
}

