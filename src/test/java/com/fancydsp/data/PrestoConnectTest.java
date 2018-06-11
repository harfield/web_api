package com.fancydsp.data;




import java.sql.*;
import java.util.Properties;
import com.facebook.presto.jdbc.PrestoConnection;

public class PrestoConnectTest {
    private static String driver = "com.mysql.jdbc.Driver"; //驱动
    private static String url = "jdbc:mysql://10.215.28.121:10080"; //数据库访问串
    private static String userName = "dw"; //数据库用户名
    private static String password = "123456"; //数据库密码
    private static String tableName = "vanee_pgc_play_data"; //要生成jopo对象的表名,使用;进行分割
    private static String tableMatchPattern = "%"; //数据库表名匹配模式
    private static String matchPattern = "true"; //是否启用数据库表名匹配模式功能,启用后tableName属性不被使用

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

//        Connection conn = DriverManager.getConnection("jdbc:presto://10.215.28.121:10080/hive/dwd",userName, null);

        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        String url = "jdbc:presto://10.215.28.121:10080/hive/default";
        Properties properties = new Properties();
        properties.setProperty("user", "dw");
//        properties.setProperty("password", "");
//        properties.setProperty("SSL", "true");
        PrestoConnection connection =(PrestoConnection) DriverManager.getConnection(url,properties);

        String sql = "select count(1) from hive.dwd.d_ad_request where thisdate='2018-06-01' and hour = '00' ";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            System.out.println(resultSet.getString(1));
        }
        resultSet.close();
        connection.close();
    }
}
