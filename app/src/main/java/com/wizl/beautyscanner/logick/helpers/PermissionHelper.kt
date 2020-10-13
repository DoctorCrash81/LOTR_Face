package com.wizl.beautyscanner.logick.helpers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionHelper {
    companion object {
        /**
         *  проверяем разрешение
         *  @return true - если есть разрешение, false - если нет разрешения
         */
        fun isPermissionGranted(activity: Activity, permission: String): Boolean {
            val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
            return permissionCheck == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }

        fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
            requestPermission(
                activity,
                arrayOf(permission),
                requestCode
            )
        }

    }
}