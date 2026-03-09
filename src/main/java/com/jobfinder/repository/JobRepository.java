package com.jobfinder.repository;

import com.jobfinder.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByFingerprint(String fingerprint);

    List<Job> findByCompanyNameIgnoreCase(String companyName);

    List<Job> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT j FROM Job j WHERE LOWER(j.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Job> findBySkill(@Param("skill") String skill);

    List<Job> findByExperienceLevelIgnoreCase(String experienceLevel);

    List<Job> findByJobTypeIgnoreCase(String jobType);

    @Query("SELECT DISTINCT j.companyName FROM Job j ORDER BY j.companyName")
    List<String> findDistinctCompanyNames();

    @Query("SELECT DISTINCT j.experienceLevel FROM Job j WHERE j.experienceLevel IS NOT NULL ORDER BY j.experienceLevel")
    List<String> findDistinctExperienceLevels();

    @Query("SELECT DISTINCT j.jobType FROM Job j WHERE j.jobType IS NOT NULL ORDER BY j.jobType")
    List<String> findDistinctJobTypes();

    @Query("""
            SELECT j FROM Job j
            WHERE (:role IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :role, '%')))
            AND (:company IS NULL OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :company, '%')))
            AND (:skill IS NULL OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :skill, '%')))
            AND (:experience IS NULL OR LOWER(j.experienceLevel) LIKE LOWER(CONCAT('%', :experience, '%')))
            AND (:jobType IS NULL OR LOWER(j.jobType) = LOWER(:jobType))
            ORDER BY j.postedDate DESC NULLS LAST, j.createdAt DESC
            """)
    List<Job> searchJobs(
            @Param("role") String role,
            @Param("company") String company,
            @Param("skill") String skill,
            @Param("experience") String experience,
            @Param("jobType") String jobType
    );

    @Query("""
            SELECT j FROM Job j
            WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY j.postedDate DESC NULLS LAST, j.createdAt DESC
            """)
    List<Job> globalSearch(@Param("keyword") String keyword);

    void deleteByCompanyNameIgnoreCase(String companyName);
}
