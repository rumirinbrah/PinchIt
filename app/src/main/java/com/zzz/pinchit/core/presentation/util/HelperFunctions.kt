package com.zzz.pinchit.core.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable

fun getUriFromIntent(intent: Intent) : Uri?{
    val uri = (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)
    return uri
}

fun isPdfSizeAllowed(uri: Uri,context: Context):Boolean{
    val length = context.contentResolver.openAssetFileDescriptor(uri,"r")
        ?.use {
            it.length
        }
    return if(length==null){
        false
    }
    else{
        length < 21000000
    }
}