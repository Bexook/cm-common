package com.cm.common.repository;

import com.cm.common.model.domain.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<ExamEntity, Long> {
}
