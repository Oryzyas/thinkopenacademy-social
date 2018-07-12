package com.thinkopen.jdbctest;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Test {
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        //System.out.println("Benvenuto. Inserire username e password.");

        MySQLAccess mySQLAccess = new MySQLAccess();

        mySQLAccess.selectAllPosts().forEach(p -> System.out.println(p));
    }

    /*public static void main(String[] args) {
        MySQLAccess mySQLAccess = new MySQLAccess();

        try {
            //User user = mySQLAccess.selectById(5);
            //System.out.println(user);

            //mySQLAccess.insert("mail11@dominio.it", "password11", "utente11", 44);

//            List<User> users = mySQLAccess.selectAll();
//            users.forEach(user -> System.out.println(user));

//            User user = mySQLAccess.selectById(5);
//            user.setEmail("nuovamail1@dominio.it");
//            user.setNome("nuovonome1");
//            user.setEta(18);
//
//            mySQLAccess.update(user);

            //User user = mySQLAccess.update(5, "nuovapassword");
            //System.out.println(user);

            // mySQLAccess.delete(1);

            //boolean success = mySQLAccess.login("nuovamail1@dominio.it", "nuovapassword");
            //System.out.println(success);

            creaUtente(mySQLAccess);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private static void creaUtente(MySQLAccess mySQLAccess) throws SQLException, ClassNotFoundException {
        User user = new User();

        System.out.println("Creazione nuovo utente");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Nome: ");
        user.setNome(scanner.nextLine());

        System.out.print("Email: ");
        user.setEmail(scanner.nextLine());

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("Età: ");
        user.setEta(scanner.nextInt());

        mySQLAccess.insert(user, password);
    }
}
