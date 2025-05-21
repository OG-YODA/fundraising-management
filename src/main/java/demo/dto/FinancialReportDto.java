package demo.dto;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class FinancialReportDto {
    private String name;
    private BigDecimal amount;
    private String currency;
    
    public FinancialReportDto(String name, BigDecimal amount, String currency) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }
}