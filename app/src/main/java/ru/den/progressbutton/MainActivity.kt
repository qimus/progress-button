package ru.den.progressbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.den.progressbutton.common.views.ProgressButton

class MainActivity : AppCompatActivity() {
    private lateinit var progressButton: ProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressButton = findViewById(R.id.progress_button)
        configure()
    }

    private fun configure() {
        progressButton.setOnClickListener {
            progressButton.startLoading()
            progressButton.postDelayed({
                progressButton.done()
            }, 2000)
            progressButton.postDelayed({
                progressButton.reset()
            }, 3000)
        }
    }
}