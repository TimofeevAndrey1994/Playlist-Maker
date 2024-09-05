package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            arrowBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            textViewShare.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.link_to_course))
                    type = "text/plain"
                }
                startActivity(intent)
            }

            textViewTextToSupport.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SENDTO)
                shareIntent.data = Uri.parse("mailto:")
                shareIntent.putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.developer_email))
                )
                shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.messageToDevelopersSubject)
                )
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messageToDevelopers))
                startActivity(shareIntent)
            }

            textViewUserAgreement.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.link_to_user_agreement))
                )
                startActivity(intent)
            }

            themeSwitcher.isChecked = (applicationContext as App).darkTheme
            themeSwitcher.setOnCheckedChangeListener { buttonView, isChecked ->
                (applicationContext as App).switchTheme(isChecked)
            }

        }
    }
}