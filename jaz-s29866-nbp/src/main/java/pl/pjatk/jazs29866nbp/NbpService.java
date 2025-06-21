package pl.pjatk.jazs29866nbp;
//komentarze do pomocy
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NbpService {

    private final RestTemplate restTemplate;
    private final NbpRepository nbpRepository;

    @Autowired
    public NbpService(RestTemplate restTemplate, NbpRepository nbpRepository) {
        this.restTemplate = restTemplate;
        this.nbpRepository = nbpRepository;
    }

    public ExchangeRateResponse getAverageExchangeRate(String currency, LocalDate startDate, LocalDate endDate) {
        // Walidacja dat
        validateDates(startDate, endDate);

        // Walidacja kodu waluty
        validateCurrency(currency);

        try {
            // API NBP
            String url = buildNbpApiUrl(currency, startDate, endDate);
            NbpResponse response = restTemplate.getForObject(url, NbpResponse.class);

            if (response == null || response.getRates() == null || response.getRates().isEmpty()) {
                throw new RuntimeException("Brak danych dla podanych parametrów");
            }

            // Obliczenie średniego kursu
            BigDecimal averageRate = calculateAverageRate(response);

            // Zapisanie zapytania w bazie danych
            ExchangeRateQuery query = new ExchangeRateQuery(
                    currency.toUpperCase(),
                    startDate,
                    endDate,
                    averageRate,
                    LocalDateTime.now()
            );
            nbpRepository.save(query);

            // odpowiedź
            return new ExchangeRateResponse(
                    currency.toUpperCase(),
                    startDate,
                    endDate,
                    averageRate,
                    LocalDate.now()
            );

        } catch (HttpClientErrorException e) {
            handleNbpApiError(e);
            return null; // Brak wykonania
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Data rozpoczęcia nie może być późniejsza niż data zakończenia");
        }

        if (endDate.isAfter(LocalDate.now())) {
            throw new RuntimeException("Data końcowa nie może być z przyszłości");
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new RuntimeException("Kod waluty nie może być pusty");
        }

        if (currency.length() != 3) {
            throw new RuntimeException("Kod waluty musi mieć dokładnie 3 znaki");
        }
    }

    private String buildNbpApiUrl(String currency, LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("http://api.nbp.pl/api/exchangerates/rates/A/%s/%s/%s/",
                currency.toUpperCase(),
                startDate.format(formatter),
                endDate.format(formatter)
        );
    }

    private BigDecimal calculateAverageRate(NbpResponse nbpResponse) {
        return nbpResponse.getRates().stream()
                .map(NbpRate::getMid)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(nbpResponse.getRates().size()), 4, RoundingMode.HALF_UP);
    }

    private void handleNbpApiError(HttpClientErrorException e) {
        switch (e.getStatusCode()) {
            case HttpStatus.NOT_FOUND:
                throw new RuntimeException("Nie znaleziono danych dla dat lub waluty");
            case HttpStatus.BAD_REQUEST:
                throw new RuntimeException("Zostały podane nieprawidłowe parametry");
            case HttpStatus.TOO_MANY_REQUESTS:
                throw new RuntimeException("Przekroczono limit zapytań do API NBP. Spróbuj ponownie za chwilę");
            default:
                throw new RuntimeException("Błąd komunikacji z API. Spróbuj ponownie później");
        }
    }
}