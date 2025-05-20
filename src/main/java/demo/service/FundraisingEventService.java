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

   System.Logger logger = System.getLogger(FundraisingEventService.class.getName());

    public FundraisingEventService(FundraisingEventRepository eventRepository, CurrencyRepository currencyRepository) {
        this.eventRepository = eventRepository;
        this.currencyRepository = currencyRepository;
    }

    public FundraisingEvent createEvent(String name, CurrencyCode currencyCode) {
        logger.log(System.Logger.Level.INFO, "Creating event with name: " + name + " and currency code: " + currencyCode);
        Optional<Currency> currencyOpt = currencyRepository.findByCode(currencyCode);
        if (currencyOpt.isEmpty()) {
            throw new IllegalArgumentException("Unsupported currency code: " + currencyCode);
        }

        FundraisingEvent event = new FundraisingEvent();
        event.setName(name);
        event.setCurrency(currencyOpt.get());
        event.setTotalAmount(BigDecimal.ZERO);

        FundraisingEvent savedEvent = eventRepository.save(event);
        logger.log(System.Logger.Level.INFO, "Event created with ID: " + savedEvent.getId() + ", Name: " + name + ", Currency: " + currencyCode);
        
        return savedEvent;
    }

    public List<FundraisingEvent> findAll() {
        return eventRepository.findAll();
    }

    public FundraisingEvent findById(Long id) {
        logger.log(System.Logger.Level.INFO, "Finding event with ID: " + id);
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));
    }

    public void closeEvent(Long id) {
        FundraisingEvent event = findById(id);
        event.setTotalAmount(event.getTotalAmount());
        logger.log(System.Logger.Level.INFO, "Closing event with ID: " + id);
        eventRepository.save(event);
    }

    public void printFinancialReport() {//TODO: fix formating
        List<FundraisingEvent> events = eventRepository.findAll();

        logger.log(System.Logger.Level.INFO, "Generating financial report for all events");
        
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        
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