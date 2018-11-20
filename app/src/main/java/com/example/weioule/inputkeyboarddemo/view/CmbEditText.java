package com.example.weioule.inputkeyboarddemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.weioule.inputkeyboarddemo.keyboard.CumKeyboardContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
@SuppressLint("AppCompatCustomView")
public class CmbEditText extends EditText implements View.OnFocusChangeListener, View.OnTouchListener {
    private CumKeyboardContainer mCmbView;
    private boolean mIsCustomKeyboardEnable = false;

    public CmbEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCustomKeyboardEnable(boolean isCustomKeyboardEnable) {
        mIsCustomKeyboardEnable = isCustomKeyboardEnable;

        if (mIsCustomKeyboardEnable) {
            noRelateIM();

            this.setOnFocusChangeListener(this);
            this.setOnTouchListener(this);
        }
    }

    public void setCmbView(CumKeyboardContainer cmbView) {
        if (!mIsCustomKeyboardEnable) {
            return;
        }
        mCmbView = cmbView;
    }

    public void showCMBKeyboardWindow() {
        if (mIsCustomKeyboardEnable) {
            //显示自定义键盘
            hideIM();
            setFocusable(true);
            requestFocus();
            setCursorVisible(true);

            mCmbView.setEditText(this);
            mCmbView.setCmbVisibility(View.VISIBLE);
        } else {
            //显示系统键盘
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(this, 0);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            closeCMBKeyboardWindow();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        showCMBKeyboardWindow();
        return false;
    }

    private void closeCMBKeyboardWindow() {

    }

    private void hideIM() {
        if (getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void noRelateIM() {
        ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            this.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);
            } catch (NoSuchMethodException e) {
                this.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCmbView = null;
    }
}
