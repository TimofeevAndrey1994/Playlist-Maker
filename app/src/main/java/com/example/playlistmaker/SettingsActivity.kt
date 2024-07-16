package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back)
        arrowBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        val textViewShare = findViewById<TextView>(R.id.textViewShare)
        textViewShare.setOnClickListener{
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.link_to_course))
                type = "text/plain"
            }
            startActivity(intent)
        }

        val textViewTextToSupport = findViewById<TextView>(R.id.textViewTextToSupport)
        textViewTextToSupport.setOnClickListener{
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.messageToDevelopersSubject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messageToDevelopers))
            startActivity(shareIntent)
        }

        val textViewUserAgreement = findViewById<TextView>(R.id.textViewUserAgreement)
        textViewUserAgreement.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_to_user_agreement)))
            startActivity(intent)
        }

    }
}