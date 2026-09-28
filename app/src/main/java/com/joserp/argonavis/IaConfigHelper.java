package com.joserp.argonavis;

import android.content.Context;
import android.content.SharedPreferences;

public class IaConfigHelper {

    private static final String PREF_NAME = "ia_config_prefs";
    public static final String KEY_API_KEY = "gemini_api_key";
    public static final String KEY_MODEL_NAME = "gemini_model_name";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveIaConfig(Context context, String apiKey, String modelName) {
        getPreferences(context).edit()
                .putString(KEY_API_KEY, apiKey)
                .putString(KEY_MODEL_NAME, modelName)
                .apply();
    }

    public static String getApiKey(Context context) {
        return getPreferences(context).getString(KEY_API_KEY, "");
    }

    public static String getModelName(Context context) {
        return getPreferences(context).getString(KEY_MODEL_NAME, "gemini-2.5-flash");
    }
}