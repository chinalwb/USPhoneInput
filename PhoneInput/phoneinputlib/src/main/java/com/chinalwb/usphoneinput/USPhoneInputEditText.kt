package com.chinalwb.usphoneinput

import android.content.Context
import android.text.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

class USPhoneInputEditText(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet) {

    private var ignoreChange = false
    private var isBackspace = false
    private var beforeChangeString: String = ""
    private var beforeChangeStart = -1
    private var beforeChangeCount = -1
    private var util = PhoneNumberUtil.createInstance(context)

    init {
        this.inputType = InputType.TYPE_CLASS_NUMBER
        this.filters += InputFilter.LengthFilter(14)
        setupListeners()
    }


    private fun setupListeners() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (after < count) {
                    isBackspace = true
                    beforeChangeString = s.toString()
                    beforeChangeStart = start
                    beforeChangeCount = count
                } else {
                    isBackspace = false
                    beforeChangeString = ""
                    beforeChangeStart = -1
                    beforeChangeCount = -1
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (isBackspace && !TextUtils.isEmpty(beforeChangeString) && beforeChangeStart > -1 && beforeChangeCount > -1) {
                    if (TextUtils.isEmpty(s)) {
                        return
                    }
                    if (s.toString() == "(") {
                        setText("")
                        return
                    }
                    val deletedContent = beforeChangeString.substring(
                        beforeChangeStart,
                        beforeChangeStart + beforeChangeCount
                    )
                    val deletedContentWithoutNumbers =
                        deletedContent.replace("[0-9]".toRegex(), "")

                    // There is no numbers being deleted
                    if (deletedContent.length == deletedContentWithoutNumbers.length) {
                        isBackspace = false
                        setText(beforeChangeString)
                        setSelection(text!!.length)
                        return
                    }

                    // There are some numbers are being deleted
                    val leftPart = beforeChangeString.substring(0, beforeChangeStart)
                    val rightPart =
                        beforeChangeString.substring(beforeChangeStart + beforeChangeCount)
                    var finalString = leftPart + rightPart
                    finalString = formatAsPhone(finalString)
                    if (TextUtils.isEmpty(finalString)) {
                        return
                    }
                    setText(finalString)
                    setSelection(text!!.length)
                    return
                }

                val textLength = text!!.length
                if (text!!.endsWith("-") || text!!.endsWith(" "))
                    return
                try {
                    val phoneNumber = util.parse(s.toString(), "US")
                    val isValid = util.isValidNumber(phoneNumber)
                    if (isValid) {
                        if (!ignoreChange) {
                            ignoreChange = true
                            val formatResult = formatAsPhone(s.toString())
                            setText(formatResult)
                            setSelection(formatResult.length)
                        }
                        return
                    } else {
                        ignoreChange = false
                    }
                } catch (e: NumberParseException) {
                    e.printStackTrace()
                }

                ignoreChange = false
                if (textLength == 1) {
                    if (!text!!.contains("(")) {
                        setText(
                            StringBuilder(text!!).insert(
                                text!!.length - 1,
                                "("
                            ).toString()
                        )
                        setSelection(text!!.length)
                    }
                } else if (textLength == 5) {
                    if (!text!!.contains(")")) {
                        setText(
                            StringBuilder(text!!).insert(
                                text!!.length - 1,
                                ")"
                            ).toString()
                        )
                        setSelection(text!!.length)
                    }
                } else if (textLength == 6) {
                    setText(StringBuilder(text!!).insert(text!!.length - 1, " ").toString())
                    setSelection(text!!.length)
                } else if (textLength == 10) {
                    if (!text!!.contains("-")) {
                        setText(
                            StringBuilder(text!!).insert(
                                text!!.length - 1,
                                "-"
                            ).toString()
                        )
                        setSelection(text!!.length)
                    }
                } else if (textLength == 15) {
                    if (text!!.contains("-")) {
                        setText(
                            StringBuilder(text!!).insert(
                                text!!.length - 1,
                                "-"
                            ).toString()
                        )
                        setSelection(text!!.length)
                    }
                } else if (textLength == 18) {
                    if (text!!.contains("-")) {
                        setText(
                            StringBuilder(text!!).insert(
                                text!!.length - 1,
                                "-"
                            ).toString()
                        )
                        setSelection(text!!.length)
                    }
                } else {
                    val formatResult = formatAsPhone(s.toString())
                    if (formatResult == s.toString()) {
                        return
                    }
                    setText(formatResult)
                    setSelection(formatResult.length)
                }
            }
        })
    }


    private fun formatAsPhone(inputString: String): String {
        var phoneNumberString = inputString
        phoneNumberString = phoneNumberString.replace("[^\\d]".toRegex(), "")
        phoneNumberString =
            phoneNumberString.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "($1) $2-$3")
        val bracket = "("
        if (!phoneNumberString.contains(bracket)) {
            phoneNumberString =
                phoneNumberString.replaceFirst("(\\d{3})(\\d+)".toRegex(), "($1) $2")
            if (!phoneNumberString.contains(bracket)) {
                phoneNumberString = phoneNumberString.replaceFirst("(\\d+)".toRegex(), "($1")
            }
        }
        return phoneNumberString
    }

    fun isValidUSPhone() : Boolean {
        return try {
            val phoneNumber = util.parse(text!!, "US")
            util.isValidNumber(phoneNumber)
        } catch (e: NumberParseException) {
            e.printStackTrace()
            false
        }
    }
}