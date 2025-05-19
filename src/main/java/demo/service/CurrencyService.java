package demo.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
        updateExchangeRates(toCurrencyCode);
        BigDecimal rate = toCurrency.getExchangeRate().divide(
            fromCurrency.getExchangeRate(), 10, BigDecimal.ROUND_HALF_UP);
        return amount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String findCodesExceptProvided(CurrencyCode currencyCode){
        return currencyRepository.findAll().stream()
            .filter(currency -> !currency.getCode().equals(currencyCode))
            .map(currency -> currency.getCode().toString())
            .collect(Collectors.joining(","));

            //returns smthg like: "USD,EUR,GBP"
    } 

    public void updateExchangeRates(CurrencyCode currencyCode) {
        String url = exchangeApiUrl + "?access_key=" + apiKey + "&base=" + String.valueOf(currencyCode) + "&symbols=" + findCodesExceptProvided(currencyCode);//resolved
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<CurrencyCode, BigDecimal> rates = (Map<CurrencyCode, BigDecimal>) response.get("rates");

        logger.log(System.Logger.Level.INFO, "Updating exchange rates for currency: " + currencyCode);
        logger.log(System.Logger.Level.INFO, "Exchange rates: " + response);

        if (rates != null) {
            for (Map.Entry<CurrencyCode, BigDecimal> entry : rates.entrySet()) {
                currencyRepository.findByCode(entry.getKey()).ifPresent(currency -> {
                    currency.setExchangeRate(entry.getValue());
                    currencyRepository.save(currency);
                });
            }
        }
    }
}
