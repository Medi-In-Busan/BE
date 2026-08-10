package com.mediinbusan.backend.common;

import com.mediinbusan.backend.hospital.domain.Coordinates;

/** 두 좌표 간 대권거리(Haversine) 계산. hospital/wellness 양쪽에서 공유한다. */
public final class GeoDistance {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private GeoDistance() {
    }

    public static Double meters(Coordinates from, Coordinates to) {
        if (from == null || to == null
            || from.getLatitude() == null || from.getLongitude() == null
            || to.getLatitude() == null || to.getLongitude() == null) {
            return null;
        }

        double fromLat = Math.toRadians(from.getLatitude());
        double toLat = Math.toRadians(to.getLatitude());
        double deltaLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double deltaLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
            + Math.cos(fromLat) * Math.cos(toLat)
            * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
