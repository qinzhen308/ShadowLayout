前面在[CardView简析](https://segmentfault.com/a/1190000021770276)中简要讲了**CardView**的基本属性和其实现逻辑，本文基于CardView的实现原理，并基于**CardView**的源码进行修改，实现对**CardView**阴影效果的修改。
首先需要明确的一点，**CardView**的阴影效果是没提供api进行修改的，只能通过***cardElevation***改变投影深度。
###### 分析原因
**CardView**是通过**CardViewImpl**来静态代理圆角和阴影绘制以及受他们影响的padding处理逻辑的，先来看看这段代码：
```
public class CardView extends FrameLayout {
    ...
    private static final CardViewImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new CardViewApi21Impl();
        } else if (Build.VERSION.SDK_INT >= 17) {
            IMPL = new CardViewApi17Impl();
        } else {
            IMPL = new CardViewBaseImpl();
        }
        IMPL.initStatic();
    }
    ...
}
```
从代码中我们了解到这代理接口是在**CardView**的类加载的时候初始化的，并且是根据安卓版本不同创建了其不同的实现类。
那接下来看看这个接口到底提供了哪些方法：
```
interface CardViewImpl {
    //初始化方法，里面逻辑主要是创建Drawable并设置为CardView的背景
    void initialize(CardViewDelegate cardView, Context context, ColorStateList backgroundColor,
    float radius, float elevation, float maxElevation);
    //设置圆角半径
    void setRadius(CardViewDelegate cardView, float radius);

    float getRadius(CardViewDelegate cardView);

    //设置阴影高度
    void setElevation(CardViewDelegate cardView, float elevation);

    float getElevation(CardViewDelegate cardView);

    //全局内容的初始化，主要是针对不同安卓版本下的RoundRectDrawableWithShadow.sRoundRectHelper进行初始化
    void initStatic();

    void setMaxElevation(CardViewDelegate cardView, float maxElevation);

    float getMaxElevation(CardViewDelegate cardView);

    float getMinWidth(CardViewDelegate cardView);

    float getMinHeight(CardViewDelegate cardView);

    void updatePadding(CardViewDelegate cardView);

    void onCompatPaddingChanged(CardViewDelegate cardView);

    void onPreventCornerOverlapChanged(CardViewDelegate cardView);

    void setBackgroundColor(CardViewDelegate cardView, @Nullable ColorStateList color);

    ColorStateList getBackgroundColor(CardViewDelegate cardView);
}
```
这里主要关注以下两个方法：
1. ***initStatic*** 这个方法是在CardView创建CardViewImpl对象的静态代码块中调用的。
2. ***initialize*** 这个方法实在CardView的构造方法中调用，主要逻辑是CardView将他被代理过程中需要用到的对象传入代理对象中，并初始化，即创建RoundRectDrawableWithShadow/RoundRectDrawable，设置阴影相关参数。
下面分别对比下**CardViewBaseImpl**、**CardViewApi17Impl**和**CardViewApi21Impl**之间这两个方法的实现到底有何不同。

```
class CardViewBaseImpl implements CardViewImpl {
    ...
    @Override
    public void initStatic() {
        // Draws a round rect using 7 draw operations. This is faster than using
        // canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
        // shapes.
        RoundRectDrawableWithShadow.sRoundRectHelper =
                new RoundRectDrawableWithShadow.RoundRectHelper() {
            @Override
            public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                    Paint paint) {
                final float twoRadius = cornerRadius * 2;
                final float innerWidth = bounds.width() - twoRadius - 1;
                final float innerHeight = bounds.height() - twoRadius - 1;
                if (cornerRadius >= 1f) {
                    // increment corner radius to account for half pixels.
                    float roundedCornerRadius = cornerRadius + .5f;
                    mCornerRect.set(-roundedCornerRadius, -roundedCornerRadius, roundedCornerRadius,
                            roundedCornerRadius);
                    int saved = canvas.save();
                    canvas.translate(bounds.left + roundedCornerRadius,
                            bounds.top + roundedCornerRadius);
                    canvas.drawArc(mCornerRect, 180, 90, true, paint);
                    canvas.translate(innerWidth, 0);
                    canvas.rotate(90);
                    canvas.drawArc(mCornerRect, 180, 90, true, paint);
                    canvas.translate(innerHeight, 0);
                    canvas.rotate(90);
                    canvas.drawArc(mCornerRect, 180, 90, true, paint);
                    canvas.translate(innerWidth, 0);
                    canvas.rotate(90);
                    canvas.drawArc(mCornerRect, 180, 90, true, paint);
                    canvas.restoreToCount(saved);
                    //draw top and bottom pieces
                    canvas.drawRect(bounds.left + roundedCornerRadius - 1f, bounds.top,
                            bounds.right - roundedCornerRadius + 1f,
                            bounds.top + roundedCornerRadius, paint);

                    canvas.drawRect(bounds.left + roundedCornerRadius - 1f,
                            bounds.bottom - roundedCornerRadius,
                            bounds.right - roundedCornerRadius + 1f, bounds.bottom, paint);
                }
                // center
                canvas.drawRect(bounds.left, bounds.top + cornerRadius,
                        bounds.right, bounds.bottom - cornerRadius , paint);
            }
        };
    }

    @Override
    public void initialize(CardViewDelegate cardView, Context context,
            ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
        RoundRectDrawableWithShadow background = createBackground(context, backgroundColor, radius,
                elevation, maxElevation);
        background.setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        cardView.setCardBackground(background);
        updatePadding(cardView);
    }
    ...
}
```
```
@RequiresApi(17)
class CardViewApi17Impl extends CardViewBaseImpl {

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
```
```
@RequiresApi(21)
class CardViewApi21Impl implements CardViewImpl {
    ...

    @Override
    public void initialize(CardViewDelegate cardView, Context context,
                ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
        final RoundRectDrawable background = new RoundRectDrawable(backgroundColor, radius);
        cardView.setCardBackground(background);

        View view = cardView.getCardView();
        view.setClipToOutline(true);
        view.setElevation(elevation);
        setMaxElevation(cardView, maxElevation);
    }
    @Override
    public void initStatic() {
    }
    ...
}
```
由上面的代码得知，安卓L以上，是以**RoundRectDrawable**来实现圆角，**View**.*setElevation*来提供阴影，Elevation在安卓5.0以上的所有View中都支持，直接由**RenderNode**渲染阴影。而在L以下，就只能通过Canvas绘制出阴影，所以**RoundRectDrawableWithShadow**就是为了满足圆角和阴影而出现的。***initStatic***中对RoundRectDrawableWithShadow.sRoundRectHelper的实现不同，***drawRoundRect***是在**RoundRectDrawableWithShadow**的***draw***方法里面调用的
```
class RoundRectDrawableWithShadow extends Drawable {
    @Override
    public void draw(Canvas canvas) {
        if (mDirty) {
            buildComponents(getBounds());
            mDirty = false;
        }
        canvas.translate(0, mRawShadowSize / 2);
        drawShadow(canvas);
        canvas.translate(0, -mRawShadowSize / 2);
        sRoundRectHelper.drawRoundRect(canvas, mCardBounds, mCornerRadius, mPaint);
    }
}
```
由此得知，CardView的阴影效果是应用层的统一效果，并未开放扩展和修改。那么既然分析清楚原因了，且清楚了CardView是在什么地方实现阴影的，那我们就自己可以进行修改。有两种方式可以修改这个效果，一种是通过反射替换**CardView**.IMPL，另一种方法，就是直接参照CardView的源码修改。当然，两种方法都需要自己实现**CardViewImpl**。
###### 实现自定义阴影
先来看看对比图：
![CardView和ShadowLayout对比图](/img/bVbDUZ4)
具体实现思路就是通过自定义**CardViewImpl**从而改变**CardView**阴影实现逻辑。下面我就阐述下我自己的实现简要过程。
我同样是通过代理模式，来实现对**CardView**的绘制管理，***ShadowLayoutImpl***对应**CardViewImpl**
```
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

```

由于我们不需要安卓L或CardView提供的阴影效果。所以**ShadowLayoutImpl**只实现了**ShadowLayoutBaseImpl**和**ShadowLayoutApi17Impl**。它们的区别只是在于对于圆角的实现，因为**Canvas**.*drawRoundRect(bounds, cornerRadius, cornerRadius, paint)* 只在SDK>=17才有。它们都同样使用了**RoundRectDrawableWithShadow**，下面贴出实现阴影的绘制逻辑：
```
public class RoundRectDrawableWithShadow extends Drawable {
    ...
    @Override
    public void draw(Canvas canvas) {
        if (mDirty) {
            buildComponents(getBounds());
            mDirty = false;
        }
//        canvas.translate(0, mRawShadowSize / 2);
        drawShadow(canvas);
//        canvas.translate(0, -mRawShadowSize / 2);
        sRoundRectHelper.drawRoundRect(canvas, mCardBounds, mCornerRadius, mPaint);
    }

    private void drawShadow(Canvas canvas) {

        final float edgeShadowTop = -mCornerRadius - mShadowSize;
//        final float inset = 0;
        final float inset = mCornerRadius + mInsetShadow ;
        final boolean drawHorizontalEdges = mCardBounds.width() - 2 * inset > 0;
        final boolean drawVerticalEdges = mCardBounds.height() - 2 * inset > 0;
        int saved=0;
        // LT
        saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.top + inset);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.width() - 2 * inset, -mCornerRadius,
                    mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // RB
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.bottom - inset);
        canvas.rotate(180f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.width() - 2 * inset, -mCornerRadius ,
                    mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // LB
        saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.bottom - inset);
        canvas.rotate(270f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // RT
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.top + inset);
        canvas.rotate(90f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
    }

    private void buildShadowCorners() {
        RectF innerBounds = new RectF(-mCornerRadius, -mCornerRadius, mCornerRadius, mCornerRadius);
        RectF outerBounds = new RectF(innerBounds);
        outerBounds.inset(-mShadowSize, -mShadowSize);

        if (mCornerShadowPath == null) {
            mCornerShadowPath = new Path();
        } else {
            mCornerShadowPath.reset();
        }
        mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        mCornerShadowPath.moveTo(-mCornerRadius, 0);
        mCornerShadowPath.rLineTo(-mShadowSize, 0);
        // outer arc
        mCornerShadowPath.arcTo(outerBounds, 180f, 90f, false);
        // inner arc
        mCornerShadowPath.arcTo(innerBounds, 270f, -90f, false);
        mCornerShadowPath.close();
        float startRatio = mCornerRadius / (mCornerRadius + mShadowSize)*.2f;
        mCornerShadowPaint.setShader(new RadialGradient(0, 0, mCornerRadius + mShadowSize,
                new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                new float[]{0f, startRatio, 1f},
                Shader.TileMode.CLAMP));

        // we offset the content shadowSize/2 pixels up to make it more realistic.
        // this is why edge shadow shader has some extra space
        // When drawing bottom edge shadow, we use that extra space.
        mEdgeShadowPaint.setShader(new LinearGradient(0, -mCornerRadius + mShadowSize, 0,
                -mCornerRadius - mShadowSize,
                new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                new float[]{0f, .1f, 1f}, Shader.TileMode.CLAMP));
        mEdgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        // Card is offset SHADOW_MULTIPLIER * maxShadowSize to account for the shadow shift.
        // We could have different top-bottom offsets to avoid extra gap above but in that case
        // center aligning Views inside the CardView would be problematic.
        final float verticalOffset = mRawMaxShadowSize * SHADOW_MULTIPLIER;
        mCardBounds.set(bounds.left + mRawMaxShadowSize, bounds.top + verticalOffset,
                bounds.right - mRawMaxShadowSize, bounds.bottom - verticalOffset);
        buildShadowCorners();
    }
    ...
}
```
自定义参数和**CardView**差不多，所以使用方法也差不多。
attrs.xml:
```
    <declare-styleable name="ShadowLayout">
        <attr name="android_minWidth" format="dimension" />
        <attr name="android_minHeight" format="dimension" />
        <attr name="cardCornerRadius" format="dimension" />
        <attr name="cardElevation" format="dimension" />
        <attr name="cardMaxElevation" format="dimension" />
        <attr name="cardBackgroundColor" format="color" />
        <attr name="cardPreventCornerOverlap" format="boolean" />
        <attr name="cardUseCompatPadding" format="boolean" />
        <attr name="contentPadding" format="dimension" />
        <attr name="contentPaddingBottom" format="dimension" />
        <attr name="contentPaddingLeft" format="dimension" />
        <attr name="contentPaddingRight" format="dimension" />
        <attr name="contentPaddingTop" format="dimension" />
        <attr name="cardShadowStartColor" format="color" />
        <attr name="cardShadowEndColor" format="color" />

    </declare-styleable>

```
到这里，本文差不多结束了.