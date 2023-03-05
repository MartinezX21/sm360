package com.agency.sm360.dao;


import com.agency.sm360.utils.ListingState;
import com.agency.sm360.entities.Dealer;
import com.agency.sm360.entities.Listing;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface ListingDAO extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {
    @Query("SELECT COUNT(l) FROM Listing l WHERE l.dealer = :dealer AND l.state = :state")
    int countByDealerAndState(@Param("dealer") Dealer dealer, @Param("state") ListingState state);

    @Query("SELECT l FROM Listing l WHERE l.state = :state ORDER BY l.createdAt ASC LIMIT 1")
    Optional<Listing> findOldestByState(@Param("state") ListingState state);
}
