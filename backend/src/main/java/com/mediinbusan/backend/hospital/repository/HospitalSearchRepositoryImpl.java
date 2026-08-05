package com.mediinbusan.backend.hospital.repository;

import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import com.mediinbusan.backend.hospital.domain.QHospital;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class HospitalSearchRepositoryImpl implements HospitalSearchRepository {

    private final JPAQueryFactory queryFactory;

    public HospitalSearchRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Hospital> search(String keyword, List<MedicalSpecialty> specialties, Pageable pageable) {
        QHospital hospital = QHospital.hospital;
        BooleanBuilder where = new BooleanBuilder();

        // 주소까지 포함하면 "부산" 같은 흔한 지명이 거의 모든 행을 매칭시켜버려서 병원명(제목)만 검색한다.
        if (StringUtils.hasText(keyword)) {
            where.and(hospital.name.containsIgnoreCase(keyword));
        }
        if (specialties != null && !specialties.isEmpty()) {
            where.and(hospital.specialties.any().in(specialties));
        }

        List<Hospital> content = queryFactory
            .selectFrom(hospital)
            .where(where)
            .orderBy(hospital.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(hospital.count())
            .from(hospital)
            .where(where)
            .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total != null ? total : 0L);
    }
}
