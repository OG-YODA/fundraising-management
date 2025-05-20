package demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.enums.CurrencyCode;
import demo.service.CurrencyService;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {
    @Autowired
    private final CurrencyService currencyService;
    
    System.Logger logger = System.getLogger(CurrencyController.class.getName());
    
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/update")
    public void updateExchangeRates(@RequestParam CurrencyCode currencyCode) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/currency/update triggered");
        currencyService.updateExchangeRates(currencyCode);
    }
}
