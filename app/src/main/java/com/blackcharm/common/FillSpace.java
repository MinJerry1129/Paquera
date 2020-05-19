package com.blackcharm.common;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import static com.blackcharm.common.UtilsKt.scaleBitmap;

public class FillSpace implements Transformation {


    @Override
    public Bitmap transform(Bitmap toTransform) {

        return scaleBitmap(toTransform);
    }

    @Override
    public String key() {
        return "square()";
    }
}