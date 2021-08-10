package com.wizl.lookalike.logick

import android.content.Context
import com.wizl.lookalike.App

class UserPersisten private constructor() {

    companion object {
        private val mPref =
            App.instance.baseContext.getSharedPreferences("Beauty", Context.MODE_PRIVATE)

        var session: Long
            get() = mPref.getLong(Key.SESSION.key, 0)
            set(value) {
                val edit = mPref.edit()
                synchronized(Key.SESSION) {
                    edit.putLong(Key.SESSION.key, value)
                    edit.commit()
                }
            }

        var isPrem: Boolean
            get() = mPref.getBoolean(Key.IS_PREM.key, false)
            set(value) {
                val edit = mPref.edit()
                synchronized(Key.IS_PREM) {
                    edit.putBoolean(Key.IS_PREM.key, value)
                    edit.commit()
                }
            }

    }

    private enum class Key(val key: String) {
        SESSION("SESSION"),
        IS_PREM("IS_PREM")
    }

}