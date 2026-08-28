package com.junior.reminder

import android.Manifest
import android.app.*
import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var textInput: EditText
    private lateinit var timeInput: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 40, 32, 32)
            gravity = Gravity.TOP
        }

        val title = TextView(this).apply {
            text = "جونیور ⏰"
            textSize = 30f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "یادآوری جدید بساز و جونیور در زمان تعیین‌شده بهت اطلاع می‌دهد."
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }
        root.addView(subtitle)

        textInput = EditText(this).apply {
            hint = "متن یادآوری"
            textSize = 18f
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(false)
        }
        root.addView(textInput, LinearLayout.LayoutParams(-1, 140))

        timeInput = TimePicker(this).apply {
            setIs24HourView(true)
            gravity = Gravity.CENTER
        }
        root.addView(timeInput, LinearLayout.LayoutParams(-1, 220))

        val save = Button(this).apply {
            text = "ذخیره یادآوری"
            textSize = 18f
            setOnClickListener { scheduleReminder() }
        }
        root.addView(save, LinearLayout.LayoutParams(-1, 60))

        setContentView(root)
    }

    private fun scheduleReminder() {
        val message = textInput.text.toString().trim()
        if (message.isEmpty()) {
            Toast.makeText(this, "اول متن یادآوری را بنویس.", Toast.LENGTH_SHORT).show()
            return
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeInput.hour)
            set(Calendar.MINUTE, timeInput.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("message", message)
        }
        val pending = PendingIntent.getBroadcast(
            this, message.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        if (android.os.Build.VERSION.SDK_INT >= 31 && !alarm.canScheduleExactAlarms()) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            Toast.makeText(this, "اجازه آلارم دقیق را فعال کن و دوباره ذخیره بزن.", Toast.LENGTH_LONG).show()
            return
        }

        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
        Toast.makeText(this, "یادآوری برای ${timeInput.hour}:${timeInput.minute.toString().padStart(2,'0')} ذخیره شد.", Toast.LENGTH_LONG).show()
    }
}
