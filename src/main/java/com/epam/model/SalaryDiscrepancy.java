package com.epam.model;

import java.math.BigDecimal;

public record SalaryDiscrepancy(DiscrepancyType type, BigDecimal discrepancy) {
}
