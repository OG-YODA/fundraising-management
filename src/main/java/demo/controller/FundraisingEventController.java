package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.service.FundraisingEventService;

@Controller
@RequestMapping("/api/events")
public class FundraisingEventController {
    @Autowired
    private FundraisingEventService eventService;

    @PostMapping("/create")
    public FundraisingEvent createEvent(@PathVariable String name, @PathVariable CurrencyCode currencyCode) {
        return eventService.createEvent(name, currencyCode);
    }

    @RequestMapping
    public List<FundraisingEvent> getAllEvents() {
        return eventService.findAll();
    }

    @PostMapping("financial-report")
    public void printFinancialReport() {
        eventService.printFinancialReport();
    }

    @PutMapping("/close/{id}")
    public void closeEvent(Long id) {
        eventService.closeEvent(id);
    }

}
