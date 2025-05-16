package demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCode(String code);
}

