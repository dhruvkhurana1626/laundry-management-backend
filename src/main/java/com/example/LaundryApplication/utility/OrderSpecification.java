package com.example.LaundryApplication.utility;

import com.example.LaundryApplication.enums.OrderStatus;
import com.example.LaundryApplication.model.OrderEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<OrderEntity> buildFilterSpec(
            Integer id,
            OrderStatus status,
            String search,
            Integer days,
            Long userId) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filter by ID
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            // 2. Filter by Status
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 3. Search across Customer Name, Phone, and Email
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("customerName")), pattern);
                Predicate phoneLike = cb.like(root.get("phone"), pattern);
                Predicate emailLike = cb.like(cb.lower(root.get("email")), pattern);

                predicates.add(cb.or(nameLike, phoneLike, emailLike));
            }

            // 4. Filter by Days
            if (days != null) {
                LocalDateTime limit = LocalDateTime.now().minusDays(days);
                predicates.add(cb.greaterThan(root.get("createdAt"), limit));
            }

            // Ownership filter
            if (userId != null) {
                predicates.add(
                        cb.equal(root.get("user").get("id"), userId)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}