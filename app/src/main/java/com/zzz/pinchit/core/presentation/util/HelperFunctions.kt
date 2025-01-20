package com.zzz.pinchit.core.presentation.util

import android.content.Intent
import android.net.Uri
import android.os.Parcelable

fun getUriFromIntent(intent: Intent) : Uri?{
    val uri = (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)
    return uri
}