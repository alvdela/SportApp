package com.alvdela.sportapp

import java.util.regex.Matcher
import java.util.regex.Pattern


class ValidateEmail {
    companion object{
        fun isEmail(email: String): Boolean {
            val pattern = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$")
            val matcher = pattern.matcher(email)
            return matcher.find()
        }
    }
}