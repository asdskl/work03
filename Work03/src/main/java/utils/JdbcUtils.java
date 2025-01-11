package utils;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcUtils {

    private static String driver = null;
    private static String url = null;
    private static String username = null;
    private static String password = null;

    static {

        try {

            InputStream in = JdbcUtils.class.getClassLoader().getResourceAsStream("dp.properities");
            Properties properties = new Properties();
            properties.load(in);

            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

            //加载驱动
            Class.forName(driver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取连接
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(url,username,password);
    }

    //开启事务
    public static void beginTransaction(Connection connection) {

        if (connection != null) {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    //提交事务
    public static void commitTransaction(Connection connection) {

        if (connection != null) {
            try {
                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //事务回滚
    public static void rollbackTransaction(Connection connection) {

        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    //执行查询的sql语句,判断是否有相同的事物存在表中
    public static boolean executeQueryExist(String sql,Object... params) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean flag = false;
        try {

            connection = getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i+1,params[i]);
            }

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                flag = true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release(connection,statement,resultSet);
        }

        return flag;
    }

    //执行查询的sql语句,返回相应的结果集
    public static <T> List <T> executeQuery(String sql,Class<T> clazz,Object... params) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> resultList = new ArrayList<>();

        try {

            connection = getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 0;i < params.length;i++) {
                statement.setObject(i+1,params[i]);
            }

            resultSet = statement.executeQuery();
            resultList = convertResultToList(resultSet,clazz);


        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            release(connection,statement,resultSet);
        }

        return resultList;
    }


    //函数重载,执行查询的sql语句,返回相应的结果集,用于管理事务
    public static <T> List <T> executeQuery(Connection connection,PreparedStatement statement,String sql,Class<T> clazz,Object... params){

        ResultSet resultSet = null;
        List<T> resultList = new ArrayList<>();

        try {

            connection = getConnection();
            statement = connection.prepareStatement(sql);
            for (int i = 0;i < params.length;i++){
                statement.setObject(i+1,params[i]);
            }

            resultSet = statement.executeQuery();
            resultList = convertResultToList(resultSet,clazz);


        } catch (SQLException | IllegalAccessException | InstantiationException e){
            e.printStackTrace();
        } finally {
            release(null,null,resultSet);
        }

        return resultList;
    }


    //将结果集转换为对象
    public static <T>  List<T> convertResultToList(ResultSet resultSet,Class<T> clazz) throws SQLException,IllegalAccessException,InstantiationException{

        List<T> resultList = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        //遍历结果集，为实例赋值
        while (resultSet.next()) {
            //创建泛型类的实例
            T instance = clazz.newInstance();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);

                Field field = getFieldByName(clazz,columnName);
                if (field != null) {
                    field.setAccessible(true);
                     if (field.getType() == int.class && columnValue instanceof Long) {
                        // 将 Long 转换为 int
                        field.set(instance, ((Long) columnValue).intValue());
                    }
                    else if (field.getType() == java.sql.Timestamp.class && columnValue instanceof java.time.LocalDateTime) {
                        // 将 LocalDateTime 转换为 Timestamp
                        LocalDateTime localDateTime = (LocalDateTime) columnValue;
                        field.set(instance, Timestamp.valueOf(localDateTime));
                    }
                    else {
                        field.set(instance,columnValue);
                    }
                }


            }

            resultList.add(instance);

        }

        return resultList;

    }

    private static Field getFieldByName(Class<?> clazz,String fieldName) {
             try {
                 return clazz.getDeclaredField(fieldName);
             } catch (NoSuchFieldException e) {
                 return null;
             }
    }



    //执行修改的sql语句
    public static int executeUpdate(String sql,Object... params){
        Connection connection = null;
        PreparedStatement statement = null;
        int affectedRows = 0;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            for (int i = 0;i < params.length;i++) {
                statement.setObject(i+1,params[i]);
            }

            affectedRows = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release(connection,statement,null);
        }

        return affectedRows;

    }

    //函数重载,执行修改的sql语句,用于事务管理
    public static int executeUpdate(Connection connection,PreparedStatement statement,String sql,Object... params) {

        int affectedRows = 0;

        try {
            statement = connection.prepareStatement(sql);

            for (int i = 0;i < params.length;i++){
                statement.setObject(i+1,params[i]);
            }

            affectedRows = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return affectedRows;

    }



    //释放资源
    public static void release(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
