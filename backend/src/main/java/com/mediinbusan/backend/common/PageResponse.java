package com.mediinbusan.backend.common;

import org.springframework.data.domain.Page;

import java.util.List;

/** Spring Data의 Page를 그대로 응답에 노출하지 않기 위한 경량 래퍼. */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
