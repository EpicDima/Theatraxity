package com.epicdima.lib.dal.dao;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.dal.other.TableProcessor;
import com.epicdima.lib.dal.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EpicDima
 */
public abstract class BaseDaoImpl<D, T> extends TableProcessor<D> implements BaseDao<T> {

    public BaseDaoImpl(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public synchronized List<T> select() {
        return doSelect(createSelectSqlQuery(), null, this::executeAndConvertToList);
    }

    @Override
    public synchronized <K> T select(K key) {
        return doSelect(createSelectByColumnSqlQuery(primaryColumn),
                (statement) -> prepareToSelectWhere(key, statement),
                this::executeAndConvertToSingleObject);
    }

    @Override
    public synchronized T insert(T item) {
        D dataItem = fromModel(item);
        return JdbcUtils.execute(connectionPool, connection -> {
            try (PreparedStatement statement = JdbcUtils.prepareStatement(connection, createInsertSqlQuery(),
                    Statement.RETURN_GENERATED_KEYS)) {
                prepareToInsert(dataItem, statement);
                if (statement.executeUpdate() > 0) {
                    updateObjectAfterInsert(dataItem, statement);
                    return toModel(dataItem);
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public synchronized Boolean update(T item) {
        D dataItem = fromModel(item);
        return JdbcUtils.execute(connectionPool, connection -> {
            try (PreparedStatement statement = JdbcUtils.prepareStatement(connection, createUpdateSqlQuery())) {
                prepareToUpdate(dataItem, statement);
                statement.setObject(fields.size(), getValueByColumnName(dataItem, primaryColumn));
                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public synchronized Boolean delete(T item) {
        return JdbcUtils.execute(connectionPool, connection -> {
            try (PreparedStatement statement = JdbcUtils.prepareStatement(connection, createDeleteSqlQuery())) {
                prepareToDelete(fromModel(item), statement);
                return statement.executeUpdate() > 0;
            }
        });
    }

    protected synchronized <K> List<T> selectWhere(String columnName, K key) {
        return doSelect(createSelectByColumnSqlQuery(columnName),
                (statement) -> prepareToSelectWhere(key, statement),
                this::executeAndConvertToList);
    }

    protected synchronized <K1, K2> List<T> selectWhere(String firstColumnName, String secondColumnName, K1 firstKey, K2 secondKey) {
        return doSelect(createSelectByTwoColumnSqlQuery(firstColumnName, secondColumnName),
                (statement) -> prepareToSelectWhere(firstKey, secondKey, statement),
                this::executeAndConvertToList);
    }

    protected List<T> executeAndConvertToList(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            return toList(resultSet).stream().map(this::toModel).collect(Collectors.toList());
        }
    }

    protected synchronized <K> T selectSingleWhere(String columnName, K key) {
        return doSelect(createSelectByColumnSqlQuery(columnName),
                (statement) -> prepareToSelectWhere(key, statement),
                this::executeAndConvertToSingleObject);
    }

    protected synchronized <K1, K2> T selectSingleWhere(String firstColumnName, String secondColumnName,
                                           K1 firstKey, K2 secondKey) {
        return doSelect(createSelectByTwoColumnSqlQuery(firstColumnName, secondColumnName),
                (statement) -> prepareToSelectWhere(firstKey, secondKey, statement),
                this::executeAndConvertToSingleObject);
    }

    protected T executeAndConvertToSingleObject(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            return toModel(toSingleObject(resultSet));
        }
    }

    protected <R> R doSelect(String sqlQuery, OnPreparedStatementVoid prep, OnPreparedStatement<R> executor) {
        return JdbcUtils.execute(connectionPool, connection -> {
            try (PreparedStatement statement = JdbcUtils.prepareStatement(connection, sqlQuery)) {
                if (prep != null) {
                    prep.run(statement);
                }
                return executor.run(statement);
            }
        });
    }

    private void updateObjectAfterInsert(D dataItem, PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                if (int.class.equals(fields.get(primaryColumn).getType())) {
                    setValueByColumnName(dataItem, primaryColumn, generatedKeys.getInt(1));
                } else {
                    setValueByColumnName(dataItem, primaryColumn, generatedKeys.getObject(1));
                }
            }
        }
    }

    protected abstract String createSelectSqlQuery();

    protected abstract String createSelectByColumnSqlQuery(String columnName);

    protected abstract String createSelectByTwoColumnSqlQuery(String firstColumnName, String secondColumnName);

    protected abstract String createInsertSqlQuery();

    protected abstract String createUpdateSqlQuery();

    protected abstract String createDeleteSqlQuery();

    protected abstract T toModel(D item);

    protected abstract D fromModel(T item);


    @FunctionalInterface
    protected interface OnPreparedStatementVoid {
        void run(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    protected interface OnPreparedStatement<T> {
        T run(PreparedStatement statement) throws SQLException;
    }
}
