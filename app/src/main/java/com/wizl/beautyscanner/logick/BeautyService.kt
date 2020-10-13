package com.wizl.beautyscanner.logick

class BeautyService {

    private object Holder {
        var INSTANCE = BeautyService()
    }

    companion object {
        val instance: BeautyService by lazy { Holder.INSTANCE }
    }

}