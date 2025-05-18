package demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import demo.entity.Currency;
import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.repository.CurrencyRepository;
import demo.repository.FundraisingEventRepository;

@Service
public class FundraisingEventService {
    private final FundraisingEventRepository eventRepository;
    private final CurrencyRepository currencyRepository;

    public FundraisingEventService(FundraisingEventRepository eventRepository, CurrencyRepository currencyRepository) {
        this.eventRepository = eventRepository;
        this.currencyRepository = currencyRepository;
    }

    public FundraisingEvent createEvent(String name, CurrencyCode currencyCode) {
        Optional<Currency> currencyOpt = currencyRepository.findByCode(String.valueOf(currencyCode));
        if (currencyOpt.isEmpty()) {
            throw new IllegalArgumentException("Unsupported currency code: " + currencyCode);
        }

        FundraisingEvent event = new FundraisingEvent();
        event.setName(name);
        event.setCurrency(currencyOpt.get());
        event.setTotalAmount(BigDecimal.ZERO);
        return eventRepository.save(event);
    }

    public List<FundraisingEvent> findAll() {
        return eventRepository.findAll();
    }

    public FundraisingEvent findById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));
    }

    public void closeEvent(Long id) {
        FundraisingEvent event = findById(id);
        event.setTotalAmount(event.getTotalAmount());
        eventRepository.save(event);
    }

    public void printFinancialReport() {
        List<FundraisingEvent> events = eventRepository.findAll();
        
        // correct table formatting
        int maxNameLength = events.stream()
            .mapToInt(event -> event.getName().length())
            .max()
            .orElse(20); // default
        
        System.out.println("Financial report:");
        System.out.println("+" + "-".repeat(maxNameLength + 2) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+");
        System.out.printf("| %-" + maxNameLength + "s | %-10s | %-8s |\n", 
            "Fundraising event name", "Amount", "Currency");
        System.out.println("+" + "-".repeat(maxNameLength + 2) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+");
        
        // event data
        for (FundraisingEvent event : events) {
            System.out.printf("| %-" + maxNameLength + "s | %10.2f | %-8s |\n", 
                event.getName(), 
                event.getTotalAmount(), 
                event.getCurrency().getCode());
        }
        
        System.out.println("+" + "-".repeat(maxNameLength + 2) + "+" + "-".repeat(12) + "+" + "-".repeat(10) + "+");
    }
}