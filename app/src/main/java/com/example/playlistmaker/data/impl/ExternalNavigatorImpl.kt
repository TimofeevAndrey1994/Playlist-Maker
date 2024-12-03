package com.example.playlistmaker.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.link_to_course))
            type = "text/plain"
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openLink() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(context.getString(R.string.link_to_user_agreement))
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openEmail() {
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(context.getString(R.string.developer_email))
        )
        shareIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.messageToDevelopersSubject)
        )
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.messageToDevelopers))
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}