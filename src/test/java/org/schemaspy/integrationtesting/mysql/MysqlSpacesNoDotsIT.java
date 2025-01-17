/*
 * Copyright (C) 2018 Nils Petzaell
 *
 * This file is part of SchemaSpy.
 *
 * SchemaSpy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SchemaSpy. If not, see <http://www.gnu.org/licenses/>.
 */
package org.schemaspy.integrationtesting.mysql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.schemaspy.integrationtesting.MysqlSuite;
import org.schemaspy.model.*;
import org.schemaspy.testing.testcontainers.SuiteContainerExtension;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.schemaspy.testing.DatabaseFixture.database;

/**
 * @author Nils Petzaell
 */
class MysqlSpacesNoDotsIT {

    private static final Path outputPath = Paths.get("target","testout","integrationtesting","mysql","spaces_no_dots");

    private static Database database;

    @RegisterExtension
    static SuiteContainerExtension container = MysqlSuite.SUITE_CONTAINER;

    @BeforeAll
    static void createDatabaseRepresentation() throws SQLException, IOException {
        String[] args = {
                "-t", "mysql",
                "-db", "TEST 1",
                "-s", "TEST 1",
                "-cat", "%",
                "-u", "test",
                "-p", "test",
                "-host", container.getHost(),
                "-port", container.getPort(3306),
                "-o", outputPath.toString(),
                "-connprops", "useSSL\\=false;allowPublicKeyRetrieval\\=true"
        };
        database = database(args);
    }

    @Test
    void databaseShouldExist() {
        assertThat(database).isNotNull();
        assertThat(database.getName()).isEqualToIgnoringCase("TEST 1");
    }

    @Test
    void databaseShouldHaveTable() {
        assertThat(database.getTables()).extracting(Table::getName).contains("TABLE 1");
    }

    @Test
    void tableShouldHavePKWithAutoIncrement() {
        assertThat(database.getTablesMap().get("TABLE 1").getColumns()).extracting(TableColumn::getName).contains("id");
        assertThat(database.getTablesMap().get("TABLE 1").getColumn("id").isPrimary()).isTrue();
        assertThat(database.getTablesMap().get("TABLE 1").getColumn("id").isAutoUpdated()).isTrue();
    }

    @Test
    void tableShouldHaveForeignKey() {
        assertThat(database.getTablesMap().get("TABLE 1").getForeignKeys()).extracting(ForeignKeyConstraint::getName).contains("link fk");
    }

    @Test
    void tableShouldHaveUniqueKey() {
        assertThat(database.getTablesMap().get("TABLE 1").getIndexes()).extracting(TableIndex::getName).contains("name_link_unique");
    }

    @Test
    void tableShouldHaveColumnWithSpaceInIt() {
        assertThat(database.getTablesMap().get("TABLE 1").getColumns()).extracting(TableColumn::getName).contains("link id");
    }
}
