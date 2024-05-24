package com.epam;

import com.epam.config.PropertyHolder;
import com.epam.model.Employee;
import com.epam.service.OrganisationOptimisationService;
import com.epam.service.ReportService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The EmployeeAnalyzer class is the entry point of the application that analyzes employee data.
 * It reads employee data from a CSV file, builds a subordinates map, and generates reports on
 * long reporting lines and salary discrepancies.
 */
public class EmployeeAnalyzer {

    /**
     * The main method that starts the application.
     *
     * @param args Command-line arguments. It should include the path to the employee report CSV file in the format
     *             --app.report.path=<path_to_csv_file>.
     */
    public static void main(String[] args) {
        ReportService reportService = new ReportService();
        OrganisationOptimisationService organisationOptimisationService = new OrganisationOptimisationService();

        String filePath = getFilePath(args);

        List<Employee> employees = reportService.readEmployeesFromCsv(filePath);
        Map<Integer, List<Employee>> subordinates = organisationOptimisationService.buildSubordinatesMap(employees);

        organisationOptimisationService.reportLongReportingLines(employees, subordinates);
        organisationOptimisationService.reportSalaryDiscrepancies(employees, subordinates);
    }
    /**
     * Retrieves the file path to the employee report CSV file from the command-line arguments or properties.
     *
     * @param args Command-line arguments passed to the application.
     * @return The file path to the employee report CSV file. If the path is not specified, returns null.
     */
    private static String getFilePath(String[] args) {
        String filePathPropertyName = "app.report.path";
        Map<String, String> arguments = parseArguments(args);
        String filePath = arguments.get(filePathPropertyName);
        if (filePath == null) {
            filePath = PropertyHolder.properties.getProperty(filePathPropertyName);
        }
        if (filePath == null) {
            System.err.println("Path to report is not specified");
        }
        return filePath;
    }

    /**
     * Parses the command-line arguments into a map of argument names and values.
     *
     * @param args Command-line arguments passed to the application.
     * @return A map of argument names and values.
     */
    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    arguments.put(parts[0], parts[1]);
                }
            }
        }
        return arguments;
    }
}
