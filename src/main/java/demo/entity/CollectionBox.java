package demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
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
    private Map<String, BigDecimal> balances = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "event_id")
    private FundraisingEvent assignedEvent;

}