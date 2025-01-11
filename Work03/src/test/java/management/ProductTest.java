package management;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTest {

    @BeforeAll
    static void setUp() throws SQLException {
        // 获取数据库连接
        Connection connection = JdbcUtils.getConnection();
        Statement statement = connection.createStatement();
        // 清理数据
        statement.execute("TRUNCATE TABLE `product`");
        statement.execute("TRUNCATE TABLE `order_product`");
        statement.execute("TRUNCATE TABLE `order`");
        //插入原始数据
        statement.execute("INSERT INTO product (`name`, `price`,`stock`) VALUES ('new product', 15.0,20)");
        statement.execute("INSERT INTO `order` (`order_id`, `time`,`total_price`) VALUES ('121213', NOW(),30)");
        statement.execute("INSERT INTO `order_product` (`order_id`, `name`,`num`) VALUES ('121213', 'new product',2)");
        statement.execute("INSERT INTO product (`name`, `price`,`stock`) VALUES ('new product5', 2.0,35), ('new product6', 20.0,25)");

        //关闭数据库连接
        JdbcUtils.release(connection,statement,null);
    }



    @Test
    @Order(1)
    //测试添加商品
    void testAddProduct(){
        ProductManagement.addProduct("new product1",100,10);
    }

    @Test
    @Order(2)
    //测试添加已经存在商品的栏目,抛出商品存在异常类
    void testAddProduct2() {
       ProductExistException exception = assertThrows(ProductExistException.class, () -> {
           ProductManagement.addProduct("new product2",100,10);
           ProductManagement.addProduct("new product2",100,10);
       });
       System.out.println(exception.getMessage());
   }

    @Test
    @Order(3)
    //测试添加价格异常的商品，抛出数据异常类
    void testAddProduct3() {
        DataErrorException exception = assertThrows(DataErrorException.class, () -> {
            ProductManagement.addProduct("new product3", -1, 10);
        });
        System.out.println(exception.getMessage());
    }

    @Test
    @Order(4)
    //测试添加库存异常的商品，抛出数据异常类
    void testAddProduct4() {
        DataErrorException exception = assertThrows(DataErrorException.class, () -> {
            ProductManagement.addProduct("new product4", 100, -1);
        });
        System.out.println(exception.getMessage());
    }

    @Test
    @Order(5)
    //测试删除商品
    void testDeleteProduct(){
        ProductManagement.deleteProduct("new product2");
    }

    @Test
    @Order(6)
    //测试删除订单中有存在的商品,抛出商品存在类异常，不可删除商品
    void testDeleteProduct2() {
        ProductExistException exception = assertThrows(ProductExistException.class, () -> {
            ProductManagement.deleteProduct("new product");
        });
        System.out.println(exception.getMessage());
    }

    @Test
    @Order(7)
    //删除不存在的商品，抛出商品存在异常类
    void testDeleteProduct3() {
        ProductExistException exception = assertThrows(ProductExistException.class, () -> {
            ProductManagement.deleteProduct("new");
        });

        System.out.println(exception.getMessage());
    }

    @Test
    @Order(8)
    //修改指定商品的价格
    void testUpdatePrice() {

        ProductManagement.updateProductPrice("new product5",12);
    }


    @Test
    @Order(9)
    //修改商品价格，但是给定的价格异常，抛出数据异常类
    void testUpdatePrice2() {
        DataErrorException exception = assertThrows(DataErrorException.class, () -> {
            ProductManagement.updateProductPrice("new product5",-12);
        });

        System.out.println(exception.getMessage());
    }

    @Test
    @Order(10)
    //修改不存在的商品，抛出商品存在异常类
    void testUpdatePrice3() {
        ProductExistException exception = assertThrows(ProductExistException.class, () -> {
            ProductManagement.updateProductPrice("new",12);
        });

        System.out.println(exception.getMessage());
    }

    @Test
    @Order(11)
    //修改指定商品的库存
    void testUpdateStock() {

        ProductManagement.updateProductStock("new product5",35);
    }


    @Test
    @Order(12)
    //修改商品库存，但是给定的库存异常，抛出数据异常类
    void testUpdateStock2() {
        DataErrorException exception = assertThrows(DataErrorException.class, () -> {
            ProductManagement.updateProductStock("new product5",-35);
        });

        System.out.println(exception.getMessage());
    }

    @Test
    @Order(13)
    //修改不存在的商品，抛出商品存在异常类
    void testUpdateStock3() {
        ProductExistException exception = assertThrows(ProductExistException.class, () -> {
            ProductManagement.updateProductStock("new",35);
        });

        System.out.println(exception.getMessage());
    }

    @Test
    @Order(14)
    //查找特定商品的信息
    void testQueryProduct() {
            ProductManagement.queryProduct("new product1");

    }

    @Test
    @Order(15)
    //查找不存在的商品的信息，抛出商品存在类异常
    void testQueryProduct2() {
        ProductExistException exception = assertThrows(ProductExistException.class, () -> {
            ProductManagement.queryProduct("new");
        });

        System.out.println(exception.getMessage());
    }

    @Test
    @Order(16)
    //查找根据价格排序的商品表
    void testQueryProductByPrice() {

        ProductManagement.queryProductByPrice();

    }



}
