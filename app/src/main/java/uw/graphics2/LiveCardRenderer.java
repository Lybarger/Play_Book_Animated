package uw.graphics2;

import com.google.android.glass.timeline.DirectRenderingCallback;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Renders a fading "Hello world!" in a {@link LiveCard}.
 */
public class LiveCardRenderer implements DirectRenderingCallback {

    /**
     * The duration, in millisconds, of one frame.
     */
    private static final long FRAME_TIME_MILLIS = 40;

    /**
     * "Hello world" text size.
     */
    private static final float TEXT_SIZE = 70f;

    /**
     * Alpha variation per frame.
     */
    private static final int ALPHA_INCREMENT = 5;

    /**
     * Max alpha value.
     */
    private static final int MAX_ALPHA = 256;

    private final Paint mPaint;
    private final Paint positionPaint;
    private final Paint courtPaint;
    private final String mText;
    private final Paint ballPaint;
    private final Paint textPaint;
    private final Paint playerSelectPaint;

    private int mCenterX;
    private int mCenterY;

    private SurfaceHolder mHolder;
    private boolean mRenderingPaused;

    private RenderThread mRenderThread;

    private float screenWidthPixels;
    private float screenHeighthPixels;
    private float courtWidthFeet;
    private float pixelsPerFoot;
    private float pixelsPerInch;
    private float hoopRadiusPixels;
    private float lineWidthInches;
    private float lineWidthPixels;
    private float hoopDiameterInches;
    private float boundaryWidth;
    private float boundaryHeight;
    private float hoopOffsetInches;
    private float hoopOffsetPixels;
    private float backboardOffsetFeet;
    private float backboardOffsetPixels;
    private float backboardWidthFeet;
    private float backboardWidthPixels;
    private float keyWidthFeet;
    private float keyWidthPixels;
    private float keyHeightFeet;
    private float keyHeightPixels;
    private float threePointPixels;
    private float oobUpperPixels;
    private float keyTickSpaceFeet;
    private float keyTickSpacePixels;
    private float keyBlockWidthFeet;
    private float keyBlockWidthPixels;
    private float keyTickLengthInches;
    private float keyTickLengthPixels;
    private float keyBlockOffsetFeet;
    private float keyBlockOffsetPixels;
    private float d;
    private String[] temp;
    private float stageDuration = 2000; //ms
    private float pauseDuration = 1000; //ms
    private float frameStage = 0;
    private float framePause = 0;
    private int stage = 0;
    private int pause = 0;
    private final int positionFont = 20;
    private String[] P1;
    private String[] P2;
    private String[] P3;
    private String[] P4;
    private String[] P5;
    private String[] ball;
    private String[] ballPosition;
    private String shoot;

    private String player1;
    private String player2;
    private String player3;
    private String player4;
    private String player5;

    private String pCornerLeft;
    private String pCornerRight;
    private String pWingLeft;
    private String pWingRight;
    private String pPoint;
    private String pElbowLeft;
    private String pElbowRight;
    private String pBlockLeft;
    private String pBlockRight;
    private String pPaintLeft ;
    private String pPaintRight;
    private String pPaintCenter ;
    private String pShortCornerLeft ;
    private String pShortCornerRight;
    private String pHighPost;
    private final int ball_size = 10;

    public LiveCardRenderer(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
        //mPaint.setAlpha(256);

        positionPaint = new Paint();
        positionPaint.setStyle(Paint.Style.FILL);
        positionPaint.setColor(Color.BLACK);
        positionPaint.setAntiAlias(true);
        positionPaint.setTextSize(positionFont);
        positionPaint.setTextAlign(Paint.Align.CENTER);
        //positionPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

        mText = context.getResources().getString(R.string.hello_world);

        screenWidthPixels = context.getResources().getInteger(R.integer.screenWidthPixels);
        screenHeighthPixels = context.getResources().getInteger(R.integer.screenHeightPixels);
        courtWidthFeet = context.getResources().getInteger(R.integer.courtWidthFeet);
        lineWidthInches = context.getResources().getInteger(R.integer.lineWidthInches);
        hoopDiameterInches = context.getResources().getInteger(R.integer.hoopDiameterInches);
        boundaryWidth = context.getResources().getInteger(R.integer.boundaryWidth);
        boundaryHeight = context.getResources().getInteger(R.integer.boundaryHeight);
        hoopOffsetInches = context.getResources().getInteger(R.integer.hoopOffsetInches);
        backboardOffsetFeet = context.getResources().getInteger(R.integer.backboardOffsetFeet);
        backboardWidthFeet = context.getResources().getInteger(R.integer.backboardWidthFeet);
        keyWidthFeet = context.getResources().getInteger(R.integer.keyWidthFeet);
        keyHeightFeet = context.getResources().getInteger(R.integer.keyHeightFeet);
        keyTickSpaceFeet = context.getResources().getInteger(R.integer.keyTickSpaceFeet);
        keyTickLengthInches = context.getResources().getInteger(R.integer.keyTickLengthInches);
        keyBlockWidthFeet = context.getResources().getInteger(R.integer.keyBlockWidthFeet);
        keyBlockOffsetFeet = context.getResources().getInteger(R.integer.keyBlockOffsetFeet);

        pixelsPerFoot = (screenWidthPixels-2*boundaryWidth)/courtWidthFeet;
        pixelsPerInch = pixelsPerFoot/12f;
        lineWidthPixels = lineWidthInches*pixelsPerInch*2f;
        hoopRadiusPixels = hoopDiameterInches*pixelsPerInch/2f;
        hoopOffsetPixels = hoopOffsetInches*pixelsPerInch;
        backboardOffsetPixels = backboardOffsetFeet*pixelsPerFoot;
        backboardWidthPixels = backboardWidthFeet*pixelsPerFoot;
        keyWidthPixels = keyWidthFeet*pixelsPerFoot;
        keyHeightPixels = keyHeightFeet*pixelsPerFoot;
        keyTickSpacePixels = keyTickSpaceFeet*pixelsPerFoot;
        keyTickLengthPixels = keyTickLengthInches*pixelsPerInch;
        keyBlockWidthPixels = keyBlockWidthFeet*pixelsPerFoot;
        keyBlockOffsetPixels = keyBlockOffsetFeet*pixelsPerFoot;
        threePointPixels = keyHeightPixels + keyWidthPixels/2f - hoopOffsetPixels;
        oobUpperPixels = boundaryHeight + lineWidthPixels/2f;


        P1 = context.getResources().getStringArray(R.array.P1);
        P2 = context.getResources().getStringArray(R.array.P2);
        P3 = context.getResources().getStringArray(R.array.P3);
        P4 = context.getResources().getStringArray(R.array.P4);
        P5 = context.getResources().getStringArray(R.array.P5);
        ball = context.getResources().getStringArray(R.array.ball);


        player1 = context.getResources().getString(R.string.player1);
        player2 = context.getResources().getString(R.string.player2);
        player3 = context.getResources().getString(R.string.player3);
        player4 = context.getResources().getString(R.string.player4);
        player5 = context.getResources().getString(R.string.player5);
        shoot = context.getResources().getString(R.string.shoot);

        for (int i=0; i < ball.length-1; i++) {
            if (ball[i].equals(player1))
            {   ball[i] = P1[i];}
            else if (ball[i].equals(player2))
            {   ball[i] = P2[i];}
            else if (ball[i].equals(player3))
            {   ball[i] = P3[i];}
            else if (ball[i].equals(player4))
            {   ball[i] = P4[i];}
            else if (ball[i].equals(player5))
            {   ball[i] = P5[i];}
            else if (ball[i].equals(shoot))
            {   ball[i] = shoot;}
            else
            {   ball[i] ="";}
        }


        pCornerLeft = context.getResources().getString(R.string.corner_left);
        pCornerRight = context.getResources().getString(R.string.corner_right);
        pWingLeft = context.getResources().getString(R.string.wing_left);
        pWingRight = context.getResources().getString(R.string.wing_right);
        pPoint = context.getResources().getString(R.string.point);
        pElbowLeft = context.getResources().getString(R.string.elbow_left);
        pElbowRight = context.getResources().getString(R.string.elbow_right);
        pBlockLeft = context.getResources().getString(R.string.block_left);
        pBlockRight = context.getResources().getString(R.string.block_right);
        pPaintLeft = context.getResources().getString(R.string.paint_left);
        pPaintRight = context.getResources().getString(R.string.paint_right);
        pPaintCenter = context.getResources().getString(R.string.paint_center);
        pShortCornerLeft = context.getResources().getString(R.string.short_corner_left);
        pShortCornerRight = context.getResources().getString(R.string.short_corner_right);
        pHighPost = context.getResources().getString(R.string.high_post);


        courtPaint = new Paint();
        courtPaint.setStyle(Paint.Style.STROKE);
        courtPaint.setColor(Color.WHITE);
        courtPaint.setStrokeWidth(lineWidthPixels);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setStyle(Paint.Style.FILL);
        //textPaint.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

        ballPaint = new Paint();
        ballPaint.setStyle(Paint.Style.STROKE);
        ballPaint.setColor(0xFFFF6600);
        ballPaint.setStrokeWidth(lineWidthPixels);
        ballPaint.setStyle(Paint.Style.FILL);


        playerSelectPaint = new Paint();
        playerSelectPaint.setStyle(Paint.Style.FILL);
        playerSelectPaint.setColor(Color.RED);
        playerSelectPaint.setAntiAlias(true);
        playerSelectPaint.setTextSize(positionFont);
        playerSelectPaint.setTextAlign(Paint.Align.CENTER);

    }




    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCenterX = width / 2;
        mCenterY = height / 2;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        mRenderingPaused = false;
        updateRenderingState();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        updateRenderingState();
    }

    @Override
    public void renderingPaused(SurfaceHolder holder, boolean paused) {
        mRenderingPaused = paused;
        updateRenderingState();
    }

    /**
     * Starts or stops rendering according to the {@link LiveCard}'s state.
     */
    private void updateRenderingState() {
        boolean shouldRender = (mHolder != null) && !mRenderingPaused;
        boolean isRendering = (mRenderThread != null);

        if (shouldRender != isRendering) {
            if (shouldRender) {
                mRenderThread = new RenderThread();
                mRenderThread.start();
            } else {
                mRenderThread.quit();
                mRenderThread = null;
            }
        }
    }

    /**
     * Draws the court
     */
    private Canvas drawCourt(Canvas canvas) {
        float x1;
        float y1;
        float x2;
        float y2;
        float r;

        //Out of bounds
        //Left
        x1 = boundaryWidth;
        y1 = boundaryHeight - lineWidthPixels/2;
        x2 = x1;
        y2 = screenHeighthPixels - boundaryHeight;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        //Right
        x1 = screenWidthPixels - boundaryWidth;
        x2 = screenWidthPixels - boundaryWidth;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        //Upper
        x1 = boundaryWidth - lineWidthPixels/2;
        y1 = boundaryHeight;
        x2 = screenWidthPixels - boundaryWidth;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        // Hoop and backboard
        x1 = screenWidthPixels/2f;
        y1 = oobUpperPixels + hoopOffsetPixels;
        r = hoopRadiusPixels;
        canvas.drawCircle(x1, y1, r,courtPaint);

        x1 = screenWidthPixels/2f-backboardWidthPixels/2f;
        y1 = oobUpperPixels+backboardOffsetPixels;
        x2 = screenWidthPixels/2f+backboardWidthPixels/2f;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        // Key
        //Left
        x1 = screenWidthPixels/2f - keyWidthPixels/2f;
        y1 = oobUpperPixels;
        x2 = x1;
        y2 = oobUpperPixels + keyHeightPixels;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Left block
        y1 = oobUpperPixels + backboardOffsetPixels + keyTickSpacePixels;
        x2 = x1 - keyTickLengthPixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Left tick 1
        y1 = y2 + keyTickSpacePixels;
        x2 = x1 - keyTickLengthPixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Left tick 2
        y1 = y2 + keyTickSpacePixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Left tick 3
        y1 = y2 + keyTickSpacePixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        //Right
        x1 = screenWidthPixels/2f + keyWidthPixels/2f;
        y1 = oobUpperPixels;
        x2 = x1;
        y2 = oobUpperPixels + keyHeightPixels;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Right block
        y1 = oobUpperPixels + backboardOffsetPixels + keyTickSpacePixels;
        x2 = x1 + keyTickLengthPixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Right tick 1
        y1 = y2 + keyTickSpacePixels;
        x2 = x1 + keyTickLengthPixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Right tick 2
        y1 = y2 + keyTickSpacePixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);
        //Right tick 3
        y1 = y2 + keyTickSpacePixels;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        //Upper
        x1 = (screenWidthPixels - keyWidthPixels)/2f;
        y1 = oobUpperPixels + keyHeightPixels;
        x2 = (screenWidthPixels + keyWidthPixels)/2f;
        y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        //Arc at key
        y1 = oobUpperPixels + keyHeightPixels - keyWidthPixels/2f;
        y2 = oobUpperPixels + keyHeightPixels + keyWidthPixels/2f;
        RectF oval = new RectF();
        oval.set(x1, y1, x2, y2);
        canvas.drawArc(oval, 0, 180, false, courtPaint);

        //3pt arc
        //Arc
        x1 = screenWidthPixels/2f-threePointPixels;
        y1 = oobUpperPixels+hoopOffsetPixels-threePointPixels;
        x2 = screenWidthPixels/2f+threePointPixels;
        y2 = oobUpperPixels+hoopOffsetPixels+threePointPixels;
        oval.set(x1, y1, x2, y2);
        canvas.drawArc(oval, 0, 180, false, courtPaint);

        //Left
        x1 = screenWidthPixels/2f-threePointPixels;
        y1 = oobUpperPixels;
        x2 = x1;
        y2 = oobUpperPixels + hoopOffsetPixels;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);

        //Right
        x1 = screenWidthPixels/2f+threePointPixels;
        x2 = x1;
        canvas.drawLine(x1, y1, x2, y2, courtPaint);


        return canvas;
    }


    private float[] descriptionToCoord(String positionDescription){
        float[] positionCoord = {0,0};

        if (positionDescription.equals(pCornerLeft))
        {   positionCoord[0] = (screenWidthPixels/2f-threePointPixels);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pCornerRight))
        {   positionCoord[0] = (screenWidthPixels/2f+threePointPixels);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pWingLeft))
        {   positionCoord[0] = (screenWidthPixels/2f-threePointPixels);
            positionCoord[1] = (oobUpperPixels + keyHeightPixels); }
        else if (positionDescription.equals(pWingRight))
        {   positionCoord[0] = (screenWidthPixels/2f+threePointPixels);
            positionCoord[1] = (oobUpperPixels + keyHeightPixels); }
        else if (positionDescription.equals(pPoint))
        {   positionCoord[0] = (screenWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels + threePointPixels); }
        else if (positionDescription.equals(pElbowLeft))
        {   positionCoord[0] = (screenWidthPixels/2f-keyWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + keyHeightPixels); }
        else if (positionDescription.equals(pElbowRight))
        {   positionCoord[0] = (screenWidthPixels/2f+keyWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + keyHeightPixels); }
        else if (positionDescription.equals(pHighPost))
        {   positionCoord[0] = (screenWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + keyHeightPixels); }
        else if (positionDescription.equals(pBlockLeft))
        {   positionCoord[0] = (screenWidthPixels/2f-keyWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pBlockRight))
        {   positionCoord[0] = (screenWidthPixels/2f+keyWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pPaintLeft))
        {   positionCoord[0] = (screenWidthPixels/2f-keyWidthPixels/4f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pPaintRight))
        {   positionCoord[0] = (screenWidthPixels/2f+keyWidthPixels/4f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pPaintCenter))
        {   positionCoord[0] = (screenWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels +keyWidthPixels/4f); }
        else if (positionDescription.equals(pShortCornerLeft))
        {   positionCoord[0] = (screenWidthPixels/2f-threePointPixels/2);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(pShortCornerRight))
        {   positionCoord[0] = (screenWidthPixels/2f+threePointPixels/2);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else if (positionDescription.equals(shoot))
        {   positionCoord[0] = (screenWidthPixels/2f);
            positionCoord[1] = (oobUpperPixels + hoopOffsetPixels); }
        else
        {   positionCoord[0] = 100;
            positionCoord[1] = 100; }
        return positionCoord;
    }

    private Canvas drawBall(Canvas canvas, String Py, String[] Px, int i, int j)
    {

        float[] xy1 = descriptionToCoord(Px[i]);
        float[] xy2 = descriptionToCoord(Px[j]);

        float x;
        float y;
        float dx;
        float dy;
        double theta;

        x = xy1[0] + (xy2[0]-xy1[0])*frameStage*FRAME_TIME_MILLIS/stageDuration;
        y = xy1[1] + (xy2[1]-xy1[1])*frameStage*FRAME_TIME_MILLIS/stageDuration;
        dx = screenWidthPixels/2f-x;
        dy = oobUpperPixels + hoopOffsetPixels - y;
        theta = (float) Math.atan2(dy,dx);

        if (!Px[j].equals(shoot)) {
            x = x + (ball_size + positionFont * 0.8f) * ((float) Math.cos(theta));
            y = y + (ball_size + positionFont * 0.8f) * ((float) Math.sin(theta)) - positionFont / 3;
        }
        canvas.drawCircle(x,y,ball_size,ballPaint);

        return canvas;
    }

    private Canvas drawPlayer(Canvas canvas, String Py, String[] Px, int i, int j, boolean select)
    {

        float[] xy1 = descriptionToCoord(Px[i]);
        float[] xy2 = descriptionToCoord(Px[j]);

        float x;
        float y;

        x = xy1[0] + (xy2[0]-xy1[0])*frameStage*FRAME_TIME_MILLIS/stageDuration;
        y = xy1[1] + (xy2[1]-xy1[1])*frameStage*FRAME_TIME_MILLIS/stageDuration;

        if (select)
        {canvas.drawCircle(x,y-positionFont/3,positionFont*.8f,playerSelectPaint);}
        else
        {canvas.drawCircle(x,y-positionFont/3,positionFont*.8f,mPaint);}

        canvas.drawText(Py,x,y,positionPaint);
        return canvas;
    }


        private Canvas drawPlay(Canvas canvas) {
        int N = P1.length-1;
        if (pause == 0) {
            frameStage++;
            if (frameStage*FRAME_TIME_MILLIS >= stageDuration) {
                stage++;
                stage = stage % N;
                frameStage = 0;
                pause = 1;
            }
        }
        else {
            framePause++;
            if (framePause*FRAME_TIME_MILLIS >= pauseDuration) {
                framePause = 0;
                pause = 0;
            }
        }

        canvas = drawPlayer(canvas,player1,P1,stage, stage + 1 %N, true);
        canvas = drawPlayer(canvas,player2,P2,stage, stage + 1 %N,false);
        canvas = drawPlayer(canvas,player3,P3,stage, stage + 1 %N,false);
        canvas = drawPlayer(canvas, player4, P4, stage, stage + 1 % N, false);
        canvas = drawPlayer(canvas, player5, P5, stage, stage + 1 % N, false);
        canvas = drawBall(canvas,"BB",ball,stage, stage + 1 %N);
        String footer = "Step " + Integer.toString(stage + 1) + " of " + Integer.toString(N + 1);
        canvas.drawText(footer,50,300,textPaint);
        //canvas = drawBall(canvas,"",ballPosition,stage, stage + 1 %N);
        return canvas;
    }


    /**
     * Draws the view in the SurfaceHolder's canvas.
     */
    private void draw() {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            // Clear the canvas.
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            canvas = drawCourt(canvas);
            canvas = drawPlay(canvas);


            // Unlock the canvas and post the updates.
            mHolder.unlockCanvasAndPost(canvas);
        }
    }






    /**
     * Redraws the {@link View} in the background.
     */
    private class RenderThread extends Thread {
        private boolean mShouldRun;

        /**
         * Initializes the background rendering thread.
         */
        public RenderThread() {
            mShouldRun = true;
        }

        /**
         * Returns true if the rendering thread should continue to run.
         *
         * @return true if the rendering thread should continue to run
         */
        private synchronized boolean shouldRun() {
            return mShouldRun;
        }

        /**
         * Requests that the rendering thread exit at the next opportunity.
         */
        public synchronized void quit() {
            mShouldRun = false;
        }

        @Override
        public void run() {
            while (shouldRun()) {
                long frameStart = SystemClock.elapsedRealtime();
                draw();
                long frameLength = SystemClock.elapsedRealtime() - frameStart;

                long sleepTime = FRAME_TIME_MILLIS - frameLength;
                if (sleepTime > 0) {
                    SystemClock.sleep(sleepTime);
                }
            }
        }
    }

}
