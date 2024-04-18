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
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        String tableName=aClass.isAnnotationPresent(Table.class)?
                        !aClass.getAnnotation(Table.class).name().isEmpty()?aClass.getAnnotation(Table.class).name(): aClass.getSimpleName()
                                                : aClass.getSimpleName();
        StringBuilder query=new StringBuilder().append("insert into ").append(tableName).append("(");
        List<Field> declaredFields = Arrays.stream(aClass.getDeclaredFields()).toList();
        StringJoiner columnJoiner=new StringJoiner(",");
        String primaryKey;
        for (Field declaredField : declaredFields) {
            String columnName;
            if(declaredField.isAnnotationPresent(PrimaryKey.class)){
                primaryKey = declaredField.getAnnotation(PrimaryKey.class).name();
            }else if(declaredField.isAnnotationPresent(Column.class)){
                columnName=declaredField.getAnnotation(Column.class).name();
                columnJoiner.add(columnName);
                }
            }
        int length = columnJoiner.toString().split(",").length;
        String qMarks = IntStream.range(0, length).mapToObj(e -> "?").collect(Collectors.joining(","));
        query.append(columnJoiner).append(")").append("values(").append(qMarks).append(")");
    }
}
