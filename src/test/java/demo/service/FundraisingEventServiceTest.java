package demo.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import demo.entity.Currency;
import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.repository.CurrencyRepository;
import demo.repository.FundraisingEventRepository;

public class FundraisingEventServiceTest {

    private FundraisingEventRepository eventRepository;
    private CurrencyRepository currencyRepository;
    private FundraisingEventService fundraisingEventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(FundraisingEventRepository.class);
        currencyRepository = mock(CurrencyRepository.class);
        fundraisingEventService = new FundraisingEventService(eventRepository, currencyRepository);
    }

    @Test
    void testCreateEvent_success() {
        Currency currency = new Currency();
        currency.setCode(CurrencyCode.USD);

        when(currencyRepository.findByCode(CurrencyCode.USD)).thenReturn(Optional.of(currency));

        FundraisingEvent savedEvent = new FundraisingEvent();
        savedEvent.setId(1L);
        savedEvent.setName("Test Event");
        savedEvent.setCurrency(currency);
        savedEvent.setTotalAmount(BigDecimal.ZERO);

        when(eventRepository.save(any(FundraisingEvent.class))).thenReturn(savedEvent);

        FundraisingEvent result = fundraisingEventService.createEvent("Test Event", CurrencyCode.USD);

        assertNotNull(result);
        assertEquals("Test Event", result.getName());
        assertEquals(CurrencyCode.USD, result.getCurrency().getCode());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());

        verify(currencyRepository).findByCode(CurrencyCode.USD);
        verify(eventRepository).save(any(FundraisingEvent.class));
    }

    @Test
    void testCreateEvent_unsupportedCurrency_throwsException() {
        when(currencyRepository.findByCode(CurrencyCode.EUR)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                fundraisingEventService.createEvent("Test", CurrencyCode.EUR)
        );
    }

    @Test
    void testFindAll_shouldReturnList() {
        List<FundraisingEvent> mockList = Arrays.asList(new FundraisingEvent(), new FundraisingEvent());

        when(eventRepository.findAll()).thenReturn(mockList);

        List<FundraisingEvent> result = fundraisingEventService.findAll();

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void testFindById_success() {
        FundraisingEvent event = new FundraisingEvent();
        event.setId(5L);

        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));

        FundraisingEvent result = fundraisingEventService.findById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void testFindById_notFound_throwsException() {
        when(eventRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fundraisingEventService.findById(100L));
    }

}