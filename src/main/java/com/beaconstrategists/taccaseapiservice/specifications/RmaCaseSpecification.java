package com.beaconstrategists.taccaseapiservice.specifications;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.entities.RmaCaseEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;

public class RmaCaseSpecification {

    public static Specification<RmaCaseEntity> buildSpecification(
            OffsetDateTime caseCreateDateFrom,
            OffsetDateTime caseCreateDateTo,
            OffsetDateTime caseCreateDateSince,
            List<CaseStatus> caseStatus,
            String logic
    ) {
        Specification<RmaCaseEntity> spec = Specification.where(null);

        if (caseCreateDateFrom != null && caseCreateDateTo != null) {
            Specification<RmaCaseEntity> dateRangeSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("caseCreatedDate"), caseCreateDateFrom, caseCreateDateTo);
            spec = combine(spec, dateRangeSpec, logic);
        }

        if (caseCreateDateSince != null) {
            Specification<RmaCaseEntity> sinceSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("caseCreatedDate"), caseCreateDateSince);
            spec = combine(spec, sinceSpec, logic);
        }

        if (caseStatus != null && !caseStatus.isEmpty()) {
            Specification<RmaCaseEntity> statusSpec = (root, query, criteriaBuilder) -> {
                CriteriaBuilder.In<CaseStatus> inClause = criteriaBuilder.in(root.get("caseStatus"));
                for (CaseStatus status : caseStatus) {
                    inClause.value(status);
                }
                return inClause;
            };
            spec = combine(spec, statusSpec, logic);
        }

        return spec;
    }

    private static Specification<RmaCaseEntity> combine(
            Specification<RmaCaseEntity> spec1,
            Specification<RmaCaseEntity> spec2,
            String logic
    ) {
        if (spec1 == null) {
            return spec2;
        }
        if ("OR".equalsIgnoreCase(logic)) {
            return spec1.or(spec2);
        } else { // Default to AND
            return spec1.and(spec2);
        }
    }
}