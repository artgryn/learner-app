package com.artgr.learner.data.repository;

import com.artgr.learner.data.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
}
