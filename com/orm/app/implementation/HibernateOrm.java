package com.orm.app.implementation;

import com.orm.app.annotations.Column;
import com.orm.app.annotations.PrimaryKey;
import com.orm.app.annotations.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HibernateOrm<T> {
    private Connection connection;
    private AtomicLong idIncrementer =new AtomicLong(0L);
    private AtomicInteger index=new AtomicInteger(0);
    public static <T> HibernateOrm<T> getConnection() throws SQLException {
        return new HibernateOrm<>();
    }
    private HibernateOrm() throws SQLException {
        this.connection= DriverManager.getConnection("");
    }

    public void write(T t) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> aClass = t.getClass();
        String tableName=aClass.isAnnotationPresent(Table.class)?
                        !aClass.getAnnotation(Table.class).name().isEmpty()?aClass.getAnnotation(Table.class).name(): aClass.getSimpleName()
                                                : aClass.getSimpleName();
        StringBuilder query=new StringBuilder().append("insert into ").append(tableName).append("(");
        List<Field> declaredFields = Arrays.stream(aClass.getDeclaredFields()).toList();
        StringJoiner columnJoiner=new StringJoiner(",");
        Field primaryKey = null;
        for (Field declaredField : declaredFields) {
            String columnName;
            if(declaredField.isAnnotationPresent(PrimaryKey.class)){
                primaryKey = declaredField;
                columnJoiner.add(primaryKey.getAnnotation(PrimaryKey.class).name());
            }else if(declaredField.isAnnotationPresent(Column.class)){
                columnName=declaredField.getAnnotation(Column.class).name();
                columnJoiner.add(columnName);
                }
            }
        int length = columnJoiner.toString().split(",").length;
        String qMarks = IntStream.range(0, length).mapToObj(e -> "?").collect(Collectors.joining(","));
        query.append(columnJoiner).append(")").append("values(").append(qMarks).append(")");
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            if (primaryKey != null && primaryKey.getGenericType()==Long.class) {
                preparedStatement.setLong(index.incrementAndGet(), idIncrementer.incrementAndGet());
            }
            for (Field declaredField : declaredFields) {
                Method getter=createGetter(declaredField,t);
                switch (extractClassName(declaredField)){
                    case "Integer", "int":
                        preparedStatement.setInt(index.incrementAndGet(), (int) getter.invoke(t,null));
                        break;
                    case "Long","long":
                        preparedStatement.setLong(index.incrementAndGet(), (long) getter.invoke(t,null));
                        break;
                    case "String":
                        preparedStatement.setString(index.incrementAndGet(), (String) getter.invoke(t,null));
                        break;
                    case "Double","double":
                        preparedStatement.setDouble(index.incrementAndGet(), (double) getter.invoke(t,null));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + extractClassName(declaredField));
                }
            }
            preparedStatement.executeUpdate();
        }
    }

    private Method createGetter(Field declaredField, T t) throws NoSuchMethodException {
        String getterName = "get" + declaredField.getName().substring(0,1).toUpperCase() + declaredField.getName().substring(1);
        return t.getClass().getDeclaredMethod(getterName,null);
    }

    private String extractClassName(Field declaredField) {
        int length = declaredField.getGenericType().getTypeName().split(".").length;
        return declaredField.getGenericType().getTypeName().split(".")[length-1];
    }
}
