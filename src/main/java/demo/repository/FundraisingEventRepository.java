package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.entity.FundraisingEvent;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
}
