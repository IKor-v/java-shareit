package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByAvailableTrueAndDescriptionContainingIgnoreCase(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByRequestNotNull();

    Page<Item> findByOwnerId(Long id, Pageable pageable);
}
