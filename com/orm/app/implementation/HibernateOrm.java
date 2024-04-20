package com.orm.app.implementation;

import com.orm.app.annotations.Column;
import com.orm.app.annotations.PrimaryKey;
import com.orm.app.annotations.Table;
import com.orm.app.config.DbConnection;
import com.orm.app.config.H2Loader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HibernateOrm<T> {
    private final Connection connection;
    private final AtomicLong idIncrementer =new AtomicLong(0L);
    private final AtomicInteger index=new AtomicInteger(0);

    public HibernateOrm(DbConnection connectionProperties) throws SQLException {
        this.connection= DriverManager.getConnection(connectionProperties.getUrl(),connectionProperties.getUserName(),"password");
        H2Loader.startWebserver(connection);
    }

    public static <T> HibernateOrm<T> getConnection(DbConnection connectionProperties) throws SQLException {
        return new HibernateOrm<>(connectionProperties);
    }

    public void write(T t) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        prepareCreateStatement(t);
        Class<?> aClass = t.getClass();
        String tableName;
        if (aClass.isAnnotationPresent(Table.class)) {
            tableName = !aClass.getAnnotation(Table.class).name().isEmpty()?
                                    aClass.getAnnotation(Table.class).name() : aClass.getSimpleName();
        }else{
            tableName = aClass.getSimpleName();
        }
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
        AtomicInteger counter=new AtomicInteger(0);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            System.out.println("Update block: "+counter.incrementAndGet());
            if (primaryKey != null && primaryKey.getGenericType()==Long.class) {
                preparedStatement.setLong(index.incrementAndGet(), idIncrementer.incrementAndGet());
            }
            for (Field declaredField : declaredFields) {
                if(declaredField.isAnnotationPresent(PrimaryKey.class)){
                    continue;
                }
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

    private void prepareCreateStatement(T t) throws SQLException {
        Class<?> clazz = t.getClass();
        String tableName=clazz.getSimpleName();
        String columnName = null;
        StringBuilder primaryKey=new StringBuilder();
        String type;
        StringBuilder createSql=new StringBuilder();
        StringJoiner tableColumnJoiner=new StringJoiner(",");
        if(clazz.isAnnotationPresent(Table.class)&&!clazz.getAnnotation(Table.class).name().isEmpty()){
            tableName=clazz.getAnnotation(Table.class).name();
        }
        createSql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(\n");
        List<Field> declaredFields = Arrays.stream(clazz.getDeclaredFields()).toList();
        for (Field declaredField : declaredFields) {
            type=extractClassName(declaredField);
            if(declaredField.isAnnotationPresent(PrimaryKey.class)){
                primaryKey.append(columnName=!declaredField.getAnnotation(PrimaryKey.class).name().isEmpty()?
                                    declaredField.getAnnotation(PrimaryKey.class).name():declaredField.getName().toUpperCase());
                createSql.append(primaryKey.append(" ").append(extractClassName(declaredField).toUpperCase()).append(" NOT NULL,"));
                continue;
            }
            if(declaredField.isAnnotationPresent(Column.class)){
                columnName=!declaredField.getAnnotation(Column.class).name().isEmpty()?declaredField.getAnnotation(Column.class).name():declaredField.getName().toUpperCase();
            }
            switch (type){
                case "Integer", "int":
                    tableColumnJoiner.add("\n"+columnName+ " INT");
                    break;
                case "Long","long":
                    tableColumnJoiner.add("\n"+columnName+ " BIGINT");
                    break;
                case "String":
                    tableColumnJoiner.add("\n"+columnName+ " VARCHAR");
                    break;
                case "Double","double":
                    tableColumnJoiner.add("\n"+columnName+ " NUMERIC");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + extractClassName(declaredField));
            }
        }
        createSql.append(tableColumnJoiner).append("\n").append(");");
        System.out.println(createSql);
        Statement statement = connection.createStatement();
        statement.execute(createSql.toString());
    }

    private Method createGetter(Field declaredField, T t) throws NoSuchMethodException {
        String getterName = "get" + declaredField.getName().substring(0,1).toUpperCase() + declaredField.getName().substring(1);
        return t.getClass().getDeclaredMethod(getterName,null);
    }

    private String extractClassName(Field declaredField) {
        int length = declaredField.getGenericType().getTypeName().split("\\.").length;
        return declaredField.getGenericType().getTypeName().split("\\.")[length-1];
    }
}
