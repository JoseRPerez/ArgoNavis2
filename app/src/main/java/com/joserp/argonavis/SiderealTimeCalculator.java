package com.joserp.argonavis;

import java.util.Calendar;
import java.util.TimeZone;

public class SiderealTimeCalculator {

    public static float calculateCorrectedLST(float longitude, String timeZoneId) {
        // 1. Obtener hora ACTUAL en UTC (sin conversiones manuales)
        Calendar utcNow = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));

        //prueba cambio de fecha manual para pruebas
        //utcNow.set(2025, 10, 15, 21,15);
        //fin prueba borrar


        // 2. Calcular días desde J2000 (1-Ene-2000 12:00 UTC)
        Calendar j2000 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        j2000.set(2000, Calendar.JANUARY, 1, 12, 0);
        double daysSinceJ2000 = (utcNow.getTimeInMillis() - j2000.getTimeInMillis()) /
                (24.0 * 60 * 60 * 1000);

        // 4. Cálculo LST
        double gst = 18.697374558 + 24.06570982441908 * daysSinceJ2000;
        double lst = (gst % 24 + longitude / 15.0) % 24;
        return (float) (lst < 0 ? lst + 24 : lst);
    }
}