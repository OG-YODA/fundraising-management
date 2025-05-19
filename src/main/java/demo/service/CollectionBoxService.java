package demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.dto.CollectionBoxDto;
import demo.entity.CollectionBox;
import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.repository.CollectionBoxRepository;
import demo.repository.FundraisingEventRepository;

@Service
public class CollectionBoxService {
    @Autowired
    private CollectionBoxRepository boxRepository;
    @Autowired
    private FundraisingEventRepository eventRepository;
    @Autowired
    private CurrencyService currencyService;

    System.Logger logger = System.getLogger(CollectionBoxService.class.getName());

    public CollectionBox createBox() {
        logger.log(System.Logger.Level.INFO, "Creating a new collection box");//resolve provide id
        return boxRepository.save(new CollectionBox());
    }

    public void assignBoxToEvent(Long boxId, Long eventId) {
        CollectionBox box = boxRepository.findById(boxId).orElseThrow();
        if (box.getAssignedEvent() != null) throw new IllegalStateException("Box is already assigned to an event.");
        FundraisingEvent event = eventRepository.findById(eventId).orElseThrow();
        if (!box.isEmpty()) throw new IllegalStateException("Cannot assign a non-empty box to an event.");
        logger.log(System.Logger.Level.INFO, "Assigning box with ID " + boxId + " to event with ID " + eventId);
        box.setAssignedEvent(event);
        boxRepository.save(box);
    }

    public void unassignBoxFromEvent(Long boxId, String reason) {
        CollectionBox box = boxRepository.findById(boxId).orElseThrow();
        box.setAssignedEvent(null);
        boxRepository.save(box);

        logger.log(System.Logger.Level.INFO, "Unassigning box with ID " + boxId + " from event. Reason: " + reason);
    }

    public void addFundsToBox(Long boxId, BigDecimal amount, CurrencyCode currencyCode) {
        CollectionBox box = boxRepository.findById(boxId).orElseThrow();
        var balances = box.getBalances();
        balances.put(currencyCode, balances.getOrDefault(currencyCode, BigDecimal.ZERO).add(amount));
        box.setBalances(balances);
        box.setEmpty(false);
        boxRepository.save(box);
        logger.log(System.Logger.Level.INFO, "Adding funds to box with ID " + boxId + ": " + amount + " " + currencyCode);
    }

    public void transferFundsToEventBalance(Long boxId) {
        CollectionBox box = boxRepository.findById(boxId)
            .orElseThrow(() -> new IllegalArgumentException("Box not found with id: " + boxId));

        FundraisingEvent event = box.getAssignedEvent();
        if (event == null) {
            logger.log(System.Logger.Level.INFO, "Box with ID " + boxId + " is not assigned to any event.");
            logger.log(System.Logger.Level.INFO, "Returning funds from the box and clearing balances.");

            //Returning mechanism
            
            box.setEmpty(true);
            box.setBalances(Map.of());
            boxRepository.save(box);

            throw new IllegalStateException("Box is not assigned to any event.");
        }

        CurrencyCode eventCurrencyCode = event.getCurrency().getCode();
        Map<CurrencyCode, BigDecimal> balances = box.getBalances();

        if (balances.isEmpty()) {
            throw new IllegalStateException("Box is empty, cannot transfer funds.");
        }

        logger.log(System.Logger.Level.INFO, "Transferring funds from box with ID " + boxId + " to event with ID " + event.getId());

        for (Map.Entry<CurrencyCode, BigDecimal> entry : balances.entrySet()) {
            CurrencyCode boxCurrencyCode = entry.getKey();
            BigDecimal amount = entry.getValue();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            if (boxCurrencyCode.equals(eventCurrencyCode)) {
                event.setTotalAmount(event.getTotalAmount().add(amount));
            } else {
                try {
                    BigDecimal convertedAmount = currencyService.convert(amount, boxCurrencyCode, eventCurrencyCode);
                    logger.log(System.Logger.Level.INFO, "Converted " + amount + " " + boxCurrencyCode + " to " + convertedAmount + " " + eventCurrencyCode);
                    event.setTotalAmount(event.getTotalAmount().add(convertedAmount));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error converting currency from " + boxCurrencyCode + " to " + eventCurrencyCode + ": " + e.getMessage());
                }
            }

            balances.remove(boxCurrencyCode);
        }

        eventRepository.save(event);
        boxRepository.save(box);
    }

    public List<CollectionBoxDto> getBoxesInfo() {
        logger.log(System.Logger.Level.INFO, "Retrieving information about all collection boxes");
        return boxRepository.findAll().stream()
            .map(box -> new CollectionBoxDto(
                box.getId(),
                box.getAssignedEvent() != null ? "Assigned" : "Not assigned",
                box.isEmpty()
            ))
            .collect(Collectors.toList());
    }
}