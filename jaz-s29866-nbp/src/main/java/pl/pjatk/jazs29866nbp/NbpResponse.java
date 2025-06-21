package pl.pjatk.jazs29866nbp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class NbpResponse {
    @JsonProperty("table")
    private String table;

    @JsonProperty("rates")
    private List<NbpRate> rates;

    // Gettery i Settery
    public String getTable() {
        return table;
    }

    public List<NbpRate> getRates() {
        return rates;
    }

    public void setRates(List<NbpRate> rates) {
        this.rates = rates;
    }
}
