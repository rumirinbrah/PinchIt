package com.zzz.pinchit.core.presentation.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun hasExternalStoragePermission(context: Context):Boolean{
    return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
         true
    }
    else{
         ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    }
}
fun requestExternalStoragePermission(context: Context){
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        0
    )
}