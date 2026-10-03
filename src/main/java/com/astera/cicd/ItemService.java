package com.astera.cicd;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemService {
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong();

    public List<Item> findAll() {
        return items.values().stream().sorted(Comparator.comparingLong(Item::id)).toList();
    }

    public Item findById(long id) {
        Item item = items.get(id);
        if (item == null) {
            throw notFound();
        }
        return item;
    }

    public Item create(ItemRequest request) {
        Item item = new Item(nextId.incrementAndGet(), request.name(), request.description());
        items.put(item.id(), item);
        return item;
    }

    public Item update(long id, ItemRequest request) {
        Item updated = new Item(id, request.name(), request.description());
        if (items.replace(id, updated) == null) {
            throw notFound();
        }
        return updated;
    }

    public void delete(long id) {
        if (items.remove(id) == null) {
            throw notFound();
        }
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
    }
}
