package com.ex.eventpro_api.event.internal.service;


import com.ex.eventpro_api.event.internal.dto.request.TicketCategoryRequest;
import com.ex.eventpro_api.event.internal.entity.Event;
import com.ex.eventpro_api.event.internal.entity.TicketCategoriesPerEvent;
import com.ex.eventpro_api.event.internal.enums.EventStatus;
import com.ex.eventpro_api.event.internal.repository.EventRepository;
import com.ex.eventpro_api.event.internal.service.Impl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void shouldThrowExceptionWhenPublishingEventWithoutCategories() {

        Event event = Event.builder().id(1L).categories(new ArrayList<>()).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.publishEvent(1L);
        });

        assertTrue(exception.getMessage().contains("sans catégories"));

    }

    @Test
    void shouldPublishingEventSuccessfulyWhenCategoriesExist() {

        TicketCategoriesPerEvent category = TicketCategoriesPerEvent.builder()
                .id(1L)
                .build();

        LocalDateTime futureDate = LocalDateTime.now().plusMinutes(10);

        Event event = Event.builder().id(1L)
                .salesClosingDate(futureDate)
                .categories(List.of(category))
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.publishEvent(1L);

        assertEquals(EventStatus.PUBLISHED, event.getStatus());

    }
}
