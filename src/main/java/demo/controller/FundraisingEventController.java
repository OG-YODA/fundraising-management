package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.service.FundraisingEventService;

@RestController
@RequestMapping("/api/events")
public class FundraisingEventController {
    @Autowired
    private FundraisingEventService eventService;

    System.Logger logger = System.getLogger(FundraisingEventController.class.getName());

    @PostMapping("/create")
    public FundraisingEvent createEvent(@RequestParam String name, @RequestParam CurrencyCode currencyCode) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/events/create triggered");
        return eventService.createEvent(name, currencyCode);
    }

    @RequestMapping
    public List<FundraisingEvent> getAllEvents() {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/events triggered");
        return eventService.findAll();
    }

    @PostMapping("/financial-report")
    public void printFinancialReport() {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/events/financial-report triggered");
        eventService.printFinancialReport();
    }

    @PutMapping("/close/{id}")
    public void closeEvent(Long id) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/events/close triggered");
        eventService.closeEvent(id);
    }

}
