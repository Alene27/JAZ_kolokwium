package pl.pjatk.jazs29866nbp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


// DataTransObj dla odpowiedzi API
class ExchangeRateResponse {
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal averageRate;
    private LocalDate queryDate;

    public ExchangeRateResponse(String currency, LocalDate startDate, LocalDate endDate,
                                BigDecimal averageRate, LocalDate queryDate) {
        this.currency = currency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.averageRate = averageRate;
        this.queryDate = queryDate;
    }

    // Gettery
    public String getCurrency() {
        return currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getAverageRate() {
        return averageRate;
    }

    public LocalDate getQueryDate() {
        return queryDate;
    }
}
