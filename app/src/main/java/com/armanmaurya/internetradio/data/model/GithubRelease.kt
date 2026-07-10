package com.armanmaurya.internetradio.data.model

import androidx.annotation.Keep

@Keep
data class GithubRelease(
    val tag_name: String,
    val html_url: String,
    val body: String
)
