package demo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import demo.entity.Currency;
import demo.enums.CurrencyCode;
import demo.repository.CurrencyRepository;
import demo.service.CurrencyService;

@SpringBootApplication
public class FundraisingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundraisingManagementApplication.class, args);
	}

	@Bean
    public CommandLineRunner dataInitializer(CurrencyRepository currencyRepository, CurrencyService currencyService) {
        return args -> {
            // no rates init
            List<Currency> initialCurrencies = Arrays.asList(
				new Currency(null, CurrencyCode.USD, "US Dollar", BigDecimal.ZERO),
				new Currency(null, CurrencyCode.EUR, "Euro", BigDecimal.ZERO),
				new Currency(null, CurrencyCode.GBP, "British Pound", BigDecimal.ZERO),
				new Currency(null, CurrencyCode.PLN, "Polish Zloty", BigDecimal.ZERO),
				new Currency(null, CurrencyCode.JPY, "Japanese Yen", BigDecimal.ZERO)
			);

            for (Currency currency : initialCurrencies) {
                currencyRepository.findByCode(String.valueOf(currency.getCode())).orElseGet(() -> currencyRepository.save(currency));
            }

            // rates update after init
            currencyService.updateExchangeRates();
            System.out.println("Initial currencies loaded and exchange rates updated successfully.");
        };
    }

}
