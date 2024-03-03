package com.kevin.ahorcado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kevin.ahorcado.models.VisitCounter;

@Repository
public interface VisitCounterRepository extends JpaRepository<VisitCounter, Long> {

	@Query(value = "SELECT MAX(count) FROM VISIT_COUNTER", nativeQuery = true)
	Integer getMaxCount();

}
