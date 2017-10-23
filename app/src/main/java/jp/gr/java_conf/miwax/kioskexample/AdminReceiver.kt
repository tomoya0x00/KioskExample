package jp.gr.java_conf.miwax.kioskexample

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Created by tomoya0x00 on 2017/10/23.
 */

class AdminReceiver: DeviceAdminReceiver() {
    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
        context?.run {
            val deviceAdmin = ComponentName(this, AdminReceiver::class.java)
            val dpm = this.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            dpm.setLockTaskPackages(deviceAdmin, arrayOf(this.packageName))
        }
    }
}