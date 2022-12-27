package com.myprogs.framelauncher

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import com.myprogs.framelauncher.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private val set = SettingsFile()
    private var brightness = 0
    private var folderNames = mutableListOf<String>()
    private var fdIndex = 0
    private var delay = 0
    private var scale = 0


    private lateinit var bind: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(bind.root)
        window.decorView.keepScreenOn
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        setFocusListeners()
        set.start()
        brightness = set.read("Brightness")
        fdIndex = set.read("Folder")
        scale = set.read("Scale")
        delay = set.read("Delay")
        set.getFolders().forEach {
            folderNames.add(it.value)
        }
        setParameters()
    }

    fun startShow(view: View) {
        set.save()
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val keyText = "Key code: $keyCode"
        bind.keyCodeView.text = keyText
        if (bind.Speed.isFocused) {
            when (keyCode) {
                21 -> if (delay > 60) delay -= 60
                22 -> delay += 60
            }
        }
        if (bind.Brightness.isFocused) {
            when (keyCode) {
                21 -> if (brightness > 0) brightness -= 5
                22 -> if (brightness < 255) brightness += 5
            }
            Settings.System.putInt(
                this.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness
            )
        }
        if (bind.Folder.isFocused) {
            when (keyCode) {
                21 -> if (fdIndex > 0) fdIndex-- else fdIndex = folderNames.size - 1
                22 -> if (fdIndex < folderNames.size - 1) fdIndex++ else fdIndex = 0
            }
        }
        if (bind.Scale.isFocused) {
            when (keyCode) {
                21 -> if (scale > 0) scale-- else scale = 2
                22 -> if (scale < 2) scale++ else scale = 0
            }
        }
        setParameters()
        return false
    }

    private fun setFocusListeners() {
        bind.apply {
            StartShow.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                if (b) {
                    Marker1.visibility = View.VISIBLE
                } else Marker1.visibility = View.INVISIBLE
            }
            Brightness.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                if (b) {
                    Marker2.visibility = View.VISIBLE
                } else Marker2.visibility = View.INVISIBLE
            }
            Folder.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                if (b) {
                    Marker3.visibility = View.VISIBLE
                } else Marker3.visibility = View.INVISIBLE
            }
            Speed.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                if (b) {
                    Marker4.visibility = View.VISIBLE
                } else Marker4.visibility = View.INVISIBLE
            }
            Scale.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                if (b) {
                    Marker5.visibility = View.VISIBLE
                } else Marker5.visibility = View.INVISIBLE
            }
        }
    }

    private fun setParameters() {
        bind.apply {
            brightnessValView.text = brightness.toString()

            folderView.text = folderNames[fdIndex]

            val scaleTypes = listOf("без изменений", "вписать", "обрезать")
            scaleView.text = scaleTypes[scale]

            val delayText = (delay / 60).toString() + " мин."
            delayView.text = delayText
        }
        set.update("Brightness", brightness)
        set.update("Folder", fdIndex)
        set.update("Delay", delay)
        set.update("Scale", scale)
    }
}