package management;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest {


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
        statement.execute("INSERT INTO product (`name`, `price`,`stock`) VALUES ('newProduct1', 5.0,10), ('newProduct2', 10.0,20),('newProduct4', 15.0,15)");


        //关闭数据库连接
        JdbcUtils.release(connection,statement,null);
    }

    @Test
    @Order(1)
    //正常添加商品订单
    void testAddOrder() {
        Product[] products = new Product[2];
        products[0] = new Product("newProduct1",0,3);
        products[1] = new Product("newProduct2",0,5);

        Product[] products2 = new Product[2];
        products2[0] = new Product("newProduct1",0,1);
        products2[1] = new Product("newProduct4",0,2);

        Product[] products3 = new Product[3];
        products3[0] = new Product("newProduct1",0,1);
        products3[1] = new Product("newProduct2",0,2);
        products3[2] = new Product("newProduct4",0,3);

        OrderManagement.addOrder(products,221213);
        OrderManagement.addOrder(products2,212134);
        OrderManagement.addOrder(products3,122324);

    }

    @Test
    @Order(2)
    //测试添加不存在的商品的订单，会抛出产品存在异常类
    void testAddOrder2() {
        Product[] products = new Product[1];
        products[0] = new Product("newProduct3",0,3);

        OrderManagement.addOrder(products,232213);

    }

    @Test
    @Order(3)
    //测试添加包含超量商品的订单，会抛出数据异常类
    void testAddOrder3() {
        Product[] products = new Product[1];
        products[0] = new Product("newProduct1",0,100);

        OrderManagement.addOrder(products,221643);

    }

    @Test
    @Order(4)
    //测试添加包含数量为负数的商品的订单，会抛出数据异常类
    void testAddOrder4() {
        Product[] products = new Product[1];
        products[0] = new Product("newProduct1",0,-1);

        OrderManagement.addOrder(products,221643);

    }

    @Test
    @Order(5)
    //测试添加id为负数的商品的订单，会抛出数据异常类
    void testAddOrder5() {
        Product[] products = new Product[1];
        products[0] = new Product("newProduct1",0,1);

        OrderManagement.addOrder(products,-249643);

    }

    @Test
    @Order(6)
    //测试查询单独的订单
    void testQueryOrder(){
        OrderManagement.queryOrder(221213);
    }

    @Test
    @Order(7)
    //测试查询不存在的订单
    void testQueryOrder2() {
        OrderExistException exception = assertThrows(OrderExistException.class, () -> {
            OrderManagement.queryOrder(232311);
        });
        System.out.println(exception.getMessage());
    }

    @Test
    @Order(8)
    //测试删除订单
    void testDeleteOrder(){
        OrderManagement.deleteOrder(221213);
    }

    @Test
    @Order(9)
    //测试删除不存在的订单,抛出订单存在异常类
    void testDeleteOrder2() {
        OrderExistException exception = assertThrows(OrderExistException.class, () -> {
            OrderManagement.deleteOrder(221213);
        });
        System.out.println(exception.getMessage());
    }

    @Test
    @Order(10)
    //查询据总价排序后的订单表
    void testQueryOrderByPrice() {

        OrderManagement.queryOrderByPrice();
    }

    @Test
    @Order(11)
    //查询据时间排序后的订单表
    void testQueryOrderByTime() {

        OrderManagement.queryOrderByTime();
    }


}
