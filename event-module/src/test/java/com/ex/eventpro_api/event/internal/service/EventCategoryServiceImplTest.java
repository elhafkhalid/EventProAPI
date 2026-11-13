package com.ex.eventpro_api.event.internal.service;

import com.ex.eventpro_api.event.internal.dto.request.EventCreateRequest;
import com.ex.eventpro_api.event.internal.dto.request.TicketCategoryRequest;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.repository.TicketCategoryRepository;
import com.ex.eventpro_api.event.internal.service.Impl.EventCategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventCategoryServiceImplTest {

    @Mock
    private TicketCategoryRepository ticketCategoryRepository;

    @InjectMocks
    private EventCategoryServiceImpl eventCategoryService;

    @Test
    void shouldThrowExceptionWhenTotalQuantityExceedsEventCapacity() {

        Event event = Event.builder().capacity(100).build();

        TicketCategoryRequest cat1 = new TicketCategoryRequest("VIP", BigDecimal.valueOf(100), 101, 2);

        EventCreateRequest request = new EventCreateRequest(
                1L, "Test", "Desc", "Lieu", null, null, null, null, 100, List.of(cat1)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {

            eventCategoryService.buildAndValidateCategories(request, event);

        });

        assertTrue(exception.getMessage().contains("dépasse la capacité"));

    }

    @Test
    void shouldCreateCategoriesWhenCapacityIsValid() {

        Event event = Event.builder().capacity(100).build();
        TicketCategoryRequest cat1 = new TicketCategoryRequest("VIP", BigDecimal.valueOf(100), 40, 2);
        EventCreateRequest request = new EventCreateRequest(
                1L, "Test", "Desc", "Lieu", null, null, null, null, 100, List.of(cat1)
        );

        when(ticketCategoryRepository.findByNameIgnoreCase("VIP")).thenReturn(Optional.empty());
        when(ticketCategoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = eventCategoryService.buildAndValidateCategories(request, event);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getCategory().getName());
    }

}
