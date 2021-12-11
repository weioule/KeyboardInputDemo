package com.example.weioule.inputkeyboarddemo.util;

import android.content.Context;
import android.text.TextUtils;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class IdCardUtil {
    private String idCardNum = null;

    private static int IS_EMPTY = 1;
    private static int LEN_ERROR = 2;
    private static int CHAR_ERROR = 3;
    private static int DATE_ERROR = 4;
    private static int CHECK_BIT_ERROR = 5;

    private String[] errMsg = new String[]{"身份证完全正确！",
            "身份证为空！",
            "身份证长度不正确！",
            "身份证有非法字符！",
            "身份证中出生日期不合法！",
            "身份证校验位错误！"};

    private int error = 0;

    /**
     * 构造方法。
     *
     * @param idCardNum
     */
    public IdCardUtil(String idCardNum) {
        this.idCardNum = idCardNum.trim();
        if (!TextUtils.isEmpty(this.idCardNum)) {
            this.idCardNum = this.idCardNum.replace("x", "X");
        }
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
        if (!TextUtils.isEmpty(this.idCardNum)) {
            this.idCardNum = this.idCardNum.replace("x", "X");
        }
    }

    /**
     * 得到身份证详细错误信息。
     *
     * @return 错误信息。
     */
    public String getErrMsg() {
        return this.errMsg[this.error];
    }

    /**
     * 是否为空。
     *
     * @return true: null  false: not null;
     */
    public boolean isEmpty() {
        if (this.idCardNum == null)
            return true;
        else
            return this.idCardNum.trim().length() > 0 ? false : true;
    }

    /**
     * 身份证长度。
     *
     * @return
     */
    public int getLength() {
        return this.isEmpty() ? 0 : this.idCardNum.length();
    }

    /**
     * 身份证长度。
     *
     * @return
     */
    public int getLength(String str) {
        return this.isEmpty() ? 0 : str.length();
    }

    /**
     * 是否是15位身份证。
     *
     * @return true: 15位  false：其他。
     */
    public boolean is15() {
        return this.getLength() == 15;
    }

    /**
     * 是否是18位身份证。
     *
     * @return true: 18位  false：其他。
     */
    public boolean is18() {
        return this.getLength() == 18;
    }

    /**
     * 得到身份证的省份代码。
     *
     * @return 省份代码。
     */
    public String getProvince() {
        return this.isCorrect() == 0 ? this.idCardNum.substring(0, 2) : "";
    }

    /**
     * 得到身份证的城市代码。
     *
     * @return 城市代码。
     */
    public String getCity() {
        return this.isCorrect() == 0 ? this.idCardNum.substring(2, 4) : "";
    }

    /**
     * 得到身份证的区县代码。
     *
     * @return 区县代码。
     */
    public String getCountry() {
        return this.isCorrect() == 0 ? this.idCardNum.substring(4, 6) : "";
    }

    /**
     * 得到身份证的出生年份。
     *
     * @return 出生年份。
     */
    public String getYear() {
        if (this.isCorrect() != 0)
            return "";

        if (this.getLength() == 15) {
            return "19" + this.idCardNum.substring(6, 8);
        } else {
            return this.idCardNum.substring(6, 10);
        }
    }

    /**
     * 得到身份证的出生月份。
     *
     * @return 出生月份。
     */
    public String getMonth() {
        if (this.isCorrect() != 0)
            return "";

        if (this.getLength() == 15) {
            return this.idCardNum.substring(8, 10);
        } else {
            return this.idCardNum.substring(10, 12);
        }
    }

    /**
     * 得到身份证的出生日子。
     *
     * @return 出生日期。
     */
    public String getDay() {
        if (this.isCorrect() != 0)
            return "";

        if (this.getLength() == 15) {
            return this.idCardNum.substring(10, 12);
        } else {
            return this.idCardNum.substring(12, 14);
        }
    }

    /**
     * 得到身份证的出生日期。
     *
     * @return 出生日期。
     */
    public String getBirthday() {
        if (this.isCorrect() != 0)
            return "";

        if (this.getLength() == 15) {
            return "19" + this.idCardNum.substring(6, 12);
        } else {
            return this.idCardNum.substring(6, 14);
        }
    }

    /**
     * 得到身份证的出生年月。
     *
     * @return 出生年月。
     */
    public String getBirthMonth() {
        return getBirthday().substring(0, 6);
    }

    /**
     * 得到身份证的顺序号。
     *
     * @return 顺序号。
     */
    public String getOrder() {
        if (this.isCorrect() != 0)
            return "";

        if (this.getLength() == 15) {
            return this.idCardNum.substring(12, 15);
        } else {
            return this.idCardNum.substring(14, 17);
        }
    }

    /**
     * 得到性别。
     *
     * @return 性别：1－男  2－女
     */
    public String getSex() {
        if (this.isCorrect() != 0)
            return "";

        int p = Integer.parseInt(getOrder());
        if (p % 2 == 1) {
            return "男";
        } else {
            return "女";
        }
    }

    /**
     * 得到性别值。
     *
     * @return 性别：1－男  2－女
     */
    public String getSexValue() {
        if (this.isCorrect() != 0)
            return "";

        int p = Integer.parseInt(getOrder());
        if (p % 2 == 1) {
            return "1";
        } else {
            return "2";
        }
    }

    /**
     * 得到校验位。
     *
     * @return 校验位。
     */
    public String getCheck() {
        if (!this.isLenCorrect())
            return "";

        String lastStr = this.idCardNum.substring(this.idCardNum.length() - 1);
        if ("x".equals(lastStr)) {
            lastStr = "X";
        }
        return lastStr;
    }

    /**
     * 得到15位身份证。
     *
     * @return 15位身份证。
     */
    public String to15() {
        if (this.isCorrect() != 0)
            return "";

        if (this.is15())
            return this.idCardNum;
        else
            return this.idCardNum.substring(0, 6) + this.idCardNum.substring(8, 17);
    }

    /**
     * 得到18位身份证。
     *
     * @return 18位身份证。
     */
    public String to18() {
        if (this.isCorrect() != 0)
            return "";

        if (this.is18())
            return this.idCardNum;
        else
            return this.idCardNum.substring(0, 6) + "19" + this.idCardNum.substring(6) + this.getCheckBit();
    }

    /**
     * 得到18位身份证。
     *
     * @return 18位身份证。
     */
    public static String toNewIdCard(String tempStr) {
        if (tempStr.length() == 18)
            return tempStr.substring(0, 6) + tempStr.substring(8, 17);
        else
            return tempStr.substring(0, 6) + "19" + tempStr.substring(6) + getCheckBit(tempStr);
    }

    /**
     * 校验身份证是否正确
     *
     * @return 0：正确
     */
    public int isCorrect() {
        if (this.isEmpty()) {
            this.error = IdCardUtil.IS_EMPTY;
            return this.error;
        }

        if (!this.isLenCorrect()) {
            this.error = IdCardUtil.LEN_ERROR;
            return this.error;
        }

        if (!this.isCharCorrect()) {
            this.error = IdCardUtil.CHAR_ERROR;
            return this.error;
        }

        if (!this.isDateCorrect()) {
            this.error = IdCardUtil.DATE_ERROR;
            return this.error;
        }

        if (this.is18()) {
            if (!this.getCheck().equals(this.getCheckBit())) {
                this.error = IdCardUtil.CHECK_BIT_ERROR;
                return this.error;
            }
        }

        return 0;
    }


    private boolean isLenCorrect() {
        return this.is15() || this.is18();
    }

    /**
     * 判断身份证中出生日期是否正确。
     *
     * @return
     */
    private boolean isDateCorrect() {

        /*非闰年天数*/
        int[] monthDayN = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        /*闰年天数*/
        int[] monthDayL = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        int month;
        if (this.is15()) {
            month = Integer.parseInt(this.idCardNum.substring(8, 10));
        } else {
            month = Integer.parseInt(this.idCardNum.substring(10, 12));
        }

        int day;
        if (this.is15()) {
            day = Integer.parseInt(this.idCardNum.substring(10, 12));
        } else {
            day = Integer.parseInt(this.idCardNum.substring(12, 14));
        }

        if (month > 12 || month <= 0) {
            return false;
        }

        if (this.isLeapyear()) {
            if (day > monthDayL[month - 1] || day <= 0)
                return false;
        } else {
            if (day > monthDayN[month - 1] || day <= 0)
                return false;
        }

        return true;
    }

    /**
     * 得到校验位。
     *
     * @return
     */
    private String getCheckBit() {
        if (!this.isLenCorrect())
            return "";

        String temp = null;
        if (this.is18())
            temp = this.idCardNum;
        else
            temp = this.idCardNum.substring(0, 6) + "19" + this.idCardNum.substring(6);


        String checkTable[] = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        int[] wi = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};
        int sum = 0;

        for (int i = 0; i < 17; i++) {
            String ch = temp.substring(i, i + 1);
            sum = sum + Integer.parseInt(ch) * wi[i];
        }

        int y = sum % 11;

        return checkTable[y];
    }


    /**
     * 得到校验位。
     *
     * @return
     */
    private static String getCheckBit(String str) {

        String temp = null;
        if (str.length() == 18)
            temp = str;
        else
            temp = str.substring(0, 6) + "19" + str.substring(6);


        String checkTable[] = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        int[] wi = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};
        int sum = 0;

        for (int i = 0; i < 17; i++) {
            String ch = temp.substring(i, i + 1);
            sum = sum + Integer.parseInt(ch) * wi[i];
        }

        int y = sum % 11;

        return checkTable[y];
    }


    /**
     * 身份证号码中是否存在非法字符。
     *
     * @return true: 正确  false：存在非法字符。
     */
    private boolean isCharCorrect() {
        boolean iRet = true;

        if (this.isLenCorrect()) {
            byte[] temp = this.idCardNum.getBytes();

            if (this.is15()) {
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i] < 48 || temp[i] > 57) {
                        iRet = false;
                        break;
                    }
                }
            }

            if (this.is18()) {
                for (int i = 0; i < temp.length; i++) {
                    if (temp[i] < 48 || temp[i] > 57) {
                        if (i == 17 && temp[i] != 88) {
                            iRet = false;
                            break;
                        }
                    }
                }
            }
        } else {
            iRet = false;
        }
        return iRet;
    }

    /**
     * 判断身份证的出生年份是否未闰年。
     *
     * @return true ：闰年  false 平年
     */
    private boolean isLeapyear() {
        String temp;

        if (this.is15()) {
            temp = "19" + this.idCardNum.substring(6, 8);
        } else {
            temp = this.idCardNum.substring(6, 10);
        }

        int year = Integer.parseInt(temp);

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            return true;
        else
            return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
