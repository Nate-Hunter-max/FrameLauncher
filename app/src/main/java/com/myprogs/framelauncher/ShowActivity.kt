package com.myprogs.framelauncher

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import com.myprogs.framelauncher.databinding.ActivityShowBinding
import java.io.File
import java.io.IOException

class ShowActivity : AppCompatActivity() {
    private lateinit var bind: ActivityShowBinding
    private lateinit var slideTime: CountDownTimer
    private var isSlideShow: Boolean = true
    private val set = SettingsFile()
    private var files = listOf<String>()
    private var i = 1
    private var delay: Long = 0
    private var scaleMode = 0
    private val displayMetrics = Point()
    private var screenW = 0
    private var screenH = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.keepScreenOn
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        bind = ActivityShowBinding.inflate(layoutInflater)
        setContentView(bind.root)
        windowManager.defaultDisplay.getRealSize(displayMetrics)
        screenW = displayMetrics.x
        screenH = displayMetrics.y

    }

    override fun onResume() {
        super.onResume()
        try {
            set.start()
        } catch (e: IOException) {
            Thread.sleep(60)
            set.start()
        }
        files = getFiles(set.getFolders().getOrElse(key = set.read("Folder")) {
            throw Exception("Folder [${set.read("Folder")} not found]")
        })
        delay = set.read("Delay") * 1000.toLong()
        scaleMode = set.read("Scale")
        slideTime = object : CountDownTimer(delay, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                slideShow()
            }
        }

        if (!files.isNullOrEmpty()) {
            isSlideShow = true
            slideShow()
        } else
            bind.imageView.setImageResource(R.drawable.empty)
    }

    override fun onPause() {
        super.onPause()
        isSlideShow = false
        slideTime.cancel()
    }

    private fun getFiles(folder: String): List<String> {
        val names = mutableListOf<String>()

        File(folder).walkTopDown().filter { it.isFile }.forEach { names.add(it.absolutePath) }
        return names
    }

    private fun slideShow() {
        if (isSlideShow) {
            changeImg(true, scaleMode)
            slideTime.start()
        }
    }

    private fun changeImg(forward: Boolean, scaleMode: Int) {
        if (forward) {
            if (i < files.size - 1) i++ else i = 0
        } else {
            if (i > 0) i-- else i = files.size - 1
        }
        val img = BitmapFactory.decodeFile(files[i])
        val imgW = img.width
        val imgH = img.height
        val scale = if (imgW > imgH) screenW.toDouble() / imgW else screenH.toDouble() / imgH
        bind.imageView.apply {
            when (scaleMode) {
                0 -> {
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    if (imgW > imgH)
                        setImageBitmap(
                            Bitmap.createScaledBitmap(
                                img,
                                screenW,
                                (imgH * scale).toInt(),
                                true
                            )
                        )
                    else
                        setImageBitmap(
                            Bitmap.createScaledBitmap(
                                img,
                                (imgW * scale).toInt(),
                                screenH,
                                true
                            )
                        )
                }

                1 -> {
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setImageBitmap(Bitmap.createScaledBitmap(img, screenW, screenH, true))
                }

                2 -> {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageBitmap(img)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 66) {//"enter" key
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        if (keyCode == 21) changeImg(false, scaleMode)//"left" key
        if (keyCode == 22) changeImg(true, scaleMode)//"right" key
        if (keyCode == 112) throw Exception("Force stop!") //"delete" key
        return false
    }

}