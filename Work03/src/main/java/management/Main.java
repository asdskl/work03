package management;

import com.mysql.cj.jdbc.PreparedStatementWrapper;
import com.mysql.jdbc.Driver;
import utils.JdbcUtils;

import java.sql.*;


public class Main {
    public static void main(String[] args)  {

        //先清空数据库
        Connection connection = null;
        Statement statement = null;
        try {
            connection = JdbcUtils.getConnection();
            statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE `product`");
            statement.execute("TRUNCATE TABLE `order_product`");
            statement.execute("TRUNCATE TABLE `order`");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.release(connection,statement,null);
        }

        Product[] products = new Product[8];
        products[0] = new Product("手机",1000,500);
        products[1] = new Product("电脑",3125.5,100);
        products[2] = new Product("电子手表",500,1000);
        products[3] = new Product("运动鞋",200,1500);
        products[4] = new Product("足球",62.5,2000);
        products[5] = new Product("键盘",56.5,200);
        products[6] = new Product("鼠标",12.3,1500);
        products[7] = new Product("平板",2000,125);

        System.out.println("添加商品中：");

        for (int i = 0; i < 8; i++) {
            ProductManagement.addProduct(products[i].getName(),products[i].getPrice(),products[i].getStock());
        }


        System.out.println("\r\n删除部分商品：");
        ProductManagement.deleteProduct("平板");
        ProductManagement.deleteProduct("鼠标");

        System.out.println("\r\n按价位排序显示当前货架：");
        ProductManagement.queryProductByPrice();

        System.out.println("\r\n修改部分商品的价格和库存：");
        ProductManagement.updateProductPrice("电脑",3000);
        ProductManagement.updateProductPrice("足球",35);
        ProductManagement.updateProductStock("足球",90);

        System.out.println("\r\n查找足球的信息：");
        ProductManagement.queryProduct("足球");

        System.out.println("\r\n按价位排序显示当前货架：");
        ProductManagement.queryProductByPrice();

        System.out.println("\r\n创建一些订单：");
        Product[] order1 = new Product[2];
        for (int i = 0; i < 2; i++){
            order1[i] = new Product(products[i]);
            order1[i].setStock(10*(i+1));
        }
        Product[] order2 = new Product[3];
        for (int i = 0; i < 3; i++){
            order2[i] = new Product(products[i+1]);
            order2[i].setStock(5*(i+1));
        }
        Product[] order3 = new Product[3];
        for (int i = 0; i < 3; i++){
            order3[i] = new Product(products[i+2]);
            order3[i].setStock(i+9);
        }
        Product[] order4 = new Product[2];
        for (int i = 0; i < 2; i++){
            order4[i] = new Product(products[i+3]);
            order4[i].setStock(6*(i+1));
        }

        OrderManagement.addOrder(order1,2113131);
        OrderManagement.addOrder(order2,2345252);
        OrderManagement.addOrder(order3,4284112);
        OrderManagement.addOrder(order4,3124424);

        System.out.println("\r\n按价位排序显示当前订单表：");
        OrderManagement.queryOrderByPrice();

        System.out.println("\r\n查看订单3124424的详情：");
        OrderManagement.queryOrder(3124424);

        System.out.println("\r\n完成交付，删除订单3124424：");
        OrderManagement.deleteOrder(3124424);

        System.out.println("\r\n按时间排序显示当前订单表：");
        OrderManagement.queryOrderByTime();

    }
}