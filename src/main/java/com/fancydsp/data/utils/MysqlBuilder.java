package com.fancydsp.data.utils;


import org.apache.ibatis.jdbc.AbstractSQL;

public final class MysqlBuilder{
    public static SQL build( ){
       return new SQL();
    }

    public static class SQL extends AbstractSQL<SQL> {


        @Override
        public SQL getSelf() {
            return this;
        }


        @Override
        public String toString() {
            return super.toString();
        }
    }

}
