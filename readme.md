# Employee Hierarchy Analysis and Optimization

## Overview

This project provides tools to analyze and optimize an organization’s employee hierarchy. It includes functionalities for reading employee data from a CSV file, identifying long reporting lines, and detecting salary discrepancies within the organization.

## Features

- **Read Employee Data**: Read employee data from a CSV file and create a list of `Employee` objects.
- **Build Subordinates Map**: Create a map of employees to their respective subordinates.
- **Report Long Reporting Lines**: Identify employees with excessively long reporting lines.
- **Report Salary Discrepancies**: Detect salary discrepancies where managers earn significantly more or less than their subordinates.

## Classes and Their Responsibilities

### `EmployeeAnalyzer`
Main class to run the application.

- Reads the file path from command-line arguments or properties file.
- Initializes services to process the employee data.
- Generates reports on long reporting lines and salary discrepancies.

### `ReportService`
Handles reading employee data from a CSV file.

#### `List<Employee> readEmployeesFromCsv(String filePath)`
Reads the employee data from a specified CSV file and returns a list of `Employee` objects.

### `OrganisationOptimisationService`
Provides methods to analyze and optimize the organization’s hierarchy.

#### `Map<Integer, List<Employee>> buildSubordinatesMap(List<Employee> employees)`
Creates a map of manager IDs to their respective subordinates.

#### `void reportSalaryDiscrepancies(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap)`
Generates a report of salary discrepancies among employees.

#### `Map<Employee, SalaryDiscrepancy> getEmployeeWithSalaryDiscrepancies(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap)`
Identifies employees with salary discrepancies.

#### `void reportLongReportingLines(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap)`
Generates a report of employees with long reporting lines.

#### `Map<Employee, Integer> getLongReportingLines(List<Employee> employees, Map<Integer, List<Employee>> subordinatesMap)`
Identifies employees with excessively long reporting lines.

#### `int getHierarchyDepth(Employee employee, Map<Integer, List<Employee>> subordinatesMap, Map<Employee, Integer> hierarchyDepthMap)`
Calculates the hierarchy depth of an employee.

### `PropertyHolder`
Loads application properties from the `application.properties` file.

- Provides a static `Properties` object holding the application properties.

## Usage

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. **Clone the repository**:
    ```sh
    git clone https://github.com/hunarepam/OrganisationOptimisation.git
    cd OrganisationOptimisation
    ```

2. **Build the project**:
    ```sh
    mvn clean install
    ```

3. **Run the application**:
    ```sh
    java -jar target/OrganisationOptimisation-1.0-SNAPSHOT.jar --app.report.path=path/to/your/report.csv
    ```

### Configuration

Ensure you have an `application.properties` file in your classpath with the following properties:

```properties
app.hierarchy.depth=4
app.salary.ration.low=1.2
app.salary.ration.high=1.5
app.report.path=/path/to/your/report.csv
