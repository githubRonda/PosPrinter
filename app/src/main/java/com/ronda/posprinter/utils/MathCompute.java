package com.ronda.posprinter.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by lrd on 0027,2016/9/27.
 * 这个类用于精确计算
 * 本项目中只涉及到 加法 、 减法 、 乘法（售价*折扣 和 重量*单价，只涉及到这两种乘法，并且要求立即保留两位小数，不能四舍五入）
 */
public class MathCompute {


    //这个类不能实例化
    private MathCompute() {
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));//使用String类型的构造器是比使用double类型的构造器更精确
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static String add(String v1, String v2) {
        // 库存报损计算总数量
        if (v1 == null || v1.isEmpty() || v1.equals(".")) {
            v1 = "0";
        }
        if (v2 == null || v2.isEmpty() || v2.equals(".")) {
            v2 = "0";
        }
        BigDecimal b1 = new BigDecimal(v1);//使用String类型的构造器是比使用double类型的构造器更精确
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static String sub(String v1, String v2) {
        if (v1 == null || v1.isEmpty() || v1.equals(".")) {
            v1 = "0";
        }
        if (v2 == null || v2.isEmpty() || v2.equals(".")) {
            v2 = "0";
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).toString();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static String mul(String v1, String v2) {
        // 避免出现异常(入库实时计算小计时)
        if (v1 == null || v1.isEmpty() || v1.equals(".")) {
            v1 = "0";
        }
        if (v2 == null || v2.isEmpty() || v2.equals(".")) {
            v2 = "0";
        }

        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).toString();
    }

    public static String mul(String v1, String... values) {
        // 避免出现异常(入库实时计算小计时)
        if (v1 == null || v1.isEmpty() || v1.equals(".")) {
            v1 = "0";
        }
        BigDecimal b1 = new BigDecimal(v1);

        for (String value : values) {
            if (value == null || value.isEmpty() || value.equals(".")) {
                value = "0";
            }

            BigDecimal b2 = new BigDecimal(value);

            b1 = b1.multiply(b2);
        }

        return b1.toString();
    }

    /**
     * 精确到小数点后两位，即保留两位小数，
     *
     * @param b 需要四舍五入的BigDecimal对象
     * @return 直接返回String类型，因为这个结果要么是显示在界面上，要么是传给后台
     */
    public static String roundHalfUp_scale2(BigDecimal b) {
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();//ROUND_FLOOR不是四舍五入，而是直接舍去
    }

    public static String roundHalfUp_scale2(String value) {
        if (value.isEmpty() || ".".equals(value)) value = "0";
        BigDecimal b = new BigDecimal(value);
        return roundHalfUp_scale2(b);
    }

    public static String roundHalfUp_scale2(double value) {
        BigDecimal b = new BigDecimal(Double.toString(value));
        return roundHalfUp_scale2(b);
    }


    public static String roundFloor_scale2(BigDecimal b) {
        return b.setScale(2, BigDecimal.ROUND_FLOOR).toString(); //ROUND_FLOOR不是四舍五入，而是直接舍去
    }

    public static String roundFloor_scale2(String value) {
        if (value.isEmpty() || ".".equals(value)) value = "0";
        BigDecimal b = new BigDecimal(value);
        return roundFloor_scale2(b);
    }


    public static String roundHalfUp_scale1(BigDecimal b) {
        return b.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static String roundHalfUp_scale1(String value) {
        if (value.isEmpty() || ".".equals(value)) value = "0";
        BigDecimal b = new BigDecimal(value);
        return roundHalfUp_scale1(b);
    }

    public static String roundHalfUp_scale1(double value) {
        BigDecimal b = new BigDecimal(Double.toString(value));
        return roundHalfUp_scale1(b);
    }

    public static String roundFloor_scale1(BigDecimal b) {
        return b.setScale(1, BigDecimal.ROUND_FLOOR).toString();
    }

    public static String roundFloor_scale1(String value) {
        if (value.isEmpty() || ".".equals(value)) value = "0";
        BigDecimal b = new BigDecimal(value);
        return roundFloor_scale1(b);
    }

    //逢1进，这个是保留1位小数。eg : 1.01 回变成 1.1。而1.00,变成1.0
    public static String roundUp_scale1(BigDecimal b) {
        return b.setScale(1, BigDecimal.ROUND_UP).toString();
    }

    public static String roundUp_scale1(String value) {
        if (value.isEmpty() || ".".equals(value)) value = "0";
        BigDecimal b = new BigDecimal(value);
        return roundUp_scale1(b);
    }

    public static String roundUp_scale1(double value) {
        BigDecimal b = new BigDecimal(Double.toString(value));
        return roundUp_scale1(b);
    }

    /**
     * 把浮点数的小数点后移两位然后转成整数(精确)
     * 因为 System.out.println((int)(1.13*100));会发现结果是112，而不是113，原因就是1.13*100是112.99999999999999
     * （这个方法是另外两个重载函数的基方法）
     *
     * @param doubleValue
     * @return
     */
    public static int yuanToFen(BigDecimal doubleValue) {
        return new BigDecimal("100").multiply(doubleValue).intValue();
    }

    public static int yuanToFen(String doubleValue) {
        return yuanToFen(new BigDecimal(doubleValue));
    }

    public static int yuanToFen(double value) {
        return yuanToFen(new BigDecimal(Double.toString(value)));
    }

    public static double fenToYuan(BigDecimal intValue) {
        return intValue.divide(new BigDecimal("100")).doubleValue();
    }

    public static double fenToYuan(String intValue) {
        return fenToYuan(new BigDecimal(intValue));
    }

    public static double fenToYuan(int intValue) {
        return fenToYuan(new BigDecimal(Integer.toString(intValue)));
    }

    /**
     * 判断当前字符串中的小数部分是否超过了两位
     * 小数，最多两位小数,如：10.2, 0.25, 20
     *
     * @param str
     */
    public static boolean isDecimalTwo(String str) {
        return str.matches("^[1-9]\\d*(\\.\\d{0,2})?|0\\.(0[1-9]?|[1-9]\\d?)?$");
    }

    /**
     * 把一个数字前面加上"0"字符串转成两位数字的字符串(用于日期选择)
     *
     * @param num
     * @return
     */
    public static String trans2Digist(int num) {
        return num < 10 ? "0" + num : num + "";
    }

    /**
     * 转成百分数
     *
     * @param s
     * @return
     */
    public static String trans2Percent(String s) {
        // 乘以100 再保留两位小数， 最后在追加百分号
        if (s.isEmpty() || ".".equals(s)) s = "0";
        double d = new BigDecimal("100").multiply(new BigDecimal(s)).doubleValue();
        DecimalFormat df = new DecimalFormat("#0.00");
        String result = df.format(d) + "%";
        return result;
    }
}
