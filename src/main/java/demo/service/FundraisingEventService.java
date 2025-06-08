package demo.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import demo.dto.FinancialReportDto;
import demo.entity.Currency;
import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.exception.BadRequestException;
import demo.exception.ResourceNotFoundException;
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

        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Event name cannot be empty");
        }

        if (eventRepository.existsByName(name)) {
            throw new BadRequestException("Event with the same name already exists: " + name);
        }

        Optional<Currency> currencyOpt = currencyRepository.findByCode(currencyCode);
        if (currencyOpt.isEmpty()) {
            throw new ResourceNotFoundException("Unsupported currency code: " + currencyCode);
        }

        FundraisingEvent event = new FundraisingEvent();
        event.setName(name.trim());
        event.setCurrency(currencyOpt.get());
        event.setTotalAmount(BigDecimal.ZERO);

        FundraisingEvent savedEvent = eventRepository.save(event);
        logger.log(System.Logger.Level.INFO, "Event created with ID: " + savedEvent.getId());

        return savedEvent;
    }

    public List<FundraisingEvent> findAll() {
        return eventRepository.findAll();
    }

    public FundraisingEvent findById(Long id) {
        logger.log(System.Logger.Level.INFO, "Finding event with ID: " + id);
        return eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
    }

    public List<FinancialReportDto> getFinancialReport() {
        List<FundraisingEvent> events = eventRepository.findAll();
        
        logger.log(System.Logger.Level.INFO, "Generating financial report for all events");
        
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        
        // сonvert events to DTOs
        List<FinancialReportDto> report = events.stream()
            .map(event -> new FinancialReportDto(
                event.getName(),
                event.getTotalAmount(),
                event.getCurrency().getCode().toString()))
            .collect(Collectors.toList());
        
        return report;
    }
}