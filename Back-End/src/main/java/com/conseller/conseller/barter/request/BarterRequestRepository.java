package com.conseller.conseller.barter.request;

import com.conseller.conseller.entity.BarterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BarterRequestRepository extends JpaRepository<BarterRequest, Long> {
    Optional<BarterRequest> findByBarterRequestIdx(Long barterRequestIdx);

    @Query("SELECT br FROM BarterRequest br WHERE br.barter = ?1")
    List<BarterRequest> findByBarterIdx(Long barterIdx);

    @Query("SELECT br FROM BarterRequest br WHERE br.user = ?1")
    List<BarterRequest> findByUserIdx(Long userIdx);
}
