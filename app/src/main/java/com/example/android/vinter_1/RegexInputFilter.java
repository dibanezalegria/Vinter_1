package com.example.android.vinter_1;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniel Ibanez on 2016-11-24.
 */

/**
 * Not used at the moment. Using textFilter and digits in activity_login.xml instead
 */
public class RegexInputFilter implements InputFilter {

    private Pattern mPattern;

    public RegexInputFilter(String pattern) {
        this(Pattern.compile(pattern));
    }

    public RegexInputFilter(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(RegexInputFilter.class.getSimpleName() +
                    " requires a regex.");
        }

        mPattern = pattern;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(source);
        if (!matcher.matches()) {
            return "";
        }

        return null;
    }
}
