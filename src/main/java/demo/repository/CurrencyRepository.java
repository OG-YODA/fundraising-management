package demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.entity.Currency;
import demo.enums.CurrencyCode;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCode(CurrencyCode code);
}

