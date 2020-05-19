package com.blackcharm.SearchTransformer

import androidx.viewpager.widget.ViewPager
import android.view.View

class DepthPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        var alpha = 0f
        if (0 <= position && position <= 1) {
            alpha = 1 - position
        } else if (-1 < position && position < 0) {
            alpha = position + 1
        }
        page.setAlpha(alpha)
        page.setTranslationX(page.getWidth() * -position)
        val yPosition = position * page.getHeight()
        page.setTranslationY(yPosition)
    }
}