package com.example.rc4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val encryptionBtn = findViewById<Button>(R.id.encryptionBtn)
        val decryptionBtn = findViewById<Button>(R.id.decryptionBtn)

        encryptionBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        decryptionBtn.setOnClickListener {
            val intent = Intent(this, DecryptActivity::class.java)
            startActivity(intent)
        }
    }
}
