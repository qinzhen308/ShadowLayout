package com.paulz.shadowlayout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.RequiresApi;

/**
 * Created by Paul Z on 2019/12/13.
 * Description:
 */
@RequiresApi(17)
public class ShadowLayoutApi17Impl extends ShadowLayoutBaseImpl {


    @Override
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper =
                new RoundRectDrawableWithShadow.RoundRectHelper() {
                    @Override
                    public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                                              Paint paint) {
                        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
                    }
                };

    }
}
