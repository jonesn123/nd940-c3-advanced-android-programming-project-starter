package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ComplexColorCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra(EXTRA_FILENAME)
        val status = intent.getStringExtra(EXTRA_STATUS)

        when(status) {
            STATUS.SUCCESS.status -> {
                status_result.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            }
            STATUS.FAIL.status -> {
                status_result.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
        }

        file_name_result.text = fileName
        status_result.text = status

        ok_btn.setOnClickListener {
            finish()
        }
    }

    enum class STATUS(val status: String) {
        SUCCESS("success"),
        FAIL("fail")
    }
    companion object {
        private const val EXTRA_FILENAME = "EXTRA_FILENAME"
        private const val EXTRA_STATUS ="EXTRA_STATUS"
        fun newIntent(context: Context, fileName: String, status: STATUS): Intent =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_FILENAME, fileName)
                putExtra(EXTRA_STATUS, status.status)
            }
    }
}
