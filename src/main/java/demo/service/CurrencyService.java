package demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import demo.entity.Currency;
import demo.repository.CurrencyRepository;

@Service
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    @Value("${exchange.api.url}")
    private String exchangeApiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal convert(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode) {
        Currency fromCurrency = currencyRepository.findByCode(fromCurrencyCode).orElseThrow();
        Currency toCurrency = currencyRepository.findByCode(toCurrencyCode).orElseThrow();
        BigDecimal rate = toCurrency.getExchangeRate().divide(
            fromCurrency.getExchangeRate(), 10, BigDecimal.ROUND_HALF_UP);
        return amount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public void updateExchangeRates() {
        String url = exchangeApiUrl + "?access_key=" + apiKey + "&base=PLN&symbols=USD,EUR,GBP,JPY";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Double> rates = (Map<String, Double>) response.get("rates");

        if (rates != null) {
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                currencyRepository.findByCode(entry.getKey()).ifPresent(currency -> {
                    currency.setExchangeRate(BigDecimal.valueOf(entry.getValue()));
                    currencyRepository.save(currency);
                });
            }
        }
    }
}
