package com.example.weioule.inputkeyboarddemo.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class CountDown {
    private static final int COUNT = 0000;
    private static final int FINISH = 0001;
    public static final int INTER_S = 1000;
    private int count;
    private int maxNum;
    private Timer timer;
    private TimerTask task;
    private OnCountDownListener listener;

    private Handler handler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            switch (msg.what) {
                case COUNT:
                    if (listener != null) {
                        listener.onCountDownRun(maxNum, maxNum - count);
                    }
                    break;
                case FINISH:
                    if (listener != null) {
                        listener.onCountDownFinished();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public CountDown(int maxNum) {
        this.maxNum = maxNum;
    }

    public void runTime(int interval) {
        stopTime();
        count = 0;
        timer = new Timer(true);
        task = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (maxNum - count <= 0) {
                    stopTime();
                    handler.sendEmptyMessage(FINISH);
                } else {
                    handler.sendEmptyMessage(COUNT);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 100, interval);
    }

    public void runTime(int interval, int delay) {
        stopTime();
        count = 0;
        timer = new Timer(true);
        task = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (maxNum - count <= 0) {
                    stopTime();
                    handler.sendEmptyMessage(FINISH);
                } else {
                    handler.sendEmptyMessage(COUNT);
                }
            }
        };
        timer.scheduleAtFixedRate(task, delay, interval);
    }

    public void stopTime() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public boolean isRunning() {
        return timer != null && task != null;
    }

    public void setListener(OnCountDownListener listener) {
        this.listener = listener;
    }

    public interface OnCountDownListener {
        void onCountDownFinished();

        void onCountDownRun(int maxNum, int remainNum);
    }
}
