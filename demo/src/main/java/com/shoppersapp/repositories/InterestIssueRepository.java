package com.shoppersapp.repositories;

import com.shoppersapp.model.InterestIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestIssueRepository extends JpaRepository<InterestIssue, Integer> {
    @Query("SELECT i FROM InterestIssue i WHERE i.interestIssueId = (SELECT MAX(ii.interestIssueId) FROM InterestIssue ii)")
    InterestIssue findLatestInterestIssue();
}