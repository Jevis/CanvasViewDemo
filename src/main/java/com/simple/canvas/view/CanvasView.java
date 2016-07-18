package com.simple.canvas.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.simple.canvas.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：自定义动画采用View实现 方式一</p>
 * 1.可以根据view的可见来控制刷新，
 * 离开当前的view界面就不会再刷新<br>
 * 2.是否有焦点<br>
 * 3.不会一直绘制消耗CPU<br>
 * 
 * onWindowVisibilityChanged 
 * onWindowFocusChanged
 *
 * @author ~若相惜
 * @version v2.0
 * @date 2016-7-18 上午10:34:51
 */
class CanvasView extends View {
    private Paint mPaint = new Paint();
    private int mTotalWidth;
    private int mCenterX ;
    private int mCenterY;
    private RectF oval;
    private int padding=55;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    private int position =0;
    private float currentValue =0;
    private long count=0;
    private List<Float> mlist;
    private Paint mBigCirclePaint = new Paint();
    private Paint mSmallCirclePaint = new Paint();

    public CanvasView(Context context) {
       super(context);
        init();
    }
    public CanvasView(Context context, AttributeSet attrs) {
       super(context, attrs);
        init();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        padding = DensityUtil.dip2px(getContext(),18);
        this.mBigCirclePaint.setStyle(Paint.Style.FILL);
        this.mBigCirclePaint.setAntiAlias(true);
        this.mBigCirclePaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
        this.mBigCirclePaint.setStrokeWidth(4);
        
        this.mSmallCirclePaint.setColor(getResources().getColor(R.color.white_d0));
        this.mSmallCirclePaint.setStyle(Paint.Style.STROKE);
        this.mSmallCirclePaint.setAntiAlias(true);
        this.mSmallCirclePaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
        this.mSmallCirclePaint.setStrokeWidth(4);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setMinimumHeight(300);
        setMinimumWidth(300);

        this.mPaint.setColor(Color.parseColor("#4cffffff"));
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);

        oval = new RectF(210, 100, 250, 100);
        initvalue(0.5f,1.06f,0.05f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("test", "--------onDraw----------");
        canvas.drawColor(Color.TRANSPARENT);
        drawAnim(canvas);
        //drawCril(canvas);
    }

    private int rb;
    private int hPadding;
    private int r;
    private void  initRab(){
        //int ra = mCenterX;
        rb=mCenterY/2;//短半径
        hPadding = padding/2;
        r = mCenterY/2+padding+padding/4;//大圆半径
    }

    private void drawAnim(Canvas canvas) {
        for (int i=0;i<4;i++){
            canvas.rotate(i==0?0:45, mCenterX, mCenterY);
            float temp=currentValue;//+volatility[mRandom.nextInt(volatility.length)];
            oval.set(padding * temp, rb -hPadding * temp, mTotalWidth  - padding * temp, mCenterY + rb+ hPadding * temp);
            canvas.drawOval(oval, mPaint);
        }
    }
    
    public void drawCril(Canvas canvas){
        canvas.drawCircle(mCenterX,mCenterY,r,this.mBigCirclePaint);
        canvas.drawCircle(mCenterX,mCenterY,r-DensityUtil.dip2px(getContext(),8),this.mSmallCirclePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        int mTotalHeight = h;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;
        invalidate();
        initRab();
        Shader mShader = new LinearGradient(mCenterX + r, mCenterY - r, mCenterX - r, mCenterY + r, Color.parseColor("#ff9b65"), Color.parseColor("#f56995"), Shader.TileMode.MIRROR);
        this.mBigCirclePaint.setShader(mShader);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            postDelayed(mRefreshProgressRunnable, 100);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(mRefreshProgressRunnable==null)return;
        if (hasWindowFocus) {
            removeCallbacks(mRefreshProgressRunnable);
            postDelayed(mRefreshProgressRunnable, 100);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
        }
    }

    private void initvalue(float start ,float end ,float offset){
        mlist = new ArrayList<>();
        int length =(int)((end-start)/offset);
        for (int i=0;i<length;i++){
            mlist.add(start+offset*i);
        }
    }
    
    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (CanvasView.this) {
                long start = System.currentTimeMillis();
                long tr =  count/(mlist.size()-1) % 2;
                currentValue=mlist.get(position);
                if(tr == 1){
                    position--;
                } else {
                    position++;
                }
                count++;
                invalidate();
                long gap = 100 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }
    
}
