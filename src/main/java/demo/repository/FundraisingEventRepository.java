package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import demo.entity.FundraisingEvent;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM FundraisingEvent e WHERE e.name = ?1")
    boolean existsByName(String name);
}
