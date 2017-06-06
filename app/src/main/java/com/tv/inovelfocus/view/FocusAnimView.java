package com.tv.inovelfocus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.tv.inovelfocus.R;

/**
 * 功能描述：对焦动画视图
 * 开发状况：正在开发中
 * 开发作者：黎丝军
 * 开发时间：2017/5/19- 10:14
 */

public class FocusAnimView extends View {

    //视图宽度
    private int mWidth = 500;
    //视图高度
    private int mHeight = 500;
    //外旋转角度
    private int mOutLoopAngle;
    //用于绘制外环
    private Matrix mOutLoopMatrix;
    //外环
    private Bitmap mOutLoopBitmap;
    //绘制笔
    private Paint mPaint;
    //绘制文本
    private String mText;
    //增量
    private int mIncrement = 5;
    //动画线程
    private RunAnimThread mAnimThread;
    //是否中断线程
    private boolean isInterrupted = false;
    //动画是否完成
    private boolean isAnimFinish = false;

    public FocusAnimView(Context context) {
        this(context, null);
    }

    public FocusAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mText = "INOVEL";

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mOutLoopMatrix = new Matrix();

        mOutLoopBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_focus_bg);
        mOutLoopAngle = 0;

        initBitmaps();
    }

    /**
     * 初始化需要绘制的图
     */
    private void initBitmaps() {
        mOutLoopBitmap = getFitBitmap(mOutLoopBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mOutLoopMatrix.setRotate(mOutLoopAngle, mWidth * 0.5f, mHeight * 0.5f);
        mOutLoopMatrix.preTranslate(53, 50);
        canvas.drawBitmap(mOutLoopBitmap, mOutLoopMatrix , mPaint);
        drawTextCenter(canvas);
    }

    /**
     * 绘制文本到中心位置
     * @param canvas 画布
     */
    private void drawTextCenter(Canvas canvas) {
        mPaint.setTextSize(90f);
        final Rect bounds = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), bounds);
        canvas.drawText(mText,(getMeasuredWidth() - bounds.width()) * 0.5f, (getMeasuredHeight() + bounds.height()) * 0.5f,mPaint);
    }

    /**
     * 开始动画
     * @param allTimeMillis 全部时间，单位毫秒
     * @param reverseTime 反转时间，单位秒
     */
    public void startAnim(final long allTimeMillis, final int reverseTime) {
        isAnimFinish = false;
        startLeftRotateAnim();
        final CountDownTimer countDownTimer = new CountDownTimer(allTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished / 1000 == reverseTime) {
                    startRightRotateAnim();
                }
            }

            @Override
            public void onFinish() {
                stopAnim();
            }
        };
        countDownTimer.start();
    }

    /**
     * 设置需要的文本
     * @param text 文本
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * 设置需要的文本
     * @param resId 资源Id
     */
    public void setText(int resId) {
        mText = getResources().getString(resId);
    }

    /**
     * 开始左转
     */
    public void startLeftRotateAnim() {
        rotateAnim(1);
    }

    /**
     * 开始左转
     */
    public void startRightRotateAnim() {
        rotateAnim(-1);
    }

    /**
     * 左转
     */
    public void leftRotate() {
        rotate(1);
    }

    /**
     * 右转
     */
    public void rightRotate() {
        rotate(-1);
    }

    /**
     * 手动转动
     * @param dir 方向
     */
    private void rotate(int dir) {
        mOutLoopAngle += dir;
        postInvalidate();
    }

    /**
     * 旋转动画
     * @param dir 方向
     */
    private void rotateAnim(int dir) {
        stopThread();
        mIncrement = dir;
        isInterrupted = false;
        mAnimThread = new RunAnimThread();
        mAnimThread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        initBitmaps();
    }

    /**
     * 停止线程
     */
    private void stopThread() {
        isInterrupted = true;
        if(mAnimThread != null) {
            mAnimThread.interrupt();
            mAnimThread = null;
        }
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        stopAnim(false);
    }

    /**
     * 停止动画
     */
    public void stopAnim(boolean isInterrupted) {
        stopThread();
        if(!isInterrupted) {
            new RecoverThread().start();
        }
    }

    /**
     * 按照视图大小来获取适合的图片
     * @param source 源图片
     * @return 返回新的图片
     */
    private Bitmap getFitBitmap(Bitmap source) {
        final int width = source.getWidth();
        final int height = source.getHeight();
        final float sx = (float)(mWidth - 103) / width;
        final float sy = (float)(mHeight - 100) / height;
        final Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
    }

    /**
     * 动画线程
     */
    class RunAnimThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                mOutLoopAngle += mIncrement;
                postInvalidate();
            }
        }
    }

    /**
     * 复原线程
     */
    class RecoverThread extends Thread {
        @Override
        public void run() {
            while (mOutLoopAngle != 0) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                if(mOutLoopAngle < 0) {
                    mOutLoopAngle = mOutLoopAngle + 1;
                }
                if(mOutLoopAngle > 0) {
                    mOutLoopAngle = mOutLoopAngle - 1;
                }
                postInvalidate();
            }
            isAnimFinish = true;
        }
    }

    /**
     * 动画是否播放完毕
     * @return true表示完成，否则没有完成
     */
    public boolean isAnimFinish() {
        return isAnimFinish;
    }
}
