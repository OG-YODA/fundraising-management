package demo.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import demo.enums.CurrencyCode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CollectionBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean empty = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "box_balances", joinColumns = @JoinColumn(name = "box_id"))
    @MapKeyColumn(name = "currency_code")
    @Column(name = "amount")
    private Map<CurrencyCode, BigDecimal> balances = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "event_id")
    private FundraisingEvent assignedEvent;

}