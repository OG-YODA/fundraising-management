package demo.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import demo.dto.CollectionBoxDto;
import demo.entity.CollectionBox;
import demo.entity.Currency;
import demo.entity.FundraisingEvent;
import demo.enums.CurrencyCode;
import demo.repository.CollectionBoxRepository;
import demo.repository.FundraisingEventRepository;

class CollectionBoxServiceTest {

    @InjectMocks
    private CollectionBoxService collectionBoxService;

    @Mock
    private CollectionBoxRepository boxRepository;

    @Mock
    private FundraisingEventRepository eventRepository;

    @Mock
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBox_shouldCreateEmptyBox() {
        CollectionBox expectedBox = new CollectionBox();
        when(boxRepository.save(any(CollectionBox.class))).thenReturn(expectedBox);

        CollectionBox result = collectionBoxService.createBox();

        assertNotNull(result);
        verify(boxRepository).save(any(CollectionBox.class));
    }

    @Test
    void assignBoxToEvent_shouldAssignBoxCorrectly() {
        CollectionBox box = new CollectionBox();
        box.setId(1L);
        FundraisingEvent event = new FundraisingEvent();
        event.setId(2L);

        when(boxRepository.findById(1L)).thenReturn(Optional.of(box));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));
        when(boxRepository.findByAssignedEventId(2L)).thenReturn(Optional.empty());

        collectionBoxService.assignBoxToEvent(1L, 2L);

        assertEquals(event, box.getAssignedEvent());
        verify(boxRepository).save(box);
    }

    @Test
    void unassignBoxFromEvent_shouldUnassign() {
        CollectionBox box = new CollectionBox();
        box.setId(1L);
        box.setAssignedEvent(new FundraisingEvent());

        when(boxRepository.findById(1L)).thenReturn(Optional.of(box));

        collectionBoxService.unassignBoxFromEvent(1L, "Test Reason");

        assertNull(box.getAssignedEvent());
        verify(boxRepository).save(box);
    }

    @Test
    void addFundsToBox_shouldUpdateBalances() {
        CollectionBox box = new CollectionBox();
        box.setBalances(new HashMap<>());
        box.setId(1L);

        when(boxRepository.findById(1L)).thenReturn(Optional.of(box));

        collectionBoxService.addFundsToBox(1L, new BigDecimal("100.00"), CurrencyCode.USD);

        assertEquals(new BigDecimal("100.00"), box.getBalances().get(CurrencyCode.USD));
        assertFalse(box.isEmpty());
        verify(boxRepository).save(box);
    }

    @Test
    void transferFundsToEventBalance_sameCurrency() {
        FundraisingEvent event = new FundraisingEvent();
        Currency currency = new Currency();
        currency.setCode(CurrencyCode.USD);
        event.setCurrency(currency);
        event.setTotalAmount(BigDecimal.ZERO);

        CollectionBox box = new CollectionBox();
        box.setAssignedEvent(event);
        box.setBalances(Map.of(CurrencyCode.USD, new BigDecimal("50.00")));
        box.setId(1L);

        when(boxRepository.findById(1L)).thenReturn(Optional.of(box));

        collectionBoxService.transferFundsToEventBalance(1L);

        assertEquals(new BigDecimal("50.00"), event.getTotalAmount());
        verify(eventRepository).save(event);
        verify(boxRepository).save(box);
    }

    @Test
    void transferFundsToEventBalance_differentCurrency() {
        FundraisingEvent event = new FundraisingEvent();
        Currency currency = new Currency();
        currency.setCode(CurrencyCode.EUR);
        event.setCurrency(currency);
        event.setTotalAmount(BigDecimal.ZERO);

        CollectionBox box = new CollectionBox();
        box.setAssignedEvent(event);
        box.setBalances(Map.of(CurrencyCode.USD, new BigDecimal("10.00")));
        box.setId(1L);

        when(boxRepository.findById(1L)).thenReturn(Optional.of(box));
        when(currencyService.convert(new BigDecimal("10.00"), CurrencyCode.USD, CurrencyCode.EUR))
                .thenReturn(new BigDecimal("9.00"));

        collectionBoxService.transferFundsToEventBalance(1L);

        assertEquals(new BigDecimal("9.00"), event.getTotalAmount());
        verify(eventRepository).save(event);
        verify(boxRepository).save(box);
    }

    @Test
    void getBoxesInfo_shouldReturnDtos() {
        CollectionBox box1 = new CollectionBox();
        box1.setId(1L);
        box1.setEmpty(true);
        box1.setAssignedEvent(null);

        CollectionBox box2 = new CollectionBox();
        box2.setId(2L);
        box2.setEmpty(false);
        box2.setAssignedEvent(new FundraisingEvent());

        when(boxRepository.findAll()).thenReturn(List.of(box1, box2));

        List<CollectionBoxDto> result = collectionBoxService.getBoxesInfo();

        assertEquals(2, result.size());
        assertEquals("Not assigned", result.get(0).getAssignmentStatus());
        assertEquals("Assigned", result.get(1).getAssignmentStatus());
    }
}