package jp.gr.java_conf.miwax.kioskexample

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import jp.gr.java_conf.miwax.kioskexample.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_START = 100
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var kioskUtils: KioskUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultUncaughtExceptionHandler()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        kioskUtils = KioskUtils(this)

        binding.kioskOnButton.setOnClickListener {
            kioskUtils.start(this)
        }

        binding.kioskOffButton.setOnClickListener {
            kioskUtils.stop(this)
        }

        binding.exceptionButton.setOnClickListener {
            throw Exception("Exception!!")
        }

        binding.clearDeviceOwnerButton.setOnClickListener {
            kioskUtils.clearDeviceOwner()
        }

        binding.installApkButton.setOnClickListener {
            val file = File(getExternalFilesDir(null), "sample.apk")
            try {
                kioskUtils.installPackage(file)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to install: $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        kioskUtils.start(this)
    }

    // キャッチされない例外が発生したらアプリを再起動
    private fun setDefaultUncaughtExceptionHandler() {
        val pendingIntent = PendingIntent.getActivity(
                application.baseContext,
                REQUEST_START,
                Intent(intent),
                PendingIntent.FLAG_CANCEL_CURRENT)
        val origin = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            @Volatile
            private var crashing = false

            override fun uncaughtException(thread: Thread, throwable: Throwable) {
                try {
                    if (crashing) {
                        return
                    }
                    crashing = true
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val triggerAtMillis = System.currentTimeMillis() + 100
                    if (android.os.Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                    }
                } finally {
                    origin.uncaughtException(thread, throwable)
                }
            }
        })
    }
}
