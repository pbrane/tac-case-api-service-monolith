package com.beaconstrategists.taccaseapiservice.specifications;

import com.beaconstrategists.taccaseapiservice.model.CaseStatus;
import com.beaconstrategists.taccaseapiservice.model.entities.TacCaseEntity;
import org.springframework.data.jpa.domain.Specification;

//import javax.persistence.criteria.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.time.OffsetDateTime;
import java.util.List;

public class TacCaseSpecification {

    public static Specification<TacCaseEntity> buildSpecification(
            OffsetDateTime caseCreateDateFrom,
            OffsetDateTime caseCreateDateTo,
            OffsetDateTime caseCreateDateSince,
            List<CaseStatus> caseStatus,
            String logic
    ) {
        Specification<TacCaseEntity> spec = Specification.where(null);

        if (caseCreateDateFrom != null && caseCreateDateTo != null) {
            Specification<TacCaseEntity> dateRangeSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("caseCreatedDate"), caseCreateDateFrom, caseCreateDateTo);
            spec = combine(spec, dateRangeSpec, logic);
        }

        if (caseCreateDateSince != null) {
            Specification<TacCaseEntity> sinceSpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("caseCreatedDate"), caseCreateDateSince);
            spec = combine(spec, sinceSpec, logic);
        }

        if (caseStatus != null && !caseStatus.isEmpty()) {
            Specification<TacCaseEntity> statusSpec = (root, query, criteriaBuilder) -> {
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

    private static Specification<TacCaseEntity> combine(
            Specification<TacCaseEntity> spec1,
            Specification<TacCaseEntity> spec2,
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