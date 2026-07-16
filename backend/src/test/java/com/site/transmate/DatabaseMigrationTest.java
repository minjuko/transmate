package com.site.transmate;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class DatabaseMigrationTest {

    @Test
    void createsCurrentSchemaForAnEmptyDatabase() throws SQLException {
        String url = "jdbc:h2:mem:migration-new;DB_CLOSE_DELAY=-1";

        migrate(url, false);

        assertThat(columns(url, "ACCOUNT"))
                .contains("ACCOUNTID", "NAME")
                .doesNotContain("ID", "PASSWORD");
        assertThat(columns(url, "MEETING")).contains("DATE", "ACCOUNT_ACCOUNTID");
        assertThat(columns(url, "SCHEDULE")).contains("DATE", "TIME", "ACCOUNT_ACCOUNTID");
    }

    @Test
    void removesLegacyAccountColumnsFromAnExistingUnversionedDatabase() throws SQLException {
        String url = "jdbc:h2:mem:migration-existing;DB_CLOSE_DELAY=-1";
        try (Connection connection = DriverManager.getConnection(url, "sa", "")) {
            connection.createStatement().execute("""
                    CREATE TABLE account (
                        accountid VARCHAR(255) NOT NULL PRIMARY KEY,
                        id INTEGER,
                        name VARCHAR(20),
                        password VARCHAR(20)
                    )
                    """);
        }

        migrate(url, true);

        assertThat(columns(url, "ACCOUNT")).doesNotContain("ID", "PASSWORD");
    }

    private void migrate(String url, boolean baselineOnMigrate) {
        Flyway.configure()
                .dataSource(url, "sa", "")
                .locations("classpath:db/migration")
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion("1")
                .load()
                .migrate();
    }

    private Set<String> columns(String url, String table) throws SQLException {
        Set<String> columns = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, "sa", "");
                ResultSet resultSet = connection.getMetaData()
                        .getColumns(null, null, table, null)) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME"));
            }
        }
        return columns;
    }
}
