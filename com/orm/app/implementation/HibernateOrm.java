package com.orm.app.implementation;

import com.orm.app.annotations.Column;
import com.orm.app.annotations.PrimaryKey;
import com.orm.app.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class HibernateOrm<T> {
    Connection connection;
    public static <T> HibernateOrm<T> getConnection() throws SQLException {
        return new HibernateOrm<T>();
    }
    private HibernateOrm() throws SQLException {
        /*this.connection= DriverManager.getConnection("");*/
    }

    public void write(T t) throws IllegalAccessException {
        Class<?> aClass = t.getClass();
        String tableName=aClass.isAnnotationPresent(Table.class)?aClass.getAnnotation(Table.class).name():"";
        StringBuilder query=new StringBuilder().append("insert into ").append(tableName);
        StringBuilder columnNames= new StringBuilder().append("(");
        StringBuilder values= new StringBuilder().append("(");
        List<Field> declaredFields = Arrays.stream(aClass.getDeclaredFields()).toList();
        for (Field declaredField : declaredFields) {
            String primaryKey="";
            String columnName="";
            if(declaredField.isAnnotationPresent(PrimaryKey.class)){
                primaryKey = declaredField.getAnnotation(PrimaryKey.class).name();
                columnNames.append(primaryKey).append(",");
                values.append(declaredField.get(t)).append(",");
            }else if(declaredField.isAnnotationPresent(Column.class)){
                columnName=declaredField.getAnnotation(Column.class).name();
                if(declaredFields.indexOf(declaredField)==declaredFields.size()-1){
                    columnNames.append(columnName).append(")");
                    values.append(declaredField.get(t)).append(")");
                }else{
                    columnNames.append(columnName).append(",");
                    values.append(declaredField.get(t)).append(",");
                }
            }

        }
        query.append(columnNames).append(" ").append(values);
        System.out.println();
        System.out.println(query);
    }
}
