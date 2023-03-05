package com.agency.sm360.criteria;

import com.agency.sm360.utils.ListingState;
import com.agency.sm360.entities.Dealer;
import com.agency.sm360.entities.Listing;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ListingSpecs {
    public static Specification<Listing> dealerIs(Dealer dealer) {
        return new Specification<Listing>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Listing> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                return builder.equal(root.<Dealer> get("dealer"), dealer);
            }
        };
    }
    public static Specification<Listing> stateIs(ListingState state) {
        return new Specification<Listing>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Listing> examRoot, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                return builder.equal(examRoot.<ListingState> get("state"), state);
            }
        };
    }
}
