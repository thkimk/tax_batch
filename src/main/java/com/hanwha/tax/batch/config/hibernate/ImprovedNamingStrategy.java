package com.hanwha.tax.batch.config.hibernate;

import com.hanwha.tax.batch.Utils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class ImprovedNamingStrategy implements PhysicalNamingStrategy {
    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convert(name);
    }

    private Identifier convert(Identifier name) {
        if (name == null || Utils.isEmpty(name.getText())) {
            return name;
        }

        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        String newName = name.getText().replaceAll(regex, replacement).toLowerCase();
        return Identifier.toIdentifier(newName);
    }
}