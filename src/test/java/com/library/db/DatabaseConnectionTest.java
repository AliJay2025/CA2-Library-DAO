package com.library.db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseConnectionTest {

    @Test
    public void testConnection() {
        assertTrue(DatabaseConnection.testConnection(),
                "Database connection should work");
    }
}