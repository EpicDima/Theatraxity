package com.epicdima.theatraxity.dal.mysql.builders;

import com.epicdima.lib.dal.builders.ExecutableSqlBuilder;

/**
 * @author EpicDima
 */
public final class MySqlDatabaseSqlBuilder extends ExecutableSqlBuilder {
    private static final String CREATE_DATABASE = "CREATE DATABASE";
    private static final String IF_NOT_EXISTS = "IF NOT EXISTS";
    private static final String CHARACTER_SET = "CHARACTER SET";
    private static final String COLLATE = "COLLATE";

    private String databaseName = null;
    private boolean createIfExists = false;
    private String characterSet = "utf8mb4";
    private String collate = "utf8mb4_unicode_ci";

    public MySqlDatabaseSqlBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public MySqlDatabaseSqlBuilder setCreateIfExists(boolean createIfExists) {
        this.createIfExists = createIfExists;
        return this;
    }

    public MySqlDatabaseSqlBuilder setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    public MySqlDatabaseSqlBuilder setCollate(String collate) {
        this.collate = collate;
        return this;
    }

    @Override
    public String createQuery() {
        verify();
        StringBuilder builder = new StringBuilder(CREATE_DATABASE).append(" ");
        if (!createIfExists) {
            builder.append(IF_NOT_EXISTS).append(" ");
        }
        builder.append(databaseName).append(" ");
        builder.append(CHARACTER_SET).append(" ").append(characterSet).append(" ");
        builder.append(COLLATE).append(" ").append(collate).append(" ").append(";");
        return builder.toString();
    }

    private void verify() {
        if (databaseName == null) {
            throw new IllegalStateException("Database Name must be specified");
        }
        if (characterSet == null) {
            throw new IllegalStateException("Character Set must not containt null");
        }
        if (collate == null) {
            throw new IllegalStateException("Collate must not containt null");
        }
    }
}
