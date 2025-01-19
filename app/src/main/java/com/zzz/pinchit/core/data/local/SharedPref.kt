package com.zzz.pinchit.core.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE

object SharedPref {
    private const val DIALOG = "dialog"
    private fun setDialog(context: Context){
        val pref = context.getSharedPreferences(DIALOG,MODE_PRIVATE)
        val editor = pref.edit()
            .putBoolean("flag",true)
            .apply()

    }
    fun getFlagStatus(context: Context) : Boolean{
        val status = context.getSharedPreferences(DIALOG, MODE_PRIVATE).getBoolean("flag",false)
        if(!status){
            setDialog(context)
        }
        return status
    }
}