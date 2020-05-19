package com.blackcharm.common

import android.graphics.Bitmap
import android.util.Log

fun getTimeString(timeInt: Long): String {
    val nowInt = System.currentTimeMillis()

    //Log.d("testtesttest", "hello wold")

    val minute = 60
    val hour = 60 * minute
    val day = 24 * hour
    val week =  7 * day
    val month = 30 * day
    val year = 12 * month
    var agoString = String()

    var timeInterval = (nowInt - timeInt) / 1000


    if (timeInterval < minute) {
        agoString = "$timeInterval segundos atrás"
    } else if (timeInterval < hour) {
        timeInterval /= minute
        agoString = "$timeInterval minutos atrás"
    } else if (timeInterval < day) {
        timeInterval /= hour
        agoString = "$timeInterval horas atrás"
    } else if (timeInterval < week) {
        timeInterval /= day
        agoString = "Há $timeInterval dias"
    } else if (timeInterval < month) {
        timeInterval /= week
        agoString = "Há $timeInterval semanas"
    } else if (timeInterval < year) {
        timeInterval /= month
        agoString = "$timeInterval meses  atrás"
    } else if (timeInterval > year) {
        timeInterval /= year
        agoString = "Há $timeInterval anos"
    }

    return agoString
}


fun scaleBitmap(bm : Bitmap) :Bitmap {
    var width = bm.getWidth();
    var height = bm.getHeight();

    Log.v("Pictures", "Width and height are " + width + "--" + height);

    if (width > height) {
        // landscape
        val ratio = (width.toFloat() / 600)
        width = 600;
        height = (height / ratio).toInt()
    } else if (height > width) {
        // portrait
        val ratio = (height.toFloat() / 600)
        height = 600;
        width = (width / ratio).toInt()
    } else {
        // square
        height = 600;
        width = 600;
    }

    Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

    val scaled :Bitmap = Bitmap.createScaledBitmap(bm, width, height, true);
    return scaled;
}
