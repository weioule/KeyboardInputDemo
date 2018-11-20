package com.example.weioule.inputkeyboarddemo.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.weioule.inputkeyboarddemo.R;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class CumKeyboardContainer extends FrameLayout implements KeyboardView.OnKeyboardActionListener, View.OnClickListener {

    public static String IDCERT_TYPE = "pwd", AMOUT_TYPE = "amount";
    private int KEYCODE_MODE_CHANGE_RIGHT = -100;
    private CustomKeyboardView mKeyboardView;
    private View mContentView, mDecorView;
    private Keyboard mNumberKeyboard;
    private EditText mEditText;
    private Context mContext;
    private int mScrollDis;

    public CumKeyboardContainer(Context context, String type) {
        super(context);
        mContext = context;
        initView(type);
        initKeyBoard(type);
        setVisibility(GONE);
    }

    public CumKeyboardContainer(Context context) {
        super(context);
        mContext = context;
        initView(IDCERT_TYPE);
        initKeyBoard(IDCERT_TYPE);
        setVisibility(GONE);
    }

    public void attachKeyBoardView() {
        if (getParent() != null) {
            return;
        }

        FrameLayout frameLayout = (FrameLayout) ((Activity) getContext()).getWindow().getDecorView();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        lp.bottomMargin = getBottomKeyboardHeight();
        frameLayout.addView(this, lp);
    }

    /**
     * 获取底部虚拟功能键的高度
     */
    public int getBottomKeyboardHeight() {
        int screenHeight = getAccurateScreenDpi()[1];
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightDifference = screenHeight - dm.heightPixels;
        return heightDifference;
    }

    /**
     * 获取精确的屏幕大小
     */
    public int[] getAccurateScreenDpi() {
        int[] screenWH = new int[2];
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class<?> c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            screenWH[0] = dm.widthPixels;
            screenWH[1] = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWH;
    }

    private void initView(String type) {
        if (AMOUT_TYPE.equalsIgnoreCase(type)) {
            LayoutInflater.from(mContext).inflate(R.layout.kb_amout_layout, this);
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.customkeyboard, this);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window mWindow = ((Activity) getContext()).getWindow();
        mDecorView = mWindow.getDecorView();
        mContentView = mWindow
                .findViewById(Window.ID_ANDROID_CONTENT);
    }

    public void setCmbVisibility(int state) {
        if (getVisibility() == state) {
            return;
        }

        setVisibility(state);
        if (state == VISIBLE) {
            scrollHostWithKeyboard();
        } else if (mScrollDis < 0) {
            View contentView = mContentView.findViewById(android.R.id.content);
            if (null != contentView) {
                contentView.scrollBy(0, mScrollDis);
            }
            mScrollDis = 0;
        }
    }

    public void setEditText(EditText editText) {
        mEditText = editText;
    }

    private void initKeyBoard(String type) {
        if (AMOUT_TYPE.equalsIgnoreCase(type)) {
            mNumberKeyboard = new Keyboard(mContext, R.xml.kb_number);
        } else {
            mNumberKeyboard = new Keyboard(mContext, R.xml.kb_idcert);
        }

        mKeyboardView = findViewById(R.id.cmbkeyboard_view);
        mKeyboardView.setKeyboard(mNumberKeyboard);

        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(this);
    }

    @Override
    public void onPress(int primaryCode) {
        if (primaryCode == -1 || primaryCode == -5 || primaryCode == -2 || primaryCode == -3 || primaryCode == KEYCODE_MODE_CHANGE_RIGHT ||
                (48 <= primaryCode && primaryCode <= 57)) {
            mKeyboardView.setPreviewEnabled(false);
        }
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (mEditText == null) {
            return;
        }

        Editable editable = mEditText.getText();
        int start = mEditText.getSelectionStart();

        // 隐藏键盘
        if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            //finish();
        } else if (primaryCode == Keyboard.KEYCODE_DELETE ||
                primaryCode == CustomKeyboardView.KEYCODE_NUM_DELETE) { // 回退
            if (editable != null && editable.length() > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start);
                }
            }
        } else if (0x0 <= primaryCode && primaryCode <= 0x7f) {
            // 可以直接输入的字符(如0-9,.)，他们在键盘映射xml中的keycode值必须配置为该字符的ASCII码
            editable.insert(start, Character.toString((char) primaryCode));
        } else if (primaryCode > 0x7f) {
            Keyboard.Key mkey = getKeyByKeyCode(primaryCode, mKeyboardView.getKeyboard());
            // 可以直接输入的字符(如0-9,.)，他们在键盘映射xml中的keycode值必须配置为该字符的ASCII码
            editable.insert(start, mkey.label);

        } else if (primaryCode == CustomKeyboardView.KEYCODE_DOUBLE_ZORE) {
            editable.insert(start, "00");
        } else if (primaryCode == Keyboard.KEYCODE_DONE) {
            setCmbVisibility(GONE);
        } else {
            // 其他一些暂未开放的键指令，如next到下一个输入框等指令
        }
    }

    private Keyboard.Key getKeyByKeyCode(int keyCode, Keyboard keyboard) {
        if (null != keyboard) {
            List<Keyboard.Key> mKeys = keyboard.getKeys();
            for (int i = 0, size = mKeys.size(); i < size; i++) {
                Keyboard.Key mKey = mKeys.get(i);
                int[] codes = mKey.codes;

                if (codes[0] == keyCode) {
                    return mKey;
                }
            }
        }
        return null;
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    /**
     * 目前基于全屏Activity
     */
    private void scrollHostWithKeyboard() {
        if (null != mDecorView && null != mContentView) {
            int[] pos = new int[2];
            View mEditTextParent = (View) mEditText.getParent();
            mEditTextParent.getLocationOnScreen(pos);

            // * 包括标题栏，但不包括状态栏。
            Rect outRect = new Rect();
            //获取root在窗体的可视区域
            mDecorView.getWindowVisibleDisplayFrame(outRect);

            //为了忽略原生键盘
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            outRect.bottom = dm.heightPixels;//屏幕高度

            int contentHeight = mEditTextParent.findViewById(R.id.input_layout).getHeight();
            int bottomOnScreen = pos[1] + contentHeight;//输入框控件的顶部+输入框的高度

            //获取到覆盖区域
            int cmbHeight = preMeasureKeyboard(findViewById(R.id.contentLayout));
            int coveredHeight = outRect.bottom - bottomOnScreen - cmbHeight;
            if (coveredHeight < 0) {
                mScrollDis = coveredHeight;
                View contentView = mContentView.findViewById(android.R.id.content);
                if (null != contentView) {
                    contentView.scrollBy(0, -mScrollDis);
                }
            }
        }
    }

    private int preMeasureKeyboard(View view) {
        view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    @Override
    public void onClick(View view) {

    }
}
