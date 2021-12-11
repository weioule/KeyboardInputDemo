package com.example.weioule.inputkeyboarddemo.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.example.weioule.inputkeyboarddemo.R;
import com.example.weioule.inputkeyboarddemo.util.IdCardUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class CustomKeyboardView extends KeyboardView {
    // 用于区分左下角空白的按键
    public static final int KEYCODE_EMPTY = -10, KEYCODE_DOUBLE_ZORE = -11, KEYCODE_NUM_DELETE = -12;
    private int KEY_PADDING_TOP;
    private Context mContext;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        KEY_PADDING_TOP = IdCardUtil.dip2px(context, 5);
        mContext = context;
        setEnabled(true);
        // 设置按键没有点击放大镜显示的效果
        setPreviewEnabled(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Keyboard.Key> keys = getKeyboard().getKeys();

        for (Keyboard.Key key : keys) {
            // 如果是左下角空白的按键，重画按键的背景
            if (key.codes[0] == KEYCODE_EMPTY) {
                drawKeyBackground(key, canvas, getResources().getColor(R.color.gray_d2d5db));
            } else if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawKeyBackground(key, canvas, getResources().getColor(R.color.gray_d2d5db));
                drawDeleteButton(key, canvas);
            } else if (key.codes[0] == KEYCODE_NUM_DELETE) {
                drawDeleteButton(key, canvas);
            } else if (key.codes[0] == Keyboard.KEYCODE_DONE) {
                //重画确定键
                drawKeyBackground(key, R.drawable.cmb_checked_bg, canvas);
                drawText(canvas, key);
            }
        }
    }

    /**
     * 绘制按键的背景
     */
    private void drawKeyBackground(Keyboard.Key key, Canvas canvas, int color) {
        ColorDrawable drawable = new ColorDrawable(color);
        drawable.setBounds(key.x, key.y + KEY_PADDING_TOP, key.x + key.width, key.y + key.height + KEY_PADDING_TOP);
        drawable.draw(canvas);
    }

    private void drawKeyBackground(Keyboard.Key key, int drawableId, Canvas canvas) {
        Drawable npd = mContext.getResources().getDrawable(drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        npd.draw(canvas);
    }


    // 绘制删除按键
    private void drawDeleteButton(Keyboard.Key key, Canvas canvas) {
        Drawable drawable;
        if (key.pressed) {
            drawable = getContext().getResources().getDrawable(R.drawable.ic_delete_key_pressed);
        } else {
            drawable = getContext().getResources().getDrawable(R.drawable.ic_delete_key_normal);
        }

        if (drawable == null) {
            return;
        }

        // 计算删除图标绘制的坐标
        Rect mDeleteDrawRect = new Rect();
        if (mDeleteDrawRect.isEmpty()) {
            int drawWidth, drawHeight;
            //getIntrinsicWidth()和getIntrinsicHeight()返回的宽高应该是dp为单位
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            drawWidth = intrinsicWidth;
            drawHeight = intrinsicHeight;

            // 限制图标的大小，防止图标超出按键
            if (drawWidth > key.width) {
                drawWidth = key.width;
                drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
            }
            if (drawHeight > key.height) {
                drawHeight = key.height;
                drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
            }

            // 获取删除图标绘制的坐标
            int left = key.x + (key.width - drawWidth) / 2;
            int top = KEY_PADDING_TOP + key.y + (key.height - drawHeight) / 2;
            mDeleteDrawRect = new Rect(left, top, left + drawWidth, top + drawHeight);
        }

        // 绘制删除的图标
        if (!mDeleteDrawRect.isEmpty()) {
            drawable.setBounds(mDeleteDrawRect.left, mDeleteDrawRect.top,
                    mDeleteDrawRect.right, mDeleteDrawRect.bottom);
            drawable.draw(canvas);
        }
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        if (key.label != null) {
            String label = key.label.toString();

            Field field;

            if (label.length() > 1 && key.codes.length < 2) {
                int labelTextSize = 0;
                try {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    labelTextSize = (int) field.get(this);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                paint.setTextSize(labelTextSize);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                int keyTextSize = 0;
                try {
                    field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                    field.setAccessible(true);
                    keyTextSize = (int) field.get(this);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                paint.setTextSize(keyTextSize);
                paint.setTypeface(Typeface.DEFAULT);
            }

            paint.getTextBounds(key.label.toString(), 0, key.label.toString()
                    .length(), bounds);
            canvas.drawText(key.label.toString(), key.x + (key.width / 2), (key.y + key.height / 2) + bounds.height() / 2, paint);
        } else if (key.icon != null) {
            key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                    key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(),
                    key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
            key.icon.draw(canvas);
        }

    }
}
