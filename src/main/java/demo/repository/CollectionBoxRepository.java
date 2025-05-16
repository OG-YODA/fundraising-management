package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.entity.CollectionBox;

public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
}
