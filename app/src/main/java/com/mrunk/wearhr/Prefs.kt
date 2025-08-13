package com.mrunk.wearhr

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val FILE = "wearhr"
    private const val KEY_URL = "url"
    private const val KEY_JWT = "jwt"

    fun prefs(ctx: Context): SharedPreferences = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    fun getUrl(ctx: Context) = prefs(ctx).getString(KEY_URL, "") ?: ""
    fun getJwt(ctx: Context) = prefs(ctx).getString(KEY_JWT, "") ?: ""
    fun set(ctx: Context, url: String, jwt: String) {
        prefs(ctx).edit().putString(KEY_URL, url.trim()).putString(KEY_JWT, jwt.trim()).apply()
    }
}