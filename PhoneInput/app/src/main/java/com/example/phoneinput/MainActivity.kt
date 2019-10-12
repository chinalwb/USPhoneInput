package com.example.phoneinput

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.chinalwb.usphoneinput.USPhoneInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        val phoneEdit = findViewById<USPhoneInputEditText>(R.id.phone_edit)
        phoneEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun afterTextChanged(p0: Editable?) {
                var isValid = phoneEdit.isValidUSPhone()
                if (isValid) {
                    phoneEdit.setTextColor(resources.getColor(R.color.colorPrimary))
                } else {
                    phoneEdit.setTextColor(resources.getColor(R.color.colorAccent))
                }
            }
        })
    }
}
