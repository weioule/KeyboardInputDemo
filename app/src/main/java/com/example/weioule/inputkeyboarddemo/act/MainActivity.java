package com.example.weioule.inputkeyboarddemo.act;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weioule.inputkeyboarddemo.R;
import com.example.weioule.inputkeyboarddemo.keyboard.CumKeyboardContainer;
import com.example.weioule.inputkeyboarddemo.util.IdCardUtil;
import com.example.weioule.inputkeyboarddemo.view.CEditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class MainActivity extends BaseTitleActivity {

    protected static final String ID_INFO_BIRTHDAY = "id_info_birthday";
    protected static final String ID_INFO_SEX = "id_info_sex";
    protected CumKeyboardContainer mCustomKeyboardView;
    private boolean mBackKeyPressed = false;
    protected CEditText mIndentityCard;
    protected TextView mIdInfo, okBtn;

    @Override
    protected void initView() {
        addTitleText("实名认证");
        mIndentityCard = findViewById(R.id.indentity_card);
        mIdInfo = findViewById(R.id.input_id_info);
        okBtn = findViewById(R.id.ok_button);

        mCustomKeyboardView = new CumKeyboardContainer(this, getInputType());
        mCustomKeyboardView.attachKeyBoardView();
        mIndentityCard.setCustomKeyboardView(mCustomKeyboardView);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn();
            }
        });
        findViewById(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyBoard();
            }
        });
    }

    protected void clickBtn() {
        IdCardUtil idCardUtil = new IdCardUtil(mIndentityCard.getText());
        int correct = idCardUtil.isCorrect();
        if (0 == correct) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra(ID_INFO_BIRTHDAY, idCardUtil.getBirthday());
            intent.putExtra(ID_INFO_SEX, idCardUtil.getSex());
            startActivity(intent);
        } else {
            mIndentityCard.showErrNote(idCardUtil.getErrMsg());
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    protected boolean hintKeyBoard() {
        if (mCustomKeyboardView != null && mCustomKeyboardView.getVisibility() == View.VISIBLE) {
            mCustomKeyboardView.setCmbVisibility(View.GONE);
            return true;
        }
        return false;
    }

    protected String getInputType() {
        return CumKeyboardContainer.IDCERT_TYPE;
    }

    @Override
    public void onBackPressed() {
        if (hintKeyBoard()) return;
        if (!mBackKeyPressed) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {// 延时两秒，如果超出则取消取消上一次的记录

                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        } else {// 退出程序
            this.finish();
            System.exit(0);
        }
    }
}
