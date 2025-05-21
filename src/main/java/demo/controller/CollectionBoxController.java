package demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.dto.CollectionBoxDto;
import demo.entity.CollectionBox;
import demo.enums.CurrencyCode;
import demo.service.CollectionBoxService;

@RestController
@RequestMapping("/api/boxes")
public class CollectionBoxController {
    @Autowired
    private CollectionBoxService boxService;
    
    System.Logger logger = System.getLogger(CollectionBoxController.class.getName());

    @PostMapping("/create")
    public CollectionBox createBox() {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/boxes/create triggered");
        return boxService.createBox();
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxDto>> getAllBoxesInfo() {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/boxes triggered");
        List<CollectionBoxDto> boxes = boxService.getBoxesInfo();
        return ResponseEntity.ok(boxes);
    }

    @PutMapping("/{boxId}/assign/{eventId}")
    public void assignBox(@PathVariable Long boxId, @PathVariable Long eventId) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/boxes/{boxId}/assign/{eventId} triggered");
        boxService.assignBoxToEvent(boxId, eventId);
    }

    @PutMapping("/{boxId}/unassign")
    public void unassignBox(@PathVariable Long boxId, @RequestParam String reason) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/boxes/{boxId}/unassign/{reason} triggered");
        boxService.unassignBoxFromEvent(boxId, reason);
    }

    @PutMapping("/{boxId}/addFunds/{amount}/{currencyCode}")
    public void addFunds(@PathVariable Long boxId, @PathVariable BigDecimal amount, @PathVariable CurrencyCode currencyCode) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/boxes/{boxId}/addFunds/{amount}/{currencyCode} triggered");
        boxService.addFundsToBox(boxId, amount, currencyCode);
    }

    @PutMapping("/{boxId}/transferToEvent")
    public void transferFundsToEvent(@PathVariable Long boxId) {
        logger.log(System.Logger.Level.INFO, "Endpoint /api/boxes/{boxId}/transferToEvent triggered");
        boxService.transferFundsToEventBalance(boxId);
    }
}
