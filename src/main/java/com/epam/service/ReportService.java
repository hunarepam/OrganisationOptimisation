package com.epam.service;

import com.epam.model.Employee;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * The {@code ReportService} class provides a method to read employee data from a CSV file.
 * Each line in the CSV file (except the header) is converted into an {@code Employee} object.
 */
public class ReportService {
    /**
     * Reads employee data from a CSV file and returns a list of {@code Employee} objects.
     * The CSV file is expected to have the following columns: id, firstName, lastName, salary, managerId.
     * The header line is skipped.
     *
     * @param filePath the path to the CSV file
     * @return a list of {@code Employee} objects read from the CSV file,
     *         or an empty list if an {@code IOException} occurs
     */
    public List<Employee> readEmployeesFromCsv(String filePath) {
        try {
            return Files.lines(Path.of(filePath))
                    .skip(1) // skip the header line
                    .map(line -> {
                        String[] values = line.split(",");
                        int id = Integer.parseInt(values[0]);
                        String firstName = values[1];
                        String lastName = values[2];
                        BigDecimal salary = new BigDecimal(values[3]);
                        Integer managerId = values.length < 5 ? null : Integer.parseInt(values[4]);
                        return new Employee(id, firstName, lastName, salary, managerId);
                    })
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
