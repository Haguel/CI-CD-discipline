package com.astera.cicd;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<Item> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Item findById(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Item> create(@Valid @RequestBody ItemRequest request) {
        Item item = service.create(request);
        return ResponseEntity.created(URI.create("/api/items/" + item.id())).body(item);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable long id, @Valid @RequestBody ItemRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
