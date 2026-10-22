package com.app.demo.repo;

import com.app.demo.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;



@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> , JpaSpecificationExecutor<Candidate> {


}
