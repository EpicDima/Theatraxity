package com.epicdima.theatraxity.dal.mysql;

import com.epicdima.lib.dal.annotations.Database;
import com.epicdima.lib.dal.annotations.handlers.ColumnAnnotationHandler;
import com.epicdima.lib.dal.annotations.handlers.DatabaseAnnotationHandler;
import com.epicdima.lib.dal.annotations.handlers.ForeignKeyAnnotationHandler;
import com.epicdima.lib.dal.annotations.handlers.TableAnnotationHandler;
import com.epicdima.lib.dal.other.DatabaseInitializer;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.dal.models.business.OrderData;
import com.epicdima.theatraxity.dal.models.business.TicketData;
import com.epicdima.theatraxity.dal.models.theatre.AuthorData;
import com.epicdima.theatraxity.dal.models.theatre.GenreData;
import com.epicdima.theatraxity.dal.models.theatre.PlayData;
import com.epicdima.theatraxity.dal.models.theatre.PresentationData;
import com.epicdima.theatraxity.dal.models.user.UserData;
import com.epicdima.theatraxity.dal.mysql.builders.MySqlDatabaseSqlBuilder;
import com.epicdima.theatraxity.dal.mysql.builders.MySqlForeignKeySqlBuilder;
import com.epicdima.theatraxity.dal.mysql.builders.MySqlTableSqlBuilder;
import com.epicdima.theatraxity.dal.mysql.builders.configurators.MySqlNameColumnConfigurator;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author EpicDima
 */
@Singleton
@Database(entities = {
        UserData.class,
        AuthorData.class,
        GenreData.class,
        PlayData.class,
        PresentationData.class,
        OrderData.class,
        TicketData.class
})
public final class MySqlDatabaseInitializer implements DatabaseInitializer {

    public MySqlDatabaseInitializer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createDatabase(Connection connection, String databaseName) {
        try (connection) {
            new MySqlDatabaseSqlBuilder()
                    .setDatabaseName(databaseName)
                    .build()
                    .execute(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createTables(Connection connection) {
        try (connection) {
            DatabaseAnnotationHandler handler = new DatabaseAnnotationHandler();
            if (!handler.handle(getClass())) {
                return;
            }
            Class<?>[] entities = handler.getEntities();
            for (Class<?> entity : entities) {
                createTable(connection, entity);
            }
            for (Class<?> entity : entities) {
                addForeignKeys(connection, entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTable(Connection connection, Class<?> entityClass) {
        TableAnnotationHandler handler = new TableAnnotationHandler();
        handler.handle(entityClass);
        MySqlTableSqlBuilder builder = new MySqlTableSqlBuilder()
                .setTableName(handler.getName())
                .setCreateIfExists(false);
        for (Field field : entityClass.getDeclaredFields()) {
            if (new ColumnAnnotationHandler().handle(field)) {
                builder.addColumn(field);
            }
        }
        builder.build().execute(connection);
    }

    private static void addForeignKeys(Connection connection, Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            ForeignKeyAnnotationHandler foreignKeyHandler = new ForeignKeyAnnotationHandler();
            if (foreignKeyHandler.handle(field)) {
                TableAnnotationHandler childTableHandler = new TableAnnotationHandler();
                childTableHandler.handle(field.getDeclaringClass());
                TableAnnotationHandler parentTableHandler = new TableAnnotationHandler();
                parentTableHandler.handle(foreignKeyHandler.getEntity());
                try {
                    new MySqlForeignKeySqlBuilder()
                            .setChildTableName(childTableHandler.getName())
                            .setChildColumnName(new MySqlNameColumnConfigurator().configure(field, ""))
                            .setParentTableName(parentTableHandler.getName())
                            .setParentColumnName(foreignKeyHandler.getParentColumn())
                            .build()
                            .execute(connection);
                } catch (Exception e) {
                    if (!e.getCause().getMessage().contains("Duplicate foreign key constraint name")) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}

