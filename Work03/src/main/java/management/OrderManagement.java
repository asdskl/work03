package management;

import utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;


public class OrderManagement {

    //因为涉及到数据库的大量的更改，进行事务管理
    //增加新的订单
    public static void addOrder(Product[] products, int order_id) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = JdbcUtils.getConnection();
            //开启事务
            JdbcUtils.beginTransaction(connection);
            if (order_id < 0){
                throw new DataErrorException("订单id不可包括负号。");
            }
            double totalPrice = 0;

            //逐个访问订单所需的商品
            for (int i = 0; i < products.length; i++) {
                String querySql = "SELECT `price`,`stock` FROM product WHERE `name`=?";
                List<Product> result = JdbcUtils.executeQuery(connection,statement,querySql, Product.class, products[i].getName());
                if (result.isEmpty()) {
                    throw new ProductExistException("未找到此商品，订单不成立。");
                }
                //以主键访问,列表里只有一个元素
                double newStock = result.get(0).getStock() - products[i].getStock();
                if (newStock < 0) {
                    throw new DataErrorException("所需货物数量超过库存，请尽快进货，订单不成立。");
                }

                if (products[i].getStock() <= 0) {
                    throw new DataErrorException("所需货物数量不可为0或者负数。");
                }

                //计算总价
                totalPrice += result.get(0).getPrice() * products[i].getStock();

                //将商品库存更新
                String updateSql = "UPDATE `product` SET `stock`=? WHERE `name`=?";
                JdbcUtils.executeUpdate(connection,statement,updateSql,newStock,products[i].getName());

            }

            //插入订单表
            String insertSql = "INSERT INTO `order` (`order_id`,`time`,`total_price`) VALUES (?,?,?)";
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            JdbcUtils.executeUpdate(connection,statement,insertSql,order_id,timestamp,totalPrice);

            //更新订单和商品关系
            String newInsertSql = "INSERT INTO `order_product` (`order_id`,`name`,`num`) VALUES (?,?,?)";
            for (int i = 0; i < products.length; i++) {
                JdbcUtils.executeUpdate(connection,statement,newInsertSql,order_id,products[i].getName(),products[i].getStock());
            }

            //提交事务
            JdbcUtils.commitTransaction(connection);
            System.out.println("id: " + order_id + " 订单已经创建成功。");


        } catch (SQLException | ProductExistException | DataErrorException e) {
            //出现异常则事务回滚
            JdbcUtils.rollbackTransaction(connection);
            e.printStackTrace();
        } finally {
            JdbcUtils.release(connection,statement,null);
        }


    }

    //删除订单，默认是已经完成交款后的订单，无需更改库存
    public static void deleteOrder(int order_id) throws OrderExistException {

        //执行删除指令
        String deleteSql = "DELETE FROM `order` WHERE `order_id`=?";
        int row= JdbcUtils.executeUpdate(deleteSql,order_id);
        if (row == 0) {
            throw new OrderExistException("订单不存在，无法取消。");
        }

        String newDeleteSql = "DELETE FROM `order_product` WHERE `order_id`=?";
        //将order_product表里的订单，商品关系一并删除
        JdbcUtils.executeUpdate(newDeleteSql,order_id);
        System.out.println("已成功取消订单: " + order_id);

    }

    //根据特定id查询特定订单
    public static void queryOrder(int order_id) throws OrderExistException {
        //执行查询指令
        String querySql = "SELECT `order_id`,`time`,`total_price` FROM `order` WHERE `order_id`=?";
        List<Order> orders = JdbcUtils.executeQuery(querySql, Order.class,order_id);
        if (orders.isEmpty()) {
            throw new OrderExistException("订单不存在，无法查询。");
        }
        System.out.println("------------------------");
        System.out.println(orders.get(0).toString());

        String newQuerySql = "SELECT `name`,`num` FROM `order_product` WHERE `order_id`=?";
        List<Order_Product> result = JdbcUtils.executeQuery(newQuerySql, Order_Product.class,order_id);

        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i).toString());
        }
        System.out.println("------------------------");

    }

    //查询总价排序后的订单列表
    public static void queryOrderByPrice() throws OrderExistException {
        //执行排序和查询指令
        String querySql = "SELECT `order_id`,`time`,`total_price` FROM `order` ORDER BY `total_price`";
        List<Order> orders = JdbcUtils.executeQuery(querySql, Order.class);

        if (orders.isEmpty()) {
            System.out.println("暂无订单。");
            return;
        }
        System.out.println("按总价排序的订单表: ");
        System.out.println("------------------------");
        for (int i = 0; i < orders.size(); i++) {

            System.out.println(orders.get(i).toString());
            String newQuerySql = "SELECT `name`,`num` FROM `order_product` WHERE `order_id`=?";
            List<Order_Product> result = JdbcUtils.executeQuery(newQuerySql, Order_Product.class,orders.get(i).getOrder_id());

            for (int j = 0; j < result.size(); j++) {
                System.out.println(result.get(j).toString());
            }
            System.out.println("------------------------");
        }

    }

    //查询时间排序后的订单列表
    public static void queryOrderByTime() throws OrderExistException {
        //执行排序和查询指令
        String querySql = "SELECT `order_id`,`time`,`total_price` FROM `order` ORDER BY `time`";
        List<Order> orders = JdbcUtils.executeQuery(querySql, Order.class);

        if (orders.isEmpty()){
            System.out.println("暂无订单。");
            return;
        }

        System.out.println("按时间排序的订单表: ");
        System.out.println("------------------------");
        for (int i = 0; i < orders.size(); i++) {

            System.out.println(orders.get(i).toString());
            String newQuerySql = "SELECT `name`,`num` FROM `order_product` WHERE `order_id`=?";
            List<Order_Product> result = JdbcUtils.executeQuery(newQuerySql, Order_Product.class,orders.get(i).getOrder_id());

            for (int j = 0; j < result.size(); j++) {
                System.out.println(result.get(j).toString());
            }
            System.out.println("------------------------");
        }

    }


}
