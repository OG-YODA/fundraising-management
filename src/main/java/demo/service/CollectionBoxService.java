package demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.entity.CollectionBox;
import demo.entity.FundraisingEvent;
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

    public CollectionBox createBox() {
        return boxRepository.save(new CollectionBox());
    }

    public List<CollectionBox> getAllBoxes() {
        return boxRepository.findAll();
    }

    public void assignBoxToEvent(Long boxId, Long eventId) {
        CollectionBox box = boxRepository.findById(boxId).orElseThrow();
        FundraisingEvent event = eventRepository.findById(eventId).orElseThrow();
        if (!box.isEmpty()) throw new IllegalStateException("Cannot assign a non-empty box to an event.");
        box.setAssignedEvent(event);
        boxRepository.save(box);
    }
}