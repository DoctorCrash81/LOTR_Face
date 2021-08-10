package com.wizl.lookalike.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wizl.lookalike.R
import kotlinx.android.synthetic.main.activity_who_is.*

class WhoIsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_who_is)


        _btHandsome.setOnClickListener{
            val intent = Intent()
            intent.putExtra(LoadActivity.GENDER,true)
            setResult(RESULT_OK,intent)
            finish()
        }

        _btBeauty.setOnClickListener{
            val intent = Intent()
            intent.putExtra(LoadActivity.GENDER,false)
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}