package jp.gr.java_conf.miwax.kioskexample

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by tomoya0x00 on 2017/10/23.
 */

class AdminReceiver: DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        KioskUtils(context).setLockTaskPackage()
    }
}