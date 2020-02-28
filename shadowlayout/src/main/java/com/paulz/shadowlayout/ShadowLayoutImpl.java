package com.paulz.shadowlayout;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.Nullable;

/**
 * Created by Paul Z on 2019/12/13.
 * Description:
 */
public interface ShadowLayoutImpl {

    void initialize(ShadowLayoutDelegate cardView, Context context, ColorStateList backgroundColor,
                    float radius,int shadowStartColor,int shadowEndColor, float elevation, float maxElevation);

    void setRadius(ShadowLayoutDelegate cardView, float radius);

    float getRadius(ShadowLayoutDelegate cardView);

    void setElevation(ShadowLayoutDelegate cardView, float elevation);

    float getElevation(ShadowLayoutDelegate cardView);

    void initStatic();

    void setMaxElevation(ShadowLayoutDelegate cardView, float maxElevation);

    float getMaxElevation(ShadowLayoutDelegate cardView);

    float getMinWidth(ShadowLayoutDelegate cardView);

    float getMinHeight(ShadowLayoutDelegate cardView);

    void updatePadding(ShadowLayoutDelegate cardView);

    void onCompatPaddingChanged(ShadowLayoutDelegate cardView);

    void onPreventCornerOverlapChanged(ShadowLayoutDelegate cardView);

    void setBackgroundColor(ShadowLayoutDelegate cardView, @Nullable ColorStateList color);

    ColorStateList getBackgroundColor(ShadowLayoutDelegate cardView);
}
