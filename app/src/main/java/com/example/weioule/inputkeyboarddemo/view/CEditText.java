package com.example.weioule.inputkeyboarddemo.view;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.weioule.inputkeyboarddemo.MyApplication;
import com.example.weioule.inputkeyboarddemo.R;
import com.example.weioule.inputkeyboarddemo.keyboard.CumKeyboardContainer;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class CEditText extends RelativeLayout {
    private int maxlength;
    private View mSplitLine;
    public Button mRightBtn;
    public ImageView mClearbtn;
    private CmbEditText mEditText;
    private FocusChange focusChange;
    private AsyncImageView mLeftImage;
    private ObjectAnimator nopeAnimator;
    public OnEditListener mEditListener;
    private boolean clear, clearImage, isShowPwd = false;
    private TextView mLabel, mLeftTextView, mRightButton, mErrNote;

    public CEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(R.layout.c_edit_text);
        initView();
        setAttribute(context, attrs);
    }

    public void inflate(int layoutId) {
        if (layoutId != -1) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(layoutId, this, true);
        }
    }

    public void initView() {
        mLabel = findViewById(R.id.label);
        mErrNote = findViewById(R.id.err_note);
        mEditText = findViewById(R.id.editText);
        mRightBtn = findViewById(R.id.right_btn);
        mClearbtn = findViewById(R.id.clear_btn);
        mLeftImage = findViewById(R.id.left_image);
        mRightButton = findViewById(R.id.right_iamge);
        mLeftTextView = findViewById(R.id.left_textView);
        mSplitLine = findViewById(R.id.bottom_split_line);
        setListener();
        listener();
    }

    /**
     * 设置控件属性
     */
    public void setAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custom);
        if (typedArray.getBoolean(R.styleable.custom_clear, false)) {
            setClear(true);
        }
        String txt = typedArray.getString(R.styleable.custom_lable);
        if (txt != null && !"".equals(txt)) {
            mLabel.setText(txt);
        }
        float left = typedArray.getDimension(R.styleable.custom_paddingleft_lable, 0);
        mLabel.setPadding((int) left, 0, 0, 0);

        String err = typedArray.getString(R.styleable.custom_errNote);
        if (err != null && !"".equals(err)) {
            mErrNote.setText(err);
        }

        if (typedArray.getBoolean(R.styleable.custom_show_custom_keyboard, false)) {
            mEditText.setCustomKeyboardEnable(true);
        }

        String hit = typedArray.getString(R.styleable.custom_hit);
        if (hit != null && !"".equals(hit)) {
            getEditText().setHint(hit);
        }

        boolean isShowBottomLine = typedArray.getBoolean(R.styleable.custom_show_bottom_split, true);
        if (!isShowBottomLine) {
            mSplitLine.setVisibility(GONE);
        }

        Drawable leftDrawable = typedArray.getDrawable(R.styleable.custom_btn_left_bg);
        if (leftDrawable != null) {
            mLeftTextView.setBackgroundDrawable(leftDrawable);
            mLeftTextView.setVisibility(View.VISIBLE);
        }

        Drawable rightDrawable = typedArray.getDrawable(R.styleable.custom_btn_right_bg);
        if (rightDrawable != null) {
            mRightButton.setBackgroundDrawable(rightDrawable);
            mRightButton.setVisibility(View.VISIBLE);
        }

        boolean isShowPwdEye = typedArray.getBoolean(R.styleable.custom_show_pwd_eye, false);
        if (isShowPwdEye) {
            setRightIamgeClick(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPwdEyeState(mRightButton);
                }
            });
        }

        String type = typedArray.getString(R.styleable.custom_input_typed);
        if (type != null && !"".equals(type)) {
            getEditText().setInputType(MyInputType.valueOf(type).getType());
        }

        int length = typedArray.getInteger(R.styleable.custom_length, -1);
        maxlength = length;
        if (length > 0) {
            getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }

        String right_btn_text = typedArray.getString(R.styleable.custom_right_btn_text);
        if (right_btn_text != null && !"".equals(right_btn_text)) {
            getEditText().setInputType(MyInputType.valueOf(type).getType());
            mRightBtn.setVisibility(VISIBLE);
            mRightBtn.setText(right_btn_text);
        }

        Drawable rightBtnDrawable = typedArray.getDrawable(R.styleable.custom_right_btn);
        if (rightBtnDrawable != null) {
            mRightBtn.setVisibility(VISIBLE);
            mRightBtn.setBackgroundDrawable(rightBtnDrawable);
        }

        String left_text = typedArray.getString(R.styleable.custom_left_text);
        if (left_text != null && !"".equals(left_text)) {
            mLeftTextView.setTextAppearance(getContext(), R.style.font_gray_6_16);
            mLeftTextView.setVisibility(VISIBLE);
            mLeftTextView.setText(left_text);
            mLeftTextView.setPadding(0, 0, 15, 0);
        }
        typedArray.recycle();
    }

    private void setPwdEyeState(View eyeView) {
        isShowPwd = !isShowPwd;
        if (isShowPwd) {
            setInputType("visible_password");
        } else {
            setInputType("textPassword");
        }

        setSelection(getEditText().getText().length());
    }


    public void setSelection(int position) {
        getEditText().setSelection(position);
    }

    public void setCustomKeyboardView(CumKeyboardContainer customKeyboardView) {
        mEditText.setCmbView(customKeyboardView);
    }

    public void listener() {
        mRightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {

            }
        });
        mClearbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                getEditText().setText("");
                getEditText().requestFocus();
                if (clearClick != null) {
                    clearClick.clear();
                }
            }
        });

    }

    //初始化控件，设置初始监听事件
    private void setListener() {
        //禁用复制黏贴
        getEditText().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        getEditText().setTextIsSelectable(false);

        getEditText().setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        ininWatch();
    }


    /**
     * 设置左边标签的宽，测量输入的字符串的宽
     */
    public void setLableWide(String source) {
        TextPaint paint = mLabel.getPaint();
        int width = (int) Layout.getDesiredWidth(source, 0, source.length(), paint);
        android.view.ViewGroup.LayoutParams params = mLabel.getLayoutParams();
        params.width = width;
        mLabel.setLayoutParams(params);
    }

    /**
     * 设置左边标签的宽，测量输入的字符串的宽
     *
     * @spaceWidth 是动态设置标签与输入框的间隔
     */
    public void setLableWide(String source, float spaceWidth) {
        TextPaint paint = mLabel.getPaint();
        int width = (int) Layout.getDesiredWidth(source, 0, source.length(), paint);
        android.view.ViewGroup.LayoutParams params = mLabel.getLayoutParams();
        params.width = width + MyApplication.instance().dip2px(spaceWidth);
        mLabel.setLayoutParams(params);
    }

    public int getLableWide(String source) {
        TextPaint paint = mLabel.getPaint();
        return (int) Layout.getDesiredWidth(source, 0, source.length(), paint);
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    /**
     * @return 获取输入框中文本
     */
    public String getText() {
        if (getEditText() == null) {
            return "";
        }
        return getEditText().getEditableText().toString();
    }

    public void setHit(String hit) {
        if (getEditText() != null) {
            getEditText().setHint(hit);
        }
    }

    public void setTextSize(int size) {
        if (getEditText() != null) {
            getEditText().setTextSize(size);
        }
    }

    public void setEditable(boolean editable) {
        if (getEditText() != null) {
            getEditText().setFocusable(editable);
        }
    }

    /**
     * @param text 向输入框中填写文本内容
     */
    public void setText(String text) {
        if (getEditText() != null) {
            getEditText().setText(text);
        }
    }

    public void setLength(int length) {
        getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    public void setActionListener(OnEditorActionListener actionListener) {
        getEditText().setOnEditorActionListener(actionListener);
    }

    public void enabled(boolean enabled) {
        if (getEditText() != null) {
            getEditText().setEnabled(enabled);
            if (enabled) {
                if (clear && getEditText().getText().length() > 0) {
                    mClearbtn.setVisibility(View.VISIBLE);
                    if (mEditListener != null) {
                        mEditListener.onEdit(true);
                    }
                } else {
                    mClearbtn.setVisibility(View.GONE);
                    if (mEditListener != null) {
                        mEditListener.onEdit(false);
                    }
                }
            } else {
                mClearbtn.setVisibility(View.GONE);
                if (mEditListener != null) {
                    mEditListener.onEdit(false);
                }
            }
        }
    }

    public void showErrNote(String errMsg) {
        if (mErrNote.getVisibility() == VISIBLE) return;

        mErrNote.setText(errMsg);
        mErrNote.setVisibility(VISIBLE);
        startLeftAndRightShake(mErrNote);
        mErrNote.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nopeAnimator.isRunning())
                    nopeAnimator.cancel();
            }
        }, 1000);
        mErrNote.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideErrNote();
            }
        }, 2600);
    }

    public void hideErrNote() {
        mErrNote.setVisibility(GONE);
    }

    public void setLabelText(String text) {
        mLabel.setText(text);
    }

    public void setSelectClearORImage(boolean clearImage) {
        this.clearImage = clearImage;
    }

    public void setLeftText(String text) {
        mLeftTextView.setText(text);
    }

    public String getLeftText() {
        return mLeftTextView.getText().toString();
    }

    /**
     * 添加额外的TextWatcher方法
     * 注意watcher是一个列表
     *
     * @param watcher
     */
    public void addWatcher(TextWatcher watcher) {
        if (getEditText() != null) {
            getEditText().addTextChangedListener(watcher);
        }
    }

    private void ininWatch() {
        getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 用户输入时，锁屏重新计时
                if (clear && getEditText().isEnabled() && getEditText().getText().length() > 0) {
                    mClearbtn.setVisibility(View.VISIBLE);
                    if (clearImage) {
                        mRightButton.setVisibility(View.GONE);
                    }
                    if (mEditListener != null) {
                        mEditListener.onEdit(true);
                    }
                } else {
                    mClearbtn.setVisibility(View.GONE);
                    if (clearImage) {
                        mRightButton.setVisibility(View.VISIBLE);
                    }
                    if (mEditListener != null) {
                        mEditListener.onEdit(false);
                    }
                }
            }
        });
    }

    public FocusChange getFocusChange() {
        return focusChange;
    }

    public void setFocusChange(FocusChange focusChange) {
        this.focusChange = focusChange;
    }

    public interface FocusChange {
        void onFocusChange(View v, boolean isFocused);
    }

    public void setOnEditListener(OnEditListener mEditListener) {
        this.mEditListener = mEditListener;
    }

    public interface OnEditListener {
        void onEdit(boolean comList);
    }

    public void setEditTextClick(View.OnClickListener l) {
        if (getEditText() != null) {
            getEditText().setOnClickListener(l);
        }
    }

    public void setRightBtnClick(View.OnClickListener l) {
        if (mRightButton != null) {
            mRightButton.setOnClickListener(l);
        }
    }

    public void setRightIamgeBg(int id) {
        if (mRightButton != null) {
            mRightButton.setBackgroundResource(id);
        }
    }

    public void setRightIamgeClick(View.OnClickListener l) {
        if (mRightButton != null) {
            mRightButton.setOnClickListener(l);
        }
    }

    public void setInputType(String type) {
        getEditText().setInputType(MyInputType.valueOf(type).getType());
        if ("visible_password".equals(type)) {
            mRightButton.setBackgroundResource(R.drawable.pwd_eye_open);
        }
        if ("textPassword".equals(type)) {
            mRightButton.setBackgroundResource(R.drawable.pwd_eye_close);
        }
    }

    enum MyInputType {
        number("number",
                InputType.TYPE_CLASS_NUMBER),
        numberpassword("numberpassword",
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD),
        email("email",
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
        visible_password("visible_password",
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD),
        textPassword("textPassword",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD),
        numberDecimal("numberDecimal",
                InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER),
        phone("phone",
                InputType.TYPE_CLASS_PHONE);

        private String typeName;
        private int type;

        MyInputType(String typeName, int type) {
            this.typeName = typeName;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    public interface OnClik {
        void rightImageBtnOnClick();
    }

    private ClearClick clearClick;

    public interface ClearClick {
        void clear();
    }


    /**
     * 获取xml中属性length的 大小
     *
     * @return
     */
    public int getMaxLenth() {
        return maxlength;
    }

    public void setHorizontallyScrolling() {
        getEditText().setHorizontallyScrolling(false);
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setTextColor(int color) {
        if (mEditText != null) {
            mEditText.setTextColor(color);
        }
    }

    public void setLabelTextColor(int color) {
        if (mLabel != null) {
            mLabel.setTextColor(color);
        }
    }

    public void setHintTextColor(int color) {
        if (mEditText != null) {
            mEditText.setHintTextColor(color);
        }
    }

    /**
     * 设置单行or 多行显示
     */
    public void setEditTextSingleLine(boolean trueorfalse) {
        mEditText.setSingleLine(trueorfalse);
    }

    /**
     * 更换软键盘的换行键为那种方式 及设置显示的文字
     */
    public void setIme(int imeOptions, String labText) {
        mEditText.setImeOptions(imeOptions);
        mEditText.setImeActionLabel(labText, imeOptions);
    }

    public ClearClick getClearClick() {
        return clearClick;
    }

    public void setClearClick(ClearClick clearClick) {
        this.clearClick = clearClick;
    }

    public void setLeftImage(String url) {
        if ("".equals(url)) {
            mLeftImage.setVisibility(VISIBLE);
            mLeftImage.downloadCache2Sd(url);
        }
    }

    /**
     * 获得光标位置
     */
    public int getSelectionStart() {
        return getEditText().getSelectionStart();
    }

    public int getSelectionEnd() {
        return getEditText().getSelectionEnd();
    }

    public Editable getEditableText() {
        if (getEditText() != null) {
            return getEditText().getEditableText();
        }
        return null;
    }

    public AsyncImageView getLeftImage() {
        return mLeftImage;
    }


    /**
     * 开始左右抖动
     */
    private void startLeftAndRightShake(View view) {
        nopeAnimator = nope(view);
        nopeAnimator.setRepeatCount(ValueAnimator.INFINITE);
        nopeAnimator.setDuration(1000);
        nopeAnimator.start();
    }

    /**
     * 左右抖动动画 * * @param view * @return
     */
    public static ObjectAnimator nope(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );
        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).setDuration(500);
    }

}
