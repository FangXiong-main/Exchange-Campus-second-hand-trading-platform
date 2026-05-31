package com.fangxiong;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String rawPassword = "123456";
        String encodePassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        System.out.println("加密后密码：" + encodePassword);
    }
}
