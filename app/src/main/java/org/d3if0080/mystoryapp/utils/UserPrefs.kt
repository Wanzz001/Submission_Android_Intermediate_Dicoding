package org.d3if0080.mystoryapp.utils

import android.content.Context
import android.content.SharedPreferences
import hu.autsoft.krate.Krate
import hu.autsoft.krate.booleanPref
import hu.autsoft.krate.default.withDefault
import hu.autsoft.krate.stringPref

class UserPrefs(context: Context) : Krate {
    override val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var isLoggedIn by booleanPref().withDefault(false)
    var token by stringPref().withDefault("")

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}