package com.orm.app;

import com.orm.app.entity.TransactionHistory;
import com.orm.app.implementation.HibernateOrm;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String connectionString="jdbc:h2:C:\\Users\\alsardar\\Desktop\\orm-with-annotation-reflection\\com\\orm\\app\\h2config\\database";
        TransactionHistory alok=new TransactionHistory(155552,"Alok","Credit", 50000.0);
        TransactionHistory tiyasa=new TransactionHistory(155553,"tiyasa","Debit", 50000.0);
        TransactionHistory anita=new TransactionHistory(155554,"anita","Debit", 50000.0);
        TransactionHistory sanjay=new TransactionHistory(155555,"sanjay","Credit", 50000.0);
        TransactionHistory rajesh=new TransactionHistory(155556,"rajesh","Credit", 50000.0);
        HibernateOrm<TransactionHistory> hibernateOrm=HibernateOrm.getConnection(connectionString);
        hibernateOrm.write(alok);
    }
}
