package com.momoc.frame.orm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EntityMethodUtil {


    private static final Logger logger = LoggerFactory.getLogger(EntityMethodUtil.class);

    /**
     * 通过无参构造函数获取当前类的实例
     *
     * @param defClass 类
     * @param <R>      实例
     * @return
     */
    public static <R> R createInstance(Class<R> defClass) {
        try {
            Constructor<R> constructor = defClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将获取结果转换为Bean
     *
     * @param resultSet               查询结果
     * @param tableFieldNameSetterMap setter映射
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <R> List<R> queryRsToBean(ResultSet resultSet, Class<R> beanClass, Map<String, List<Method>> tableFieldNameSetterMap) throws SQLException {

        ArrayList<R> defs = new ArrayList<>();

        Statement statement = resultSet.getStatement();
        logger.debug("final statement sql:{}", statement.toString());
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            R instance = createInstance(beanClass);
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);

                Object columnValue = resultSet.getObject(i);
                try {

                    if (columnValue != null) {
//                        logger.info("columnName:{}, columnValue:{}  type:{} ", columnName, columnValue, columnValue.getClass().getName());
                    }
                    // 根据列名设置到resultObject的对应属性

                    List<Method> methods = null;
                    if (tableFieldNameSetterMap != null && !tableFieldNameSetterMap.isEmpty()) {
                        methods = tableFieldNameSetterMap.get(columnName);
                        setterFieldValue(columnValue, instance, methods);
                    }

                } catch (Exception e) {
                    logger.error("转换失败: columnName:{} columnValue:{}", columnName, columnValue, e);
                }

            }
            defs.add(instance);
        }
        return defs;
    }

    /**
     * 重载方法
     * {@link EntityMethodUtil#queryRsToBean}
     */
    public static <R> List<R> queryRsToBean(ResultSet resultSet, Class<R> beanClass) throws SQLException, IllegalAccessException, InvocationTargetException {
        return queryRsToBean(resultSet, beanClass, null);
    }

    /**
     * 将结果映射成map
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> queryRsToMap(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> defs = new ArrayList<>();
        if (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            HashMap<String, Object> dataRow = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                dataRow.put(columnName, columnValue);
            }
            defs.add(dataRow);
        }
        return defs;
    }

    /**
     * 获取字段名称的构造方法
     *
     * @param methodParam 方法参数
     * @param instance    bean对象
     * @param methods     需要调用的setter方法列表
     * @return
     */
    public static void setterFieldValue(Object methodParam, Object instance, List<Method> methods) {
        try {
            if (methodParam == null) {
                return;
            }
//            if (method == null) {
//                String setterName = fieldName;
//                if (!fieldName.contains("set")) {
//                    setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//                }
//                method = constructClass.getMethod(setterName, methodParam.getClass());
//            }
//            Parameter[] parameters = method.getParameters();
            /**
             * @todo 需要完善
             */
//            if (methodParam instanceof Integer){
//
//            } else if (methodParam instanceof Long) {
//
//            } else if (methodParam instanceof Byte) {
//
//            }else if (methodParam instanceof Short) {
//
//            }else if (methodParam instanceof Double) {
//
//            }else if (methodParam instanceof Float) {
//
//            }else if (methodParam instanceof Boolean) {
//
//            }else if (methodParam instanceof Character) {
//
//            }else if (methodParam instanceof String) {
//
//            }


            for (Method method : methods) {
                Parameter[] parameters = method.getParameters();
                if (parameters[0].getType().equals(String.class)) {
                    method.invoke(instance, methodParam.toString());
                } else {
                    method.invoke(instance, methodParam);
                }
            }

        } catch (Exception e) {
            String methodNames = methods.stream().map(k -> k.getName()).collect(Collectors.joining(","));
            //setter失败不处理
            logger.warn("setterFieldValue error, method:{}  param:{} paramType:{}", methodNames, methodParam, methodParam.getClass().getName());
        }
    }


    /**
     * 拼接列表，并给字符串加上'A','A'
     *
     * @param list
     * @param separator 分隔符
     * @return
     */
    public static String join(Collection<?> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (Object source : list) {
            if (source instanceof String) {
                stringBuilder.append("'").append(source).append("'").append(separator);
            } else {
                stringBuilder.append(source.toString()).append(separator);
            }
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1);

    }

    public static String join(Object[] arr, String separator) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object source : arr) {
            if (source instanceof String) {
                stringBuilder.append("'").append(source).append("'").append(separator);
            } else {
                stringBuilder.append(source.toString()).append(separator);
            }
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


}
