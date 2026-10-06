package com.aetherfocus.core.model

data class BlockedApp(
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val isDefaultDistraction: Boolean = false
) {
    companion object {
        // Known common distraction packages
        val DEFAULT_DISTRACTION_PACKAGES = setOf(
            "com.zhiliaoapp.musically",          // TikTok
            "com.ss.android.ugc.trill",          // TikTok (regional)
            "com.instagram.android",             // Instagram
            "com.google.android.youtube",        // YouTube
            "com.twitter.android",               // X / Twitter
            "com.facebook.katana",               // Facebook
            "com.reddit.frontpage"               // Reddit
        )
    }
}

