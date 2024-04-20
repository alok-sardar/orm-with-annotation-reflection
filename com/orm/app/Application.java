package com.orm.app;

import com.orm.app.config.PropertiesLoader;
import com.orm.app.config.DbConnection;
import com.orm.app.entity.TransactionHistory;
import com.orm.app.implementation.HibernateOrm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Properties;

public class Application {
    public static void main(String[] args) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, InterruptedException {
        Properties properties = new PropertiesLoader().loadProperties();
        DbConnection dbConnection =new DbConnection(properties.getProperty("h2.url.alok.laptop"),properties.getProperty("h2.username"),"password");
        TransactionHistory alok=new TransactionHistory(155552,"Alok","Credit", 50000.0);
        TransactionHistory tiyasa=new TransactionHistory(155553,"tiyasa","Debit", 50000.0);
        TransactionHistory anita=new TransactionHistory(155554,"anita","Debit", 50000.0);
        TransactionHistory sanjay=new TransactionHistory(155555,"sanjay","Credit", 50000.0);
        TransactionHistory rajesh=new TransactionHistory(155556,"rajesh","Credit", 50000.0);
        HibernateOrm<TransactionHistory> hibernateOrm=HibernateOrm.getConnection(dbConnection);
        hibernateOrm.write(alok);
        Thread.currentThread().join();
    }
}
