package com.csucsu.flipendo

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

object ThemeHelper {
    fun applyTheme(activity: AppCompatActivity) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val themeValue = prefs.getString("theme", "light")!!
        val accentColor = prefs.getString("accent_color", "red")!!
        val themeRes = when(themeValue to accentColor) {
            "light" to "blue"   -> R.style.Theme_Flipendo_Light_Blue
            "light" to "green"  -> R.style.Theme_Flipendo_Light_Green
            "light" to "orange" -> R.style.Theme_Flipendo_Light_Orange
            "dark" to "red"   -> R.style.Theme_Flipendo_Dark_Red
            "dark" to "blue"   -> R.style.Theme_Flipendo_Dark_Blue
            "dark" to "green"   -> R.style.Theme_Flipendo_Dark_Green
            "dark" to "orange"   -> R.style.Theme_Flipendo_Dark_Orange
            "amoled" to "red"   -> R.style.Theme_Flipendo_AMOLED_Red
            "amoled" to "blue"   -> R.style.Theme_Flipendo_AMOLED_Blue
            "amoled" to "green"   -> R.style.Theme_Flipendo_AMOLED_Green
            "amoled" to "orange"   -> R.style.Theme_Flipendo_AMOLED_Orange
            else                -> R.style.Theme_Flipendo_Light_Red
        }
        activity.setTheme(themeRes)
    }

    fun toolbarPopupTheme(activity: AppCompatActivity): Int =
        when (PreferenceManager.getDefaultSharedPreferences(activity)
            .getString("theme", "light")) {
            "dark"   -> R.style.Theme_Flipendo_PopupOverlay_Dark
            "amoled" -> R.style.Theme_Flipendo_PopupOverlay_AMOLED
            else     -> R.style.Theme_Flipendo_PopupOverlay
        }

}
