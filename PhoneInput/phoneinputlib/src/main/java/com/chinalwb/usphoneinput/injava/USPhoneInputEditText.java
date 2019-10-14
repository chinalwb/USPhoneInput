package com.chinalwb.usphoneinput.injava;

import android.content.Context;
import android.text.*;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * An extension on EditText. Accept digits only. Format the input as US phone number format
 * automatically. (XXX) XXX-XXXX.
 *
 * @author Wenbin
 */
public class USPhoneInputEditText extends AppCompatEditText {

    private static final String BRACKET = "(";
    private boolean ignoreChange = false;
    private boolean isBackspace = false;
    private String beforeChangeString = "";
    private int beforeChangeStart = -1;
    private int beforeChangeCount = -1;
    private PhoneNumberUtil util = PhoneNumberUtil.createInstance(getContext());

    public USPhoneInputEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public USPhoneInputEditText(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init();
    }

    private void init() {
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(14)
        });
        setupListeners();
    }

    private void setupListeners() {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after < count) {
                    isBackspace = true;
                    beforeChangeString = s.toString();
                    beforeChangeStart = start;
                    beforeChangeCount = count;
                } else {
                    isBackspace = false;
                    beforeChangeString = "";
                    beforeChangeStart = -1;
                    beforeChangeCount = -1;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Handle delete
                if (isBackspace
                        && !TextUtils.isEmpty(beforeChangeString)
                        && beforeChangeStart > -1
                        && beforeChangeCount > -1) {
                    if (TextUtils.isEmpty(s)) {
                        return;
                    }
                    if (s.toString().equalsIgnoreCase(BRACKET)) {
                        setText("");
                        return;
                    }
                    String deletedContent = beforeChangeString.substring(
                            beforeChangeStart, beforeChangeStart + beforeChangeCount);
                    String deletedContentWithoutNumbers = deletedContent.replaceAll("[0-9]", "");

                    // There is no numbers being deleted
                    if (deletedContent.length() == deletedContentWithoutNumbers.length()) {
                        isBackspace = false;
                        setText(beforeChangeString);
                        endFocus();
                        return;
                    }

                    // There are some numbers are being deleted
                    String leftPart = beforeChangeString.substring(0, beforeChangeStart);
                    String rightPart = beforeChangeString.substring(beforeChangeStart + beforeChangeCount);
                    String finalString = leftPart + rightPart;
                    finalString = formatAsPhone(finalString);
                    if (TextUtils.isEmpty(finalString)) {
                        return;
                    }
                    setText(finalString);
                    endFocus();
                    return;
                }

                // Handle insert
                if (null == getText()) {
                    return;
                }
                String text = getText().toString();
                int textLength = text.length();
                if (text.endsWith("-") || text.endsWith(" ")) {
                    return;
                }
                try {
                    Phonenumber.PhoneNumber phoneNumber = util.parse(s.toString(), "US");
                    boolean isValid = util.isValidNumber(phoneNumber);
                    if (isValid) {
                        if (!ignoreChange) {
                            ignoreChange = true;
                            String formatResult = formatAsPhone(s.toString());
                            setText(formatResult);
                            endFocus();
                        }
                        return;
                    } else {
                        ignoreChange = false;
                    }
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }

                ignoreChange = false;
                if (textLength == 1) {
                    if (!text.contains("(")) {
                        setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                        setSelection(getText().length());
                    }
                } else if (textLength == 5) {
                    if (!text.contains(")")) {
                        setText(new StringBuilder(text).insert(text.length() - 1, ")").toString());
                        setSelection(getText().length());
                    }
                } else if (textLength == 6) {
                    setText(new StringBuilder(text).insert(text.length() - 1, " ").toString());
                    setSelection(getText().length());
                } else if (textLength == 10) {
                    if (!text.contains("-")) {
                        setText(new StringBuilder(text).insert(text.length() - 1, "-").toString());
                        setSelection(getText().length());
                    }
                } else {
                    String formatResult = formatAsPhone(s.toString());
                    if (null != formatResult && formatResult.equals(s.toString())) {
                        return;
                    }
                    setText(formatResult);
                    setSelection(getText().length());
                }

            }
        });
    }

    private void endFocus() {
        if (null != getText()) {
            setSelection(getText().length());
        }
    }

    private String formatAsPhone(String phoneNumberString) {
        phoneNumberString = phoneNumberString.replaceAll("[^\\d]", "");
        phoneNumberString = phoneNumberString.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
        if (!phoneNumberString.contains(BRACKET)) {
            phoneNumberString = phoneNumberString.replaceFirst("(\\d{3})(\\d+)", "($1) $2");
            if (!phoneNumberString.contains(BRACKET)) {
                phoneNumberString = phoneNumberString.replaceFirst("(\\d+)", "($1");
            }
        }
        return phoneNumberString;
    }

    /**
     * Check if the current content in EditText is a valid US phone.
     *
     * @return true if it is a valid phone number; false otherwise
     */
    public boolean isValidUSPhone() {
        try {
            Phonenumber.PhoneNumber phoneNumber = util.parse(getText(), "US");
            return util.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
