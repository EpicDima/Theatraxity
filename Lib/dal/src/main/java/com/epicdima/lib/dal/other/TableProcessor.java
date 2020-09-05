package com.epicdima.lib.dal.other;

import com.epicdima.lib.dal.annotations.handlers.ColumnAnnotationHandler;
import com.epicdima.lib.dal.annotations.handlers.PrimaryKeyAnnotationHandler;
import com.epicdima.lib.dal.annotations.handlers.TableAnnotationHandler;
import com.epicdima.lib.dal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author EpicDima
 */
public abstract class TableProcessor<T> {
    protected final Class<T> entityClass = ReflectionUtils.getFirstGenericType(getClass());
    protected final ConnectionPool connectionPool;

    protected String tableName;
    protected String primaryColumn;
    protected final Map<String, Field> fields = new LinkedHashMap<>();

    public TableProcessor(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        initialProcessing();
    }

    private void initialProcessing() {
        TableAnnotationHandler tableHandler = new TableAnnotationHandler();
        tableHandler.handle(entityClass);
        tableName = tableHandler.getName();
        for (Field field : entityClass.getDeclaredFields()) {
            ColumnAnnotationHandler columnHandler = new ColumnAnnotationHandler();
            if (columnHandler.handle(field)) {
                String name = columnHandler.getName();
                if (new PrimaryKeyAnnotationHandler().handle(field)) {
                    primaryColumn = name;
                }
                fields.put(name, field);
            }
        }
    }

    protected <K> void prepareToSelectWhere(K key, PreparedStatement statement) throws SQLException {
        statement.setObject(1, key);
    }

    protected <K1, K2> void prepareToSelectWhere(K1 firstKey, K2 secondKey, PreparedStatement statement) throws SQLException {
        statement.setObject(1, firstKey);
        statement.setObject(2, secondKey);
    }

    protected void prepareToInsert(T obj, PreparedStatement statement) throws SQLException {
        int counter = 1;
        for (Map.Entry<String, Field> stringFieldEntry : fields.entrySet()) {
            String columnName = stringFieldEntry.getKey();
            Field field = stringFieldEntry.getValue();
            if (!columnName.equals(primaryColumn)) {
                Object value = getValueByColumnName(obj, columnName);
                if (ReflectionUtils.fieldIsEnum(field)) {
                    value = value.toString();
                }
                statement.setObject(counter, value);
                counter++;
            }
        }
    }

    protected void prepareToUpdate(T obj, PreparedStatement statement) throws SQLException {
        prepareToInsert(obj, statement);
    }

    protected void prepareToDelete(T obj, PreparedStatement statement) throws SQLException {
        statement.setObject(1, getValueByColumnName(obj, primaryColumn));
    }

    protected List<T> toList(ResultSet resultSet) throws SQLException {
        List<T> items = new ArrayList<>();
        while (resultSet.next()) {
            items.add(toObject(resultSet));
        }
        return items;
    }

    protected T toSingleObject(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return toObject(resultSet);
        }
        return null;
    }

    protected Object getValueByColumnName(T obj, String columnName) {
        return ReflectionUtils.getValue(obj, fields.get(columnName));
    }

    protected void setValueByColumnName(T obj, String columnName, Object value) {
        ReflectionUtils.setValue(obj, fields.get(columnName), value);
    }

    protected T toObject(ResultSet resultSet) throws SQLException {
        T obj = ReflectionUtils.createInstanceOfEntityClass(entityClass);
        for (Map.Entry<String, Field> stringFieldEntry : fields.entrySet()) {
            String columnName = stringFieldEntry.getKey();
            Field field = stringFieldEntry.getValue();
            Object value;
            if (ReflectionUtils.fieldIsEnum(field)) {
                value = ReflectionUtils.getEnumConstant(field.getType(), resultSet.getString(columnName));
            } else {
                value = resultSet.getObject(columnName);
            }
            setValueByColumnName(obj, columnName, value);
        }
        return obj;
    }
}
