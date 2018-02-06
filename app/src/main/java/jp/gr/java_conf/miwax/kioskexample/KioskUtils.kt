package jp.gr.java_conf.miwax.kioskexample

import android.app.Activity
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import java.io.File
import java.io.FileInputStream

/**
 * Created by tomoya0x00 on 2018/01/06.
 * Kiosk関係のユーティリティ
 */

class KioskUtils(private val context: Context) {
    private val deviceAdmin = ComponentName(context, AdminReceiver::class.java)
    private val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    // ホームアプリ化
    fun setHomeActivity(activity: Activity) {
        val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addCategory(Intent.CATEGORY_HOME)
        }

        val home = ComponentName(context, activity::class.java)
        dpm.addPersistentPreferredActivity(deviceAdmin, intentFilter, home)
    }

    // ホームアプリ化解除
    fun resetHomeActivity() =
            dpm.clearPackagePersistentPreferredActivities(deviceAdmin, context.packageName)

    fun hasDeviceOwnerPermission(): Boolean =
            dpm.isAdminActive(deviceAdmin) && dpm.isDeviceOwnerApp(context.packageName)

    fun clearDeviceOwner() {
        if (dpm.isDeviceOwnerApp(context.packageName)) {
            dpm.clearDeviceOwnerApp(context.packageName)
        }
    }

    // 自分自身に対してユーザ確認無しのPinningを許可する
    fun setLockTaskPackage() =
            dpm.setLockTaskPackages(deviceAdmin, arrayOf(context.packageName))

    fun start(activity: Activity) {
        activity.startLockTask()

        if (hasDeviceOwnerPermission()) {
            setHomeActivity(activity)
        }
    }

    fun stop(activity: Activity) {
        activity.stopLockTask()

        if (hasDeviceOwnerPermission()) {
            resetHomeActivity()
        }
    }

    fun installPackage(file: File) {
        val packageInstaller = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
            setInstallLocation(PackageInfo.INSTALL_LOCATION_AUTO)
        }

        val sessionId = packageInstaller.createSession(params)
        packageInstaller.openSession(sessionId).use { session ->
            session.openWrite("hoge", 0, file.length()).use { output ->
                FileInputStream(file).use { input ->
                    input.copyTo(output)
                    session.fsync(output)
                }
            }
            val dummySender = PendingIntent.getBroadcast(context,
                    sessionId, Intent("dummy"), 0).intentSender
            session.commit(dummySender)
        }
    }
}
