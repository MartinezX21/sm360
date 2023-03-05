package com.agency.sm360.dao;

import com.agency.sm360.entities.Dealer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface DealerDAO extends JpaRepository<Dealer, UUID> {
}
