package me.ajax.loadingview.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by aj on 2018/4/2
 */

public class LoadingView extends View {

    Paint linePaint1;
    Paint linePaint2;
    Paint linePaint3;
    RectF rectF = new RectF();

    ValueAnimator circleAnimator;
    ValueAnimator rotateAnimator;

    boolean isStart;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速

        //画笔
        linePaint1 = new Paint();
        linePaint1.setColor(Color.RED);
        linePaint1.setAntiAlias(true);
        linePaint1.setStyle(Paint.Style.STROKE);
        linePaint1.setStrokeWidth(dp2Dx(2));
        linePaint2 = new Paint(linePaint1);
        linePaint2.setColor(Color.YELLOW);
        linePaint3 = new Paint(linePaint1);
        linePaint3.setColor(Color.GREEN);
        linePaint1.setShader(new LinearGradient(
                0, -dp2Dx(50), 0, 0,
                0xFF1DB0B8, 0x441DB0B8, Shader.TileMode.MIRROR));
        linePaint2.setShader(new LinearGradient(
                0, -dp2Dx(50), 0, 0,
                0xAA9966cc, 0x449966cc, Shader.TileMode.MIRROR));
        linePaint3.setShader(new LinearGradient(
                0, -dp2Dx(50), 0, 0,
                0xAAf17c67, 0x44f17c67, Shader.TileMode.MIRROR));

        //初始化动画
        initAnimator();

        //启动动画
        post(new Runnable() {
            @Override
            public void run() {
                circleAnimator.start();
                rotateAnimator.start();
                isStart = true;
            }
        });
    }

    void initAnimator() {

        //圆动画
        circleAnimator = ValueAnimator.ofInt(dp2Dx(50), 0, dp2Dx(50));
        circleAnimator.setDuration(1000);
        circleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        circleAnimator.setRepeatCount(Integer.MAX_VALUE - 1);
        circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidateView();
            }
        });

        //旋转动画
        rotateAnimator = ValueAnimator.ofInt(0, 90);
        rotateAnimator.setDuration(1000);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.setRepeatCount(Integer.MAX_VALUE - 1);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getWidth();
        int mHeight = getHeight();

        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);

        int circleRadius = !isStart ? dp2Dx(50) : (int) circleAnimator.getAnimatedValue();
        rectF.top = -dp2Dx(50);
        rectF.bottom = dp2Dx(50);
        rectF.left = -circleRadius;
        rectF.right = circleRadius;

        int rotateDegrees = !isStart ? 0 : (int) rotateAnimator.getAnimatedValue();

        //绘制
        canvas.drawOval(rectF, linePaint1);
        canvas.rotate(-rotateDegrees);
        canvas.drawOval(rectF, linePaint2);
        canvas.rotate(rotateDegrees);
        canvas.rotate(rotateDegrees);
        canvas.drawOval(rectF, linePaint3);

        canvas.restore();
    }

    int dp2Dx(int dp) {
        //return dp;
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    void l(Object o) {
        Log.e("######", o.toString());
    }


    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //  当前线程是主UI线程，直接刷新。
            invalidate();
        } else {
            //  当前线程是非UI线程，post刷新。
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimAndRemoveCallbacks();
    }

    private void stopAnimAndRemoveCallbacks() {
        isStart = false;
        if (circleAnimator != null) circleAnimator.end();
        if (rotateAnimator != null) rotateAnimator.end();

        Handler handler = this.getHandler();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
