package management;



public class Product {
    private String name;
    private double price;
    private int stock;

    public Product() {

    }
     public Product(String name,double price,int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;

     }

    public Product(Product product) {
        this.name = product.name;
        this.price = product.price;
        this.stock = product.stock;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "商品名: " + name + "\n" +
                "价格:" + price + "\n" +
                "库存:" + stock;
    }
}
