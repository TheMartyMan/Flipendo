package com.csucsu.flipendo.settings


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.csucsu.flipendo.R
import com.github.chrisbanes.photoview.BuildConfig

class SettingsFragment : PreferenceFragmentCompat() {


    private var clickCount = 0
    private val requiredClicks = 18
    private val targetUrl = "https://www.youtube.com/watch?v=t-Qju5Fow9I&t=18s"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Téma
        val themePref = findPreference<ListPreference>("theme")
        themePref?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue as String) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            true
        }


        // Kiemelő szín választás
        val accentColorPref = findPreference<ListPreference>("accent_color")
        accentColorPref?.setOnPreferenceChangeListener { _, newValue ->
            val accentColorName = newValue as String
            context?.let {
                PreferenceManager.getDefaultSharedPreferences(it).edit {
                    putString("accent_color", accentColorName)
                    apply()
                }
            }
            // Újraindítás, hogy a MainActivity az új témával induljon
            requireActivity().recreate()
            true
        }

        // Lapozási irány beállítása
        val orientationPref = findPreference<ListPreference>("page_orientation")
        orientationPref?.setOnPreferenceChangeListener { _, newValue ->
            context?.let {
                PreferenceManager.getDefaultSharedPreferences(it).edit {
                    putString("page_orientation", newValue as String)
                    apply()
                }
            }
            true
        }

        // Nyelvválasztás
        findPreference<ListPreference>("language")?.apply {
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            setOnPreferenceChangeListener { pref, newValue ->
                val langTag = newValue as String
                val appLocales = androidx.core.os.LocaleListCompat.forLanguageTags(langTag)
                AppCompatDelegate.setApplicationLocales(appLocales)
                true
            }
        }

        // App erzió lekérése Gradle konfigból
        findPreference<Preference>("app_version")?.also { versionPref ->
            versionPref.summary = BuildConfig.VERSION_NAME
        }

        // Easter Egg
        val versionPref = findPreference<Preference>("app_version")
        versionPref?.setOnPreferenceClickListener {
            clickCount++
            if (clickCount >= requiredClicks) {
                clickCount = 0
                val intent = Intent(Intent.ACTION_VIEW, targetUrl.toUri())
                startActivity(intent)
            }
            true
        }
    }
}
