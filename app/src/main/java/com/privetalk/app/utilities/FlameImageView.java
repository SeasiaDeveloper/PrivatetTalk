package com.privetalk.app.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.mask.PorterImageView;

/**
 * Created by zeniosagapiou on 12/01/16.
 */
public class FlameImageView extends PorterImageView {

    private Drawable shape;
    private Matrix matrix;
    private Matrix drawMatrix;
    private BitmapDrawable firstShape;
    private Canvas canvas;
    private Rect r;
    private Paint paint;
    private Bitmap bitmapToDraw;

    public FlameImageView(Context context) {
        super(context);
        setup(context, null, 0);
    }

    public FlameImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    public FlameImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, com.github.siyamed.shapeimageview.R.styleable.ShaderImageView, defStyle, 0);
            firstShape = (BitmapDrawable) typedArray.getDrawable(com.github.siyamed.shapeimageview.R.styleable.ShaderImageView_siShape);
            typedArray.recycle();
        }

        bitmapToDraw = Bitmap.createBitmap(firstShape.getIntrinsicWidth(), firstShape.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmapToDraw);

        r = new Rect(0, 0, bitmapToDraw.getWidth(), bitmapToDraw.getHeight());

        paint = new Paint();
        paint.setColor(0xFFFFFF);
        canvas.drawRect(r.left, r.top, r.right, r.bottom, paint);

        shape = new BitmapDrawable(bitmapToDraw);
        matrix = new Matrix();

    }

    public void changeHotness(float hotness) {

        float valueInPx = hotness * (float)r.bottom;

        Rect drawR = new Rect(r.left, r.bottom - (int)valueInPx, r.right, r.bottom);
        canvas.drawRect(r.left, r.top, r.right, r.bottom, paint);
        canvas.drawBitmap(firstShape.getBitmap(), drawR, drawR, null);
        shape = new BitmapDrawable(bitmapToDraw);
        onSizeChanged(getWidth(), getHeight(), 0xFF, 0xFF);

    }

    @Override
    protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {

        if (shape != null) {
            if (shape instanceof BitmapDrawable) {
                configureBitmapBounds(width, height);
                if (drawMatrix != null) {
                    int drawableSaveCount = maskCanvas.getSaveCount();
                    maskCanvas.save();
                    maskCanvas.concat(matrix);
                    shape.draw(maskCanvas);
                    maskCanvas.restoreToCount(drawableSaveCount);
                    return;
                }
            }

            shape.setBounds(0, 0, width, height);
            shape.draw(maskCanvas);
        }

        requestLayout();
        invalidate();
    }

    private void configureBitmapBounds(int viewWidth, int viewHeight) {

        drawMatrix = null;
        int drawableWidth = shape.getIntrinsicWidth();
        int drawableHeight = shape.getIntrinsicHeight();
        boolean fits = viewWidth == drawableWidth && viewHeight == drawableHeight;

        if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
            shape.setBounds(0, 0, drawableWidth, drawableHeight);
            float widthRatio = (float) viewWidth / (float) drawableWidth;
            float heightRatio = (float) viewHeight / (float) drawableHeight;
            float scale = Math.min(widthRatio, heightRatio);
            float dx = (int) ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f);
            float dy = (int) ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f);

            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
        }
    }


}
