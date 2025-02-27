package com.usereligibilityapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.usereligibilityapi.entity.EligibilityEntity;

public interface EligibilityRepository extends JpaRepository<EligibilityEntity, Integer> {

	@Query("select distinct(planName) from EligibilityEntity")
	List<String> findPlanNames();

	@Query("select distinct(planStatus) from EligibilityEntity")
	List<String> findPlanStatus();
}
