package com.careerfitai.repository;

import com.careerfitai.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.skills WHERE r.id = :id")
    Optional<Resume> findByIdWithSkills(@Param("id") Long id);
}
