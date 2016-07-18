package com.simple.canvas.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>描述：自定义动画采用SurfaceView实现 方式二</p>
 *
 * @author ~若相惜
 * @version v2.0
 * @date 2016-7-18 上午10:34:51
 */
public class CanvasSurfaceView extends g implements SurfaceHolder.Callback {
    public Paint mPaint = new Paint();
    private int mTotalWidth;
    private int mTotalHeight;
    private int mCenterX ;
    private int mCenterY;
    private RectF oval;
    private int padding=120;
    private int position =0;
    private float currentValue =0;
    private int count=0;
    private List<Float> mlist;
    private SurfaceHolder mSurfaceHolder;
    private UIThread uiThread;
    private  Random mRandom = new Random();
    private  float[] volatility={0.010f,0.015f,0.020f,0.025f,0.030f,0.035f};
    public CanvasSurfaceView(Context context) {
       super(context);
        init();
    }
    public CanvasSurfaceView(Context context, AttributeSet attrs) {
       super(context, attrs);
        init();
    }

    public CanvasSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mSurfaceHolder = getHolder(); 
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);//设置画布背景透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        this.mPaint.setColor(Color.parseColor("#ffffff"));
        this.mPaint.setAlpha(80);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);

        // 防抖动  
        this.mPaint.setDither(true);
        // 开启图像过滤  
        this.mPaint.setFilterBitmap(true);
        // 设置外围模糊效果  
        this.mPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
        oval = new RectF(0, 0, mTotalWidth, mTotalHeight);
        initvalue(0.6f,1.06f,0.04f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;
    }


    private void initvalue(float start ,float end ,float offset){
        mlist = new ArrayList<>();
        int length =(int)((end-start)/offset);
        for (int i=0;i<length;i++){
            mlist.add(start+offset*i);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        uiThread = new UIThread();
        uiThread.setRunning(true);
        uiThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mTotalWidth = width;
        mTotalHeight = height;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        uiThread.setRunning(false);
    }

     class UIThread extends Thread {
        private  boolean toRun = false;
        private SurfaceHolder surfaceHolder;

        public UIThread() {
            surfaceHolder = getHolder();
        }

        public boolean isThreadRunning() {
            return toRun;
        }

        public void setRunning(boolean run) {
            toRun = run;
        }

        @Override
        public void run() {
            Canvas c;
            while (toRun) {
                c = null;
                try {
                    int tr =  count/(mlist.size()-1) % 2;
                    currentValue=mlist.get(position);
                    if(tr == 1){
                        position--;
                    } else {
                        position++;
                    }
                    count++;
                    
                    c = surfaceHolder.lockCanvas(null);
                    draw(c);
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

         private void draw(Canvas canvas) {
             canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);// 设置画布的背景为透明
             //int ra=mCenterX;//长半径
             int rb=mCenterY/2;//短半径
             float temp=currentValue+volatility[mRandom.nextInt(volatility.length)];
             oval.set(padding + padding * temp, rb + padding * temp, mTotalWidth - padding - padding * temp, mCenterY + rb- padding * temp);
             canvas.drawOval(oval, mPaint);
             for (int i=0;i<3;i++){
                 canvas.rotate(45, mCenterX, mCenterY);
                 float temp2=currentValue+volatility[mRandom.nextInt(volatility.length)];
                 oval.set(padding + padding * temp2, rb + padding * temp2, mTotalWidth - padding - padding * temp2, mCenterY + rb- padding * temp2);
                 canvas.drawOval(oval, mPaint);
             }
         }

    }
}
