package jp.gr.java_conf.miwax.kioskexample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.gr.java_conf.miwax.kioskexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.kioskOnButton.setOnClickListener {
            this.startLockTask()
        }

        binding.kioskOffButton.setOnClickListener {
            this.stopLockTask()
        }
    }
}
