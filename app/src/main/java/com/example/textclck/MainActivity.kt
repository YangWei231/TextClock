package com.example.textclck

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private var isDarkMode = false
    private var isTomatoRunning = false
    private var isWorking = true // true=工作时间, false=休息时间

    private lateinit var rootLayout: View
    private lateinit var textClock: TextClock
    private lateinit var tomatoStatus: TextView
    private lateinit var tomatoTimer: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button
    private lateinit var toggleThemeButton: Button

    private var countDownTimer: CountDownTimer? = null

    // 番茄钟时间设置（毫秒）
    private val WORK_TIME = 25 * 60 * 1000L  // 25分钟
    private val BREAK_TIME = 5 * 60 * 1000L  // 5分钟
    private var remainingTime = WORK_TIME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取视图引用
        rootLayout = findViewById(R.id.rootLayout)
        textClock = findViewById(R.id.textClock)
        tomatoStatus = findViewById(R.id.tomatoStatus)
        tomatoTimer = findViewById(R.id.tomatoTimer)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)
        toggleThemeButton = findViewById(R.id.toggleThemeButton)

        // 设置时钟点击事件
        textClock.setOnClickListener {
            val currentTime = textClock.text
            Toast.makeText(this, "当前时间: $currentTime", Toast.LENGTH_SHORT).show()
        }

        // 设置番茄钟控制按钮事件
        startButton.setOnClickListener { startTomato() }
        pauseButton.setOnClickListener { pauseTomato() }
        resetButton.setOnClickListener { resetTomato() }

        // 设置主题切换按钮事件
        toggleThemeButton.setOnClickListener {
            toggleTheme()
        }

        // 初始化显示
        updateTimerDisplay(WORK_TIME)
    }

    private fun startTomato() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        isTomatoRunning = true
        updateButtonVisibility()

        // 隐藏正常时钟，显示番茄钟
        textClock.visibility = View.GONE
        tomatoStatus.visibility = View.GONE
        tomatoTimer.visibility = View.VISIBLE

        // 根据当前状态设置倒计时时间
        val timeToCountDown = if (isWorking) WORK_TIME else BREAK_TIME

        // 应用相应的主题
        if (isWorking) {
            setDarkMode()
        } else {
            setLightMode()
        }

        // 创建倒计时器
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
            }

            override fun onFinish() {
                // 倒计时结束，切换状态
                isWorking = !isWorking
                remainingTime = if (isWorking) WORK_TIME else BREAK_TIME

                // 更新状态显示
                tomatoStatus.text = if (isWorking) "工作中" else "休息中"

                // 重新开始倒计时
                startTomato()

                // 显示提示
                Toast.makeText(
                    this@MainActivity,
                    if (isWorking) "工作时间开始！" else "休息时间开始！",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.start()
    }

    private fun pauseTomato() {
        countDownTimer?.cancel()
        isTomatoRunning = false
        updateButtonVisibility()
    }

    private fun resetTomato() {
        countDownTimer?.cancel()
        isWorking = true
        remainingTime = WORK_TIME
        isTomatoRunning = false

        // 恢复正常时钟显示，隐藏番茄钟
        textClock.visibility = View.VISIBLE
        tomatoStatus.visibility = View.VISIBLE
        tomatoTimer.visibility = View.VISIBLE

        tomatoStatus.text = "准备开始番茄钟"
        updateTimerDisplay(WORK_TIME)
        updateButtonVisibility()
        setLightMode()
    }

    private fun updateTimerDisplay(millis: Long) {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        tomatoTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateButtonVisibility() {
        if (isTomatoRunning) {
            startButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        } else {
            startButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
        }
    }

    private fun toggleTheme() {
        if (isTomatoRunning) {
            // 如果番茄钟正在运行，不允许手动切换主题
            Toast.makeText(this, "番茄钟运行中，无法切换主题", Toast.LENGTH_SHORT).show()
            return
        }

        if (isDarkMode) {
            setLightMode()
        } else {
            setDarkMode()
        }
    }

    private fun setDarkMode() {
        rootLayout.setBackgroundColor(android.graphics.Color.BLACK)
        textClock.setTextColor(android.graphics.Color.WHITE)
        tomatoStatus.setTextColor(android.graphics.Color.WHITE)
        tomatoTimer.setTextColor(android.graphics.Color.WHITE)
        startButton.setTextColor(android.graphics.Color.WHITE)
        pauseButton.setTextColor(android.graphics.Color.WHITE)
        resetButton.setTextColor(android.graphics.Color.WHITE)
        toggleThemeButton.setTextColor(android.graphics.Color.WHITE)
        toggleThemeButton.text = "切换到浅色模式"
        isDarkMode = true
    }

    private fun setLightMode() {
        rootLayout.setBackgroundColor(android.graphics.Color.WHITE)
        textClock.setTextColor(android.graphics.Color.BLACK)
        tomatoStatus.setTextColor(android.graphics.Color.BLACK)
        tomatoTimer.setTextColor(android.graphics.Color.BLACK)
        startButton.setTextColor(android.graphics.Color.BLACK)
        pauseButton.setTextColor(android.graphics.Color.BLACK)
        resetButton.setTextColor(android.graphics.Color.BLACK)
        toggleThemeButton.setTextColor(android.graphics.Color.BLACK)
        toggleThemeButton.text = "切换到深色模式"
        isDarkMode = false
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}