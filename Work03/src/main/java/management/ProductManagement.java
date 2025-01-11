package management;

import utils.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class ProductManagement {

    //增加新的商品的栏目
    public static void addProduct(String name,double price,int stock) throws ProductExistException,DataErrorException {

        String querySql = "SELECT `name` FROM product WHERE `name`=?";
        boolean flag = false;
        flag = JdbcUtils.executeQueryExist(querySql,name);
        //查询到有同名商品，不可添加
        if (flag) {
            throw new ProductExistException("已有同名商品存在，不可添加。");
        }
        //其价格,库存给的数据异常
        if (price < 0 ) {
            throw new DataErrorException("添加商品的价格出现异常，不可添加商品");
        }

        if (stock < 0) {
            throw new DataErrorException("添加商品的库存数目出现异常，不可添加商品");
        }

        //添加新的商品栏目
        else {
            String insertSql = "INSERT INTO product (`name`,`price`,`stock`) VALUES (?,?,?)";
            JdbcUtils.executeUpdate(insertSql,name,price,stock);
            System.out.println("商品添加成功：" + name);
        }
    }

    //删除商品的栏目
    public static void deleteProduct(String name) throws ProductExistException {

        String querySql = "SELECT `name` FROM order_product WHERE `name`=?";
        boolean flag = false;
        flag = JdbcUtils.executeQueryExist(querySql,name);
        //查询到订单中有同名商品，不可删除
        if (flag) {
            throw new ProductExistException("在订单中有商品存在，不可删除。");
        }

        //删除该商品
        else {
            String deleteSql = "DELETE FROM `product` WHERE `name`=?";;
            int row = JdbcUtils.executeUpdate(deleteSql,name);
            if (row == 0) {
                throw new ProductExistException("未找到要删除的商品，不可删除。");
            }

            System.out.println("商品删除成功");
        }
    }

    //根据特定商品名查询商品
    public static void queryProduct (String name) throws OrderExistException {
        String querySql = "SELECT `name`,`price`,`stock` FROM `product` WHERE `name`=?";
        List<Product> products = JdbcUtils.executeQuery(querySql, Product.class,name);
        if (products.isEmpty()) {
            throw new ProductExistException("商品不存在，无法查询。");
        }
        System.out.println("查找的结果如下: ");
        System.out.println("------------------------");
        System.out.println(products.get(0).toString());
        System.out.println("------------------------");
    }

    //查询商品根据价格排序的列表
    public static void queryProductByPrice () {
        String sql = "SELECT `name`,`price`,`stock` FROM product order by price";
        List<Product> products = JdbcUtils.executeQuery(sql,Product.class);
        if (products.isEmpty()) {
            System.out.println("暂无商品上架。");
            return;
        }
        System.out.println("按价格排序的商品表: ");
        System.out.println("------------------------");
        for (int i = 0; i < products.size(); i++) {
            System.out.println(products.get(i).toString());
            System.out.println("------------------------");
        }

    }

    //修改商品的价格，由于订单已经成立，故在此不修改订单中的价格
    public static void updateProductPrice (String name,double price) throws DataErrorException,ProductExistException {

        if (price <= 0) {
            throw new DataErrorException("商品的价格不可为0或者是负数。");
        }

        String sql = "UPDATE product SET price = ? WHERE `name` = ?";
        int row = JdbcUtils.executeUpdate(sql,price,name);

        if (row == 0) {
            throw new ProductExistException("商品不存在，无法修改价格。");
        }

        System.out.println("商品价格已经成功修改。");

    }

    //修改商品的库存
    public static void updateProductStock (String name,int stock) throws DataErrorException,ProductExistException {

        if (stock < 0) {
            throw new DataErrorException("商品的库存不可为负数。");
        }

        String sql = "UPDATE product SET stock = ? WHERE `name` = ?";
        int row = JdbcUtils.executeUpdate(sql,stock,name);

        if (row == 0) {
            throw new ProductExistException("商品不存在，无法修改库存。");
        }

        System.out.println("商品库存已经成功修改。");

    }





}
