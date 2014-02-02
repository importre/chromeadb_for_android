package io.github.importre.android.chromeadb;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import java.io.File;
import java.io.IOException;

public class ChromeAdbService extends Service implements TailerListener {

    private File mEventFile = new File("/sdcard/chromeadb.event");
    private ImageView mCursorImage;
    private String mPrevLine;
    private Tailer mTailer;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParam;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTailer();
        addMouseCursor();
        setCursorPosToCenter();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTailer();
        removeMouseCursor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addMouseCursor() {
        if (mCursorImage == null) {
            mCursorImage = new ImageView(this);
            mCursorImage.setImageResource(R.drawable.cursor);
        }

        if (mLayoutParam == null) {
            mLayoutParam = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            mLayoutParam.gravity = Gravity.LEFT | Gravity.TOP;
            mLayoutParam.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mCursorImage, mLayoutParam);
        }
    }

    private void setCursorPosToCenter() {
        if (mWindowManager == null || mCursorImage == null) {
            return;
        }

        Display display = mWindowManager.getDefaultDisplay();
        int x, y;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            x = size.x;
            y = size.y;
        } else {
            x = display.getWidth();
            y = display.getHeight();
        }

        move(x >> 1, y >> 1);
    }

    private void removeMouseCursor() {
        if (mCursorImage != null && mWindowManager != null) {
            mWindowManager.removeView(mCursorImage);
            mCursorImage = null;
        }
    }

    public void move(int touchX, int touchY) {
        if (mLayoutParam == null || mWindowManager == null || mCursorImage == null) {
            return;
        }

        mLayoutParam.x = touchX;
        mLayoutParam.y = touchY;
        mWindowManager.updateViewLayout(mCursorImage, mLayoutParam);
    }

    private void startTailer() {
        try {
            if (mEventFile.exists()) {
                mEventFile.delete();
            }
            mEventFile.createNewFile();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTailer != null) {
            mTailer.stop();
        }

        mTailer = new Tailer(mEventFile, this, 10, true);
        Thread thread = new Thread(mTailer);
        thread.start();
    }

    private void stopTailer() {
        if (mTailer != null) {
            mTailer.stop();
            mTailer = null;
        }

        if (mEventFile != null && mEventFile.exists()) {
            mEventFile.delete();
        }
    }

    @Override
    public void init(Tailer tailer) {
    }

    @Override
    public void fileNotFound() {
        mTailer.stop();
    }

    @Override
    public void fileRotated() {
    }

    @Override
    public void handle(String s) {
        if (mPrevLine != null && mPrevLine.equals(s)) {
            return;
        }

        String coords = Command.getCoordinates(s);
        if (coords != null) {
            moveCursor(coords);
        }

        mPrevLine = s;
    }

    private void moveCursor(String coords) {
        try {
            final String[] points = coords.split(",");
            for (int i = 0; i < points.length; i += 2) {
                int x = Integer.parseInt(points[i]);
                int y = Integer.parseInt(points[i + 1]);
                Message msg = mHandler.obtainMessage();
                Bundle data = new Bundle();
                data.putInt("x", x);
                data.putInt("y", y);
                msg.setData(data);
                mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
        }
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data != null) {
                int x = data.getInt("x", 0);
                int y = data.getInt("y", 0);
                move(x, y);
            }
        }
    };

    @Override
    public void handle(Exception e) {
    }
}
