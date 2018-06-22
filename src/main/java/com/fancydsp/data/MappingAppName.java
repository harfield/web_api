package com.fancydsp.data;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MappingAppName {
    static Map<String,String> nameCn = new HashMap<String,String>();
    static Connection connection = null;
    static PreparedStatement psm = null;
    static {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://10.215.28.69:8066/ad_report?useUnicode=true&characterEncoding=UTF-8","report","69PkFs7ty");
            psm = connection.prepareStatement("select domain_app_cn from ad_report.media_domain_app_map where is_mobile=1 and domain_app=?;");
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) throws IOException, SQLException {
        if(args.length < 3){
            System.out.println("unixtimestamp  input output ");
            System.exit(1);
        }
        BufferedReader bfr = new BufferedReader(new FileReader(new File(args[1])));
        BufferedWriter bfw = new BufferedWriter(new FileWriter(new File(args[2])));
        String line = null;
        JSONObject map = new JSONObject();
        JSONArray array = new JSONArray();
        while ((line=bfr.readLine())!=null){
            String[] split = line.split("\t");
            map.clear();

            map.put("time",args[0]);
            map.put("mac",split[1]);
            map.put("imei",split[2]);
            array.clear();
            for(String packName : split[6].split(",")){
                   array.add(packName +"," + getName(packName) + ",0");
            }
            map.put("applist",array);
            bfw.write(map.toJSONString());
            bfw.write("\n");
        }
        bfr.close();
        bfw.close();
        psm.close();
        connection.close();
    }
    static String getName(String packName) throws SQLException {
        if(nameCn.containsKey(packName)){
            return nameCn.get(packName);
        }else {
            psm.setString(1, packName.trim());
            ResultSet resultSet = psm.executeQuery();
            String r = "";
            if (resultSet.next()) {
                r = resultSet.getString(1);
            }
            nameCn.put(packName, r);
            resultSet.close();
            return r;

        }

    }
}
