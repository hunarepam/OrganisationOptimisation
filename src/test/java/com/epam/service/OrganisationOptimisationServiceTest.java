package com.epam.service;

import com.epam.config.PropertyHolder;
import com.epam.model.DiscrepancyType;
import com.epam.model.Employee;
import com.epam.model.SalaryDiscrepancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class OrganisationOptimisationServiceTest {

    private OrganisationOptimisationService service;
    private List<Employee> employees;
    private Map<Integer, List<Employee>> subordinatesMap;

    @BeforeEach
    public void setUp() {
        PropertyHolder.properties.setProperty("app.hierarchy.depth", "4");
        PropertyHolder.properties.setProperty("app.salary.ration.low", "1.2");
        PropertyHolder.properties.setProperty("app.salary.ration.high", "1.5");

        service = new OrganisationOptimisationService();

        employees = List.of(
                new Employee(123, "Joe", "Doe", new BigDecimal("70000"), null),
                new Employee(124, "Martin", "Chekov", new BigDecimal("45000"), 123),
                new Employee(125, "Bob", "Ronstad", new BigDecimal("47000"), 123),
                new Employee(300, "Alice", "Hasacat", new BigDecimal("50000"), 124),
                new Employee(305, "Brett", "Hardleaf", new BigDecimal("34000"), 300)
        );

        subordinatesMap = service.buildSubordinatesMap(employees);
    }

    @Test
    public void testBuildSubordinatesMap() {
        Map<Integer, List<Employee>> result = service.buildSubordinatesMap(employees);
        assertEquals(3, result.size());
        assertTrue(result.containsKey(123));
        assertTrue(result.containsKey(124));
        assertTrue(result.containsKey(300));
    }

    @Test
    public void getEmployeeWithSalaryDiscrepancies() {
        Map<Employee, SalaryDiscrepancy> result = service.getEmployeeWithSalaryDiscrepancies(employees, subordinatesMap);

        // Print intermediate values for debugging
        System.out.println("Discrepancy Results: " + result);

        assertEquals(2, result.size());

        Employee martin = employees.stream().filter(e -> e.id() == 124).findFirst().orElse(null);
        Employee joe = employees.stream().filter(e -> e.id() == 123).findFirst().orElse(null);

        assertNotNull(martin);
        assertNotNull(joe);

        // Debug output to verify values
        System.out.println("Martin's discrepancy: " + result.get(martin));
        System.out.println("Joe's discrepancy: " + result.get(joe));

        assertEquals(DiscrepancyType.LESS, result.get(martin).type());
        assertEquals(new BigDecimal("15000.0"), result.get(martin).discrepancy());

        assertEquals(DiscrepancyType.MORE, result.get(joe).type());
        assertEquals(new BigDecimal("1000.0"), result.get(joe).discrepancy());
    }

    @Test
    public void testReportSalaryDiscrepancies() {
        // Mock System.out
        var out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.reportSalaryDiscrepancies(employees, subordinatesMap);

        // Validate the output
        String expectedOutput = String.format(
                "Manager %s earns more than they should by %s%nManager %s earns less than they should by %s%n",
                employees.get(0), "1000.0", employees.get(1), "15000.0"
        );
        assertEquals(expectedOutput, out.toString());

        // Restore System.out
        System.setOut(System.out);
    }

    @Test
    public void testGetLongReportingLines() {
        Map<Employee, Integer> result = service.getLongReportingLines(employees, subordinatesMap);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testReportLongReportingLines() {
        // Mock System.out
        var out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        service.reportLongReportingLines(employees, subordinatesMap);

        // Validate the output
        assertEquals("", out.toString());

        // Restore System.out
        System.setOut(System.out);
    }

    @ParameterizedTest
    @MethodSource("provideEmployeesAndExpectedDepths")
    public void getHierarchyDepth(Employee employee, int expectedDepth) {
        // Arrange
        Map<Employee, Integer> hierarchyDepthMap = new HashMap<>();

        // Act
        int actualDepth = service.getHierarchyDepth(employee, subordinatesMap, hierarchyDepthMap);

        // Assert
        assertEquals(expectedDepth, actualDepth);
    }

    private static Stream<Arguments> provideEmployeesAndExpectedDepths() {
        // Creating different sets of employees and their expected depths
        return Stream.of(
                Arguments.of(new Employee(123, "Joe", "Doe", new BigDecimal("60000"), null), 0),
                Arguments.of(null, 0),
                Arguments.of(new Employee(124, "Martin", "Chekov", new BigDecimal("45000"), 123), 1),
                Arguments.of(new Employee(125, "Bob", "Ronstad", new BigDecimal("47000"), 124), 2)
        );
    }
}
