package com.epicdima.theatraxity.dal.mysql.builders;

import com.epicdima.lib.dal.builders.ExecutableSqlBuilder;

/**
 * @author EpicDima
 */
public final class MySqlForeignKeySqlBuilder extends ExecutableSqlBuilder {
    private static final String ALTER_TABLE = "ALTER TABLE";
    private static final String ADD_CONSTRAINT = "ADD CONSTRAINT";
    private static final String FORMAT_NAME = "%s_%s_TO_%s_FK";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String REFERENCES = "REFERENCES";
    private static final String ON_DELETE_CASCADE = "ON DELETE CASCADE";

    private String childTableName = null;
    private String parentTableName = null;
    private String childColumnName = null;
    private String parentColumnName = null;
    private boolean onDeleteCascade = true;

    public MySqlForeignKeySqlBuilder setChildTableName(String childTableName) {
        this.childTableName = childTableName;
        return this;
    }

    public MySqlForeignKeySqlBuilder setParentTableName(String parentTableName) {
        this.parentTableName = parentTableName;
        return this;
    }

    public MySqlForeignKeySqlBuilder setChildColumnName(String childColumnName) {
        this.childColumnName = childColumnName;
        return this;
    }

    public MySqlForeignKeySqlBuilder setParentColumnName(String parentColumnName) {
        this.parentColumnName = parentColumnName;
        return this;
    }

    public MySqlForeignKeySqlBuilder setOnDeleteCascade(boolean onDeleteCascade) {
        this.onDeleteCascade = onDeleteCascade;
        return this;
    }

    @Override
    public String createQuery() {
        verify();
        StringBuilder builder = new StringBuilder(ALTER_TABLE).append(" ");
        builder.append(childTableName).append(" ");
        builder.append(ADD_CONSTRAINT).append(" ");
        builder.append(String.format(FORMAT_NAME, childTableName,
                childColumnName.substring(1, childColumnName.length() - 1), parentTableName)).append(" ");
        builder.append(FOREIGN_KEY).append(" ");
        builder.append("(").append(childColumnName).append(")").append(" ");
        builder.append(REFERENCES).append(" ");
        builder.append(parentTableName).append(" ");
        builder.append("(").append(parentColumnName).append(")");
        if (onDeleteCascade) {
            builder.append(" ").append(ON_DELETE_CASCADE);
        }
        builder.append(";");
        return builder.toString();
    }

    private void verify() {
        if (childTableName == null) {
            throw new IllegalStateException("Child Table Name must be specified");
        }
        if (parentTableName == null) {
            throw new IllegalStateException("Parent Table Name must be specified");
        }
        if (childColumnName == null) {
            throw new IllegalStateException("Child Column Name must be specified");
        }
        if (parentColumnName == null) {
            throw new IllegalStateException("Parent Column Name must be specified");
        }
    }
}