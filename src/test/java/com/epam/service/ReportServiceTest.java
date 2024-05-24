package com.epam.service;

import com.epam.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
    }

    @Test
    void readEmployeesFromCsv_successful() throws URISyntaxException {
        String filePath = Paths.get(Objects.requireNonNull(ReportServiceTest.class.getClassLoader().getResource("report.csv")).toURI()).toString();

        List<Employee> employees = reportService.readEmployeesFromCsv(filePath);

        assertEquals(2, employees.size());

        assertEquals(123, employees.get(0).id());
        assertEquals("Joe", employees.get(0).firstName());
        assertEquals("Doe", employees.get(0).lastName());
        assertEquals(new BigDecimal("60000"), employees.get(0).salary());
        assertNull(employees.get(0).managerId());

        assertEquals(124, employees.get(1).id());
        assertEquals("Martsin", employees.get(1).firstName());
        assertEquals("Adamovich", employees.get(1).lastName());
        assertEquals(new BigDecimal("45000"), employees.get(1).salary());
        assertEquals(123, employees.get(1).managerId());

    }

    @Test
    void readEmployeesFromCsv_noData(@TempDir Path tempDir) throws IOException {
        String content = "id,firstName,lastName,salary,managerId\n";
        Path filePath = tempDir.resolve("employees.csv");
        Files.writeString(filePath, content);

        List<Employee> employees = reportService.readEmployeesFromCsv(filePath.toString());

        assertTrue(employees.isEmpty());
    }

    @Test
    void readEmployeesFromCsv_ioException() {
        List<Employee> employees = reportService.readEmployeesFromCsv("nonexistentfile.csv");

        assertTrue(employees.isEmpty());
    }
}
