package com.zzz.pinchit.core.presentation

interface UIEvents {

    data object Success : UIEvents
    data class Error(val error : String) : UIEvents

}