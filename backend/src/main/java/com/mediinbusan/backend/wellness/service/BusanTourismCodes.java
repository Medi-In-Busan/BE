package com.mediinbusan.backend.wellness.service;

import java.util.List;

/**
 * 관광공사 API의 부산 행정구역 코드 단일 출처.
 * 일반 TourAPI는 법정동 코드, 관광 빅데이터 API는 areaCd/signguCd를 사용한다.
 */
public final class BusanTourismCodes {

    public static final String LDONG_REGN_CD = "26";
    public static final String BIGDATA_AREA_CD = "26";

    private BusanTourismCodes() {
    }

    public enum District {
        JUNG("110", "26110"),
        SEO("140", "26140"),
        DONG("170", "26170"),
        YEONGDO("200", "26200"),
        BUSANJIN("230", "26230"),
        DONGNAE("260", "26260"),
        NAM("290", "26290"),
        BUK("320", "26320"),
        HAEUNDAE("350", "26350"),
        SAHA("380", "26380"),
        GEUMJEONG("410", "26410"),
        GANGSEO("440", "26440"),
        YEONJE("470", "26470"),
        SUYEONG("500", "26500"),
        SASANG("530", "26530"),
        GIJANG("710", "26710");

        private final String lDongSignguCd;
        private final String bigdataSignguCd;

        District(String lDongSignguCd, String bigdataSignguCd) {
            this.lDongSignguCd = lDongSignguCd;
            this.bigdataSignguCd = bigdataSignguCd;
        }

        public String lDongSignguCd() {
            return lDongSignguCd;
        }

        public String bigdataSignguCd() {
            return bigdataSignguCd;
        }
    }

    public static List<District> districts() {
        return List.of(District.values());
    }
}
