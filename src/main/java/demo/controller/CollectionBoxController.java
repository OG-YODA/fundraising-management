package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.entity.CollectionBox;
import demo.service.CollectionBoxService;

@RestController
@RequestMapping("/api/boxes")
public class CollectionBoxController {
    @Autowired
    private CollectionBoxService boxService;

    @PostMapping
    public CollectionBox createBox() {
        return boxService.createBox();
    }

    @GetMapping
    public List<CollectionBox> getAllBoxes() {
        return boxService.getAllBoxes();
    }

    @PutMapping("/{boxId}/assign/{eventId}")
    public void assignBox(@PathVariable Long boxId, @PathVariable Long eventId) {
        boxService.assignBoxToEvent(boxId, eventId);
    }
}
