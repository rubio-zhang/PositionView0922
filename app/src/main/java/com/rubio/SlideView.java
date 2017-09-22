package com.rubio;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by Rubio on 2017/8/30 0030.
 * 滑动视图
 */

public class SlideView extends View implements ScaleGestureDetector.OnScaleGestureListener {

    public final static int UP = 0x01;
    public final static int DOWN = 0x02;
    public final static int LEFT = 0x03;
    public final static int RIGHT = 0x04;

    private int myselfWidth = -1;         //控件的宽
    private int myselfHeight = -1;         //控件的长

    private int ShowTopX = -1;           //图片的X坐标
    private int ShowTopY = -1;           //图片的Y坐标

    private float zoom = 1;                 //0.5  -  4 倍
    private int alpha = 0xff;               //透明度       0-255
    private int picture = -1;               //图片的地址

    private Bitmap mbit;
    private Bitmap showBit;
    private Bitmap changeBit;

    CallView cview;

    private ScaleGestureDetector mScaleGestureDetector = null;

    public SlideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
        picture = typedArray.getResourceId(R.styleable.SlideView_background, android.R.mipmap.sym_def_app_icon);
        mbit = BitmapFactory.decodeResource(getResources(), picture);
        showBit = BitmapFactory.decodeResource(getResources(), picture);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    public void setCall(CallView c) {
        this.cview = c;
    }

    public void setAplha(int a) {
        this.alpha = a;
        invalidate();
    }

    public void setCurrentZoom(float size){
        int showW=showBit.getWidth();
        int shohH=showBit.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(size, size);
        Bitmap chanBit2= Bitmap.createBitmap(showBit, 0, 0, showW, shohH, matrix, true);
    }

    //设置图片倍数
    public void setZoom(float size) {
        if (size<0.5||size>4){
            return;
        }
        int showW=showBit.getWidth();
        int shohH=showBit.getHeight();
        int w=mbit.getWidth();
        int h=mbit.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(size, size);
        showBit= Bitmap.createBitmap(mbit, 0, 0, w, h, matrix, true);
        int w2=showBit.getWidth();
        int h2=showBit.getHeight();
        if (size>zoom){
           ShowTopX -=Math.abs((showW-w2)/2);
           ShowTopY -=Math.abs((shohH-h2)/2);
        }else{
           ShowTopX +=Math.abs((w2-showW)/2);
           ShowTopY +=Math.abs((h2-shohH)/2);
        }
        this.zoom = size;
        invalidate();
    }

    private void changeZoom(float size,boolean callback){
        setZoom(size);
        if (cview!=null&&callback){
            cview.onScale(zoom);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int bpWidth = mbit.getWidth();
        int bpHeight = mbit.getHeight();
        myselfWidth = this.getMeasuredWidth();
        myselfHeight = this.getMeasuredHeight();
        ShowTopX = myselfWidth / 2 - bpWidth / 2;
        ShowTopY = myselfWidth / 2 - bpHeight / 2;
    }

    //手柄控制器位移的方向
    public void setOpre(int type) {
        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);
        Bitmap changeBitmap = Bitmap.createBitmap(mbit, 0, 0, mbit.getWidth(), mbit.getHeight(), matrix, true);
        switch (type) {
            case UP:
                if (ShowTopY > 10) {
                    ShowTopY -= 10;
                } else {
                    ShowTopY = 0;
                }
                break;
            case DOWN:
                if ((ShowTopY + changeBitmap.getHeight()) < myselfHeight - 10) {
                    ShowTopY += 10;
                } else {
                    ShowTopY = myselfHeight - changeBitmap.getHeight();
                }
                break;
            case LEFT:
                if (ShowTopX > 10) {
                    ShowTopX -= 10;
                } else {
                    ShowTopX = 0;
                }
                break;
            case RIGHT:
                if ((ShowTopX + changeBitmap.getWidth()) < myselfWidth - 10) {
                    ShowTopX += 10;
                } else {
                    ShowTopX = myselfWidth - changeBitmap.getWidth();
                }
                break;
        }
        invalidate();
    }

    float downX=-1;
    float downY=-1;

    int distance=-1;

    int pointerCount = 0;
    boolean canMove=false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pointerCount = event.getPointerCount();
        if (pointerCount==1){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX=event.getX(0);
                    downY=event.getY(0);
                    canMove = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canMove){
                        int pointerCount_move = event.getPointerCount();
                        if (pointerCount_move==1){
                            float tempX=event.getX(0);
                            float tempY=event.getY(0);
                            ShowTopX +=tempX-downX;
                            ShowTopY +=tempY-downY;
                            downX=tempX;
                            downY=tempY;
                            onLimitLengthWidth();
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int pointerCount3 = event.getPointerCount();
                    distance=-1;
                    downX=-1;
                    downY=-1;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }
        // 缩放
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                // 第二根手指按下去
                canMove = false;
                break;
            default:
                break;
        }
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    //限制移动的时候不能滑出界面
    private void onLimitLengthWidth(){
        if (ShowTopX <=0){
            ShowTopX =0;
        }
        if (ShowTopX +showBit.getWidth()>= myselfWidth){
            ShowTopX = myselfWidth -showBit.getWidth();
        }
        if (ShowTopY <=0){
            ShowTopY =0;
        }
        if (ShowTopY +showBit.getHeight()>= myselfHeight){
            ShowTopY = myselfHeight -showBit.getHeight();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mbit.recycle();
        showBit.recycle();
    }

    float temp;

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        temp=zoom;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        if (scaleFactor>1){
            float zoom1=scaleFactor-1+temp;
            changeZoom(zoom1,true);
            if (cview!=null){
                cview.onZoomTouch(false);
            }
        }else{
            float zoom2=temp-(1-scaleFactor);
            changeZoom(zoom2,true);
            if (cview!=null){
                cview.onZoomTouch(false);
            }
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        if (scaleFactor>1){
            float zoom1=scaleFactor-1+temp;
            if (zoom1>4){
                zoom1=4;
            }
            changeZoom(zoom1,true);
            if (cview!=null){
                cview.onZoomTouch(true);
            }
        }else{
            float zoom2=temp-(1-scaleFactor);
            if (zoom2<0.5){
                zoom2= (float) 0.5;
            }
            changeZoom(zoom2,true);
            if (cview!=null){
                cview.onZoomTouch(true);
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {          //刷新的时候  不能改变图片的任何属性
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(alpha);
        canvas.drawBitmap(showBit, ShowTopX, ShowTopY, paint);
        if (cview!=null){
            cview.onBitmapMessage(myselfHeight,myselfWidth,zoom,alpha,ShowTopX+showBit.getWidth()/2,ShowTopY+showBit.getHeight()/2,showBit.getWidth(),showBit.getHeight());
        }


    }
    public interface CallView {
        void onMessage(String name);

        /**
         *  缩放大小的scale
         * */
        void onScale(float size);

        /**
         *  是否缩放中
         * */
        void onZoomTouch(boolean flag);

        void onBitmapMessage(int TotalLength, int TotalWidth, float scale, float alpha, int centerPointX, int centerPointY, int bitWidth, int bitHeight);

    }
}
