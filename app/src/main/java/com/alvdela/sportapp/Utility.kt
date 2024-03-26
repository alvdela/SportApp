package com.alvdela.sportapp

import android.animation.ObjectAnimator
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.core.content.ContextCompat

object Utility {

    /* FUNCIONES DE ANIMACION Y CAMBIOs DE ATRIBUTOS */
    fun setHeightLinearLayout(ly: LinearLayout, value: Int){
        val params: LinearLayout.LayoutParams = ly.layoutParams as LayoutParams
        params.height = value
        ly.layoutParams = params
    }

    fun animateViewOfInt(v: View, attr: String, value: Int, time: Long){
        ObjectAnimator.ofInt(v,attr,value).apply {
            duration = time
            start()
        }
    }

    fun animateViewOfFloat(v: View, attr: String, value: Float, time: Long){
        ObjectAnimator.ofFloat(v,attr,value).apply {
            duration = time
            start()
        }
    }

    fun getSecondsFromWatch(watch: String): Int{
        var secs = 0
        var w: String = watch
        if (w.length == 5) w = "00:$w"

        secs += w.subSequence(0,2).toString().toInt() * 3600
        secs += w.subSequence(3,5).toString().toInt() * 60
        secs += w.subSequence(6,8).toString().toInt()

        return secs
    }
}