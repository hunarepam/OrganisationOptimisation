package com.epam.service;

import com.epam.config.PropertyHolder;
import com.epam.model.DiscrepancyType;
import com.epam.model.Employee;
import com.epam.model.SalaryDiscrepancy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for optimizing organizational structure by analyzing reporting lines and salary discrepancies.
 */
public class OrganisationOptimisationService {
    private final Integer hierarchyDepthThreshold;
    private final Double lowSalaryRation;
    private final Double highSalaryRation;

    /**
     * Constructs an OrganisationOptimisationService with configuration properties.
     * Reads hierarchy depth threshold and salary ratios from PropertyHolder.
     */
    public OrganisationOptimisationService() {
        hierarchyDepthThreshold = Integer.valueOf(PropertyHolder.properties.getProperty("app.hierarchy.depth"));
        lowSalaryRation = Double.valueOf(PropertyHolder.properties.getProperty("app.salary.ration.low"));
        highSalaryRation = Double.valueOf(PropertyHolder.properties.getProperty("app.salary.ration.high"));
    }

    /**
     * Builds a map of subordinates for each manager.
     *
     * @param employees List of employees.
     * @return A map where the key is the manager's ID and the value is a list of their subordinates.
     */
    public Map<Integer, List<Employee>> buildSubordinatesMap(List<Employee> employees) {
        return employees.stream()
                .filter(emp -> emp.managerId() != null)
                .collect(Collectors.groupingBy(Employee::managerId));
    }

    /**
     * Reports salary discrepancies for managers based on the salaries of their subordinates.
     *
     * @param employees List of employees.
     * @param subordinatesMap Map of subordinates for each manager.
     */
    public void reportSalaryDiscrepancies(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap) {
        Map<Employee, SalaryDiscrepancy> employeeWithSalaryDiscrepancies = getEmployeeWithSalaryDiscrepancies(employees, subordinatesMap);
        employeeWithSalaryDiscrepancies.forEach((employee, salaryDiscrepancy) -> {
            if (DiscrepancyType.LESS.equals(salaryDiscrepancy.type())) {
                System.out.printf("Manager %s earns less than they should by %s%n", employee, salaryDiscrepancy.discrepancy());
            } else if (DiscrepancyType.MORE.equals(salaryDiscrepancy.type())) {
                System.out.printf("Manager %s earns more than they should by %s%n", employee, salaryDiscrepancy.discrepancy());
            }
        });
    }

    /**
     * Identifies employees with salary discrepancies based on the salaries of their subordinates.
     *
     * @param employees List of employees.
     * @param subordinatesMap Map of subordinates for each manager.
     * @return A map where the key is the employee and the value is their salary discrepancy.
     */
    public Map<Employee, SalaryDiscrepancy> getEmployeeWithSalaryDiscrepancies(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap) {
        Map<Employee, SalaryDiscrepancy> employeeWithSalaryDiscrepancy = new HashMap<>();

        for (Employee manager : employees) {
            List<Employee> subordinates = subordinatesMap.get(manager.id());
            if (subordinates != null && !subordinates.isEmpty()) {
                BigDecimal averageSalary = subordinates.stream()
                        .map(Employee::salary)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(subordinates.size()), RoundingMode.HALF_UP);

                BigDecimal lowerBound = averageSalary.multiply(BigDecimal.valueOf(lowSalaryRation));
                BigDecimal upperBound = averageSalary.multiply(BigDecimal.valueOf(highSalaryRation));

                if (manager.salary().compareTo(lowerBound) < 0) {
                    employeeWithSalaryDiscrepancy.put(manager, new SalaryDiscrepancy(DiscrepancyType.LESS, lowerBound.subtract(manager.salary())));
                } else if (manager.salary().compareTo(upperBound) > 0) {
                    employeeWithSalaryDiscrepancy.put(manager, new SalaryDiscrepancy(DiscrepancyType.MORE, manager.salary().subtract(upperBound)));
                }
            }
        }
        return employeeWithSalaryDiscrepancy;
    }

    /**
     * Reports employees with long reporting lines exceeding the defined threshold.
     *
     * @param employees List of employees.
     * @param subordinatesMap Map of subordinates for each manager.
     */
    public void reportLongReportingLines(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap) {
        Map<Employee, Integer> hierarchyDepthMap = getLongReportingLines(employees, subordinatesMap);
        hierarchyDepthMap.forEach((employee, hierarchyDepthDifference) ->
                System.out.printf("Employee %s has a reporting line that is too long by %d%n", employee, hierarchyDepthDifference));
    }

    /**
     * Identifies employees with long reporting lines exceeding the defined threshold.
     *
     * @param employees List of employees.
     * @param subordinatesMap Map of subordinates for each manager.
     * @return A map where the key is the employee and the value is the difference between their reporting line length and the threshold.
     */
    public Map<Employee, Integer> getLongReportingLines(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap) {
        Map<Employee, Integer> hierarchyDepthMap = new HashMap<>();
        Map<Employee, Integer> result = new HashMap<>();

        for (Employee employee : employees) {
            int depth = getHierarchyDepth(employee, subordinatesMap, hierarchyDepthMap);
            if (depth > hierarchyDepthThreshold) {
                result.put(employee, depth - hierarchyDepthThreshold);
            }
        }

        return result;
    }

    /**
     * Recursively calculates the hierarchy depth for a given employee.
     *
     * @param employee The employee for whom to calculate the hierarchy depth.
     * @param subordinatesMap Map of subordinates for each manager.
     * @param hierarchyDepthMap Map to cache hierarchy depths.
     * @return The hierarchy depth for the given employee.
     */
    int getHierarchyDepth(Employee employee, Map<Integer, List<Employee>> subordinatesMap,
                                 Map<Employee, Integer> hierarchyDepthMap) {
        if (employee == null || employee.managerId() == null) {
            return 0;
        }

        // Check if the employee is already present in the hierarchyDepthMap
        if (hierarchyDepthMap.containsKey(employee)) {
            return hierarchyDepthMap.get(employee);
        }

        int depth = 1; // Start with depth 1 for the employee
        Integer managerId = employee.managerId();

        // Find the manager of the current employee
        for (Map.Entry<Integer, List<Employee>> entry : subordinatesMap.entrySet()) {
            for (Employee emp : entry.getValue()) {
                if (emp.id().equals(managerId)) {
                    depth += getHierarchyDepth(emp, subordinatesMap, hierarchyDepthMap);
                    break;
                }
            }
        }
        // Cache the depth in the hierarchyDepthMap
        hierarchyDepthMap.put(employee, depth);

        return depth;
    }
}
