package com.paulz.shadowlayout;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Paul Z on 2019/12/13.
 * Description:
 */
public interface ShadowLayoutDelegate {

    void setShadowBackground(Drawable drawable);
    Drawable getShadowBackground();
    boolean getUseCompatPadding();
    boolean getPreventCornerOverlap();
    void setShadowPadding(int left, int top, int right, int bottom);
    void setMinWidthHeightInternal(int width, int height);
    View getShadowLayout();
}
