package com.joserp.argonavis.AuxFun;

import android.content.Context;
import android.os.Vibrator;

public class VibrationHelper {

    private static Integer estandar = 20;
    private static Integer corta = 10;

    private static Vibrator vibrator;

    public static void initialize(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void vibrate(String duracion) {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(DuracionVibracion(duracion));
        }
    }

    public static Integer DuracionVibracion(String duracion) {
        Integer r;

        switch (duracion) {
            case "estandar":
                r = estandar;
                break;
            case "corta":
                r = corta;
                break;
            default:
                r = corta;
                break;
        }
        return r;
    }
}
