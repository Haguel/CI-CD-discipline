package com.astera.cicd;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;

class ItemServiceTests {
    private final ItemService service = new ItemService();

    @Test
    void createsDistinctItemsAndListsThemInIdOrder() {
        assertThat(service.findAll()).isEmpty();
        Item first = service.create(new ItemRequest("First", "Description"));
        Item second = service.create(new ItemRequest("Second", null));
        assertThat(second.id()).isGreaterThan(first.id());
        assertThat(service.findById(first.id())).isEqualTo(first);
        assertThat(service.findAll()).containsExactly(first, second);
    }

    @Test
    void updatesExistingItemWithoutChangingId() {
        Item item = service.create(new ItemRequest("Before", "Old"));
        Item updated = service.update(item.id(), new ItemRequest("After", "New"));
        assertThat(updated).isEqualTo(new Item(item.id(), "After", "New"));
        assertThat(service.findById(item.id())).isEqualTo(updated);
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void deletesExistingItem() {
        Item item = service.create(new ItemRequest("Item", null));
        service.delete(item.id());
        assertThat(service.findAll()).isEmpty();
        assertNotFound(() -> service.findById(item.id()));
    }

    @Test
    void missingItemsReturnNotFoundAndAreNotCreatedByUpdate() {
        assertNotFound(() -> service.findById(99));
        assertNotFound(() -> service.update(99, new ItemRequest("Missing", null)));
        assertNotFound(() -> service.delete(99));
        assertThat(service.findAll()).isEmpty();
    }

    private void assertNotFound(ThrowingCallable operation) {
        assertThatThrownBy(operation).isInstanceOfSatisfying(ResponseStatusException.class,
                exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
