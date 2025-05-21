package demo.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import demo.entity.Currency;
import demo.enums.CurrencyCode;
import demo.repository.CurrencyRepository;

@Service
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    System.Logger logger = System.getLogger(CurrencyService.class.getName());

    @Value("${exchange.api.url}")
    private String exchangeApiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal convert(BigDecimal amount, CurrencyCode fromCurrencyCode, CurrencyCode toCurrencyCode) {

        Currency fromCurrency = currencyRepository.findByCode(fromCurrencyCode).orElseThrow();
        Currency toCurrency = currencyRepository.findByCode(toCurrencyCode).orElseThrow();
        BigDecimal rate = toCurrency.getExchangeRate().divide(
            fromCurrency.getExchangeRate(), 10, BigDecimal.ROUND_HALF_UP);

        logger.log(System.Logger.Level.INFO, "Converting " + amount + " from " + fromCurrencyCode + " to " + toCurrencyCode);
        logger.log(System.Logger.Level.INFO, "Exchange rate: " + rate);
        return amount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String findCodesExceptProvided(CurrencyCode currencyCode){
        return currencyRepository.findAll().stream()
            .filter(currency -> !currency.getCode().equals(currencyCode))
            .map(currency -> currency.getCode().toString())
            .collect(Collectors.joining(","));

            //returns smthg like: "USD,EUR,GBP"
    } 

    @Transactional
    public void updateExchangeRates(CurrencyCode currencyCode) {
        String url = exchangeApiUrl + "?access_key=" + apiKey + "&base=" + currencyCode + "&symbols=" + findCodesExceptProvided(currencyCode);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        if (response == null || !response.containsKey("rates")) {
            throw new IllegalStateException("Invalid API response: " + response);
        }
        if (response.get("success") == null || !(Boolean) response.get("success")) {
            throw new IllegalStateException("API request failed: " + response);
        }
        Map<String, Object> rawRates = (Map<String, Object>) response.get("rates");
        Map<CurrencyCode, BigDecimal> rates = new HashMap<>();
        rawRates.forEach((key, value) -> {
            BigDecimal rate = value instanceof Double ? 
                BigDecimal.valueOf((Double) value) : 
                BigDecimal.valueOf(((Integer) value).doubleValue());
            rates.put(CurrencyCode.valueOf(key), rate);
        });

        logger.log(System.Logger.Level.INFO, "Updating exchange rates for currency: ");
        logger.log(System.Logger.Level.INFO, "Exchange rates: " + rates);

        //db rates update
        rates.put(currencyCode, BigDecimal.ONE);
        rates.forEach((code, rate) -> {
            currencyRepository.findByCode(code).ifPresentOrElse(
                currency -> {
                    currency.setExchangeRate(rate);
                    currencyRepository.save(currency);
                    logger.log(System.Logger.Level.INFO, "Updated rate for " + code + ": " + rate);
                },
                () -> logger.log(System.Logger.Level.WARNING, "Currency not found: " + code)
            );
        });
    }
}
