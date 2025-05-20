package demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import demo.entity.CollectionBox;

public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
    @Query("SELECT cb FROM CollectionBox cb WHERE cb.assignedEvent.id = :eventId")
    Optional<CollectionBox> findByAssignedEventId(@Param("eventId") Long eventId);

    Optional<CollectionBox> findById(Long id);
}
