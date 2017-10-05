package com.ronda.posprinter.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.google.zxing.WriterException;
import com.ronda.posprinter.utils.QRCodeUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/04/17
 * Version: v1.0
 * <p>
 * <p>
 * ESC/POS 打印控制命令。
 * <p>
 * 打印机：一行可以打印32个英文字符或数字； 或者 可以打印16个中文字符。
 * 也就是说：打印机有32列，一个英文字符或数字占一列，一个汉字占两列
 * <p>
 */

public class PosCmd {

    private static String CHARSET_NAME = "GBK"; // 貌似pos打印机只能用GBK编码，UTF-8不可以

    private OutputStreamWriter writer;
    private OutputStream mOutputStream;

    public PosCmd(OutputStream out) {
        mOutputStream = out;
        try {
            writer = new OutputStreamWriter(out, CHARSET_NAME); // 把 OutputStream 转换成 OutputStreamWriter，有更多的 write() 重载方法，还可以指定编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void write(byte[] buf) throws IOException {
        writer.write(new String(buf, CHARSET_NAME));
        writer.flush();
    }


    public void flush() {
        try {
            if (writer != null)
                writer.flush();
            if (mOutputStream != null)
                mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (writer != null)
                writer.close();
            if (mOutputStream != null)
                mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 对打印机进行初始化
     *
     * @throws IOException
     */
    public void initPrinter() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        writer.flush();
    }

    /**
     * 打印文本
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        writer.write(text);
        writer.flush();
    }

    /**
     * 设置文本对齐方式
     *
     * @param align 打印位置  0：居左(默认) 1：居中 2：居右
     * @throws IOException
     */
    public void setAlignPosition(int align) throws IOException {
        writer.write(0x1B);
        writer.write(0x61);
        writer.write(align);
        writer.flush();
    }


    /**
     * 换行
     * 直接输出对应的字符即可,在打印订单详情的时候使用最多
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        writer.write('\n');
        writer.flush();
    }

    /**
     * 打印换行
     *
     * @return rows 需要打印的空行数
     * @throws IOException
     */
    protected void printLine(int rows) throws IOException {
        for (int i = 0; i < rows; i++) {
            writer.write("\n");
        }
        writer.flush();
    }

    /**
     * 制表符
     * 直接输出对应的字符即可,在打印订单详情的时候使用最多。可以让每一列的文字对齐。
     *
     * @param length
     * @throws IOException
     */
    public void printTab(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("\t");
        }
        writer.flush();
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printWordSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("  ");
        }
        writer.flush();
    }

    /**
     * 打印虚线分割线（长度就为32个）
     *
     * @throws IOException
     */
    public void printDivider() throws IOException {
//        for (int i = 0; i < 32; i++) {
//            writer.write("_");
//        }
        for (int i = 0; i < 16; i++) {
            writer.write("_ ");
        }

        writer.flush();
    }

    /**
     * 设置行间距
     * 行间距为 [ n × 纵向或横向移动单位] 英寸
     *
     * @param gap 表示行间距为gap个像素点[0,255]
     * @throws IOException
     */
    public void setLineGap(int gap) throws IOException {
        writer.write(0x1B);
        writer.write(0x33);
        writer.write(gap);
        writer.flush();
    }

    /**
     * 是否加粗
     * (蓝牙打印机上无效)
     *
     * @param flag
     * @return
     * @throws IOException
     */
    public void setBold_ESC(boolean flag) throws IOException {
        if (flag) {
            //加粗
            writer.write(0x1B);
            writer.write(0x45);
            writer.write(0x01);
            writer.flush();
        } else {
            //常规粗细
            writer.write(0x1B);
            writer.write(0x45);
            writer.write(0);
            writer.flush();
        }
    }

    /**
     * 是否双重打印（和加粗效果一样）
     * (蓝牙打印机和普通有线打印机上都无效)
     *
     * @param flag
     * @return
     * @throws IOException
     */
    public void setDoublePrint_ESC(boolean flag) throws IOException {
        if (flag) {
            //加粗
            writer.write(0x1B);
            writer.write(0x47);
            writer.write(0x01);
            writer.flush();
        } else {
            //常规粗细
            writer.write(0x1B);
            writer.write(0x47);
            writer.write(0);
            writer.flush();
        }
    }

    /**
     * 设置或取消倍高和倍宽
     * (蓝牙打印机上无效)
     *
     * @param flag
     * @throws IOException
     */
    public void setDoubleSize_ESC(boolean flag) throws IOException {
        if (flag) {
            //倍高和倍宽
            writer.write(0x1B);
            writer.write(0x21);
            writer.write(0x30);// 倍宽+倍高的值的和
            writer.flush();
        } else {
            writer.write(0x1B);
            writer.write(0x21);
            writer.write(0x00);
            writer.flush();
        }
    }

    /**
     * 设置或取消倍高和倍宽
     * (蓝牙打印机上有效， 普通有线的打印机也有效)
     *
     * @param flag
     * @throws IOException
     */
    public void setDoubleSize_FS(boolean flag) throws IOException {
        if (flag) {
            //倍高和倍宽
            writer.write(0x1C);
            writer.write(0x21);
            writer.write(0x0C);// 倍宽+倍高的值的和
            writer.flush();
        } else {
            writer.write(0x1C);
            writer.write(0x21);
            writer.write(0x00);
            writer.flush();
        }
    }


    /**
     * 打印条形码
     *
     * @param height   条码的高度[0~255], 打印机默认是162
     * @param location 选择HRI字符的打印位置 [0,3]
     *                 0:不打印
     *                 1:条码上方
     *                 2:条码下方
     *                 3:条码上下方都打印
     * @param type     条码的类型[0~6].建议取值为4，这样字符个数范围[1,255],而且字符种类有0~9,A~Z,还有一些其他字符,没有小写字母。
     *                 注意其他条码的类型有字符个数限制和字符种类限制
     * @param msg      条码的内容
     * @throws IOException
     */
    public void printBarcode(int height, int location, int type, String msg) throws IOException {
        // 设置条码高度
        writer.write(0x1d);
        writer.write(0x68);
        writer.write(height);

        // 选择HRI字符的打印位置
        writer.write(0x1d);
        writer.write(0x48);
        writer.write(location);

        // 打印条码
        writer.write(0x1d);
        writer.write(0x6B);
        writer.write(type);
        writer.write(msg); //条形码数字
        writer.write(0x00);
        writer.write("\n");
    }

    /**
     * 打印并走纸
     * 打印缓冲区数据并走纸 [ length × 纵向或横向移动单位] 英寸
     * 注意：
     * 最大走纸距离是956 mm。 如果超出这个距离，取最大距离。
     *
     * @param length 范围[0, 255]
     * @throws IOException
     */
    public void skipPaper(int length) throws IOException {
        writer.write(0x1B);
        writer.write(0x4A);
        writer.write(length);
    }

    /**
     * 设置绝对打印位置(看不太懂，不太会用，它的两个形参很奇怪的)
     * 将当前位置设置到距离行首（nL + nH×256）× (横向或纵向移动单位)处
     *
     * @param nL
     * @param nH
     * @throws IOException
     */
    public void setAbsolutePosition(int nL, int nH) throws IOException {
        writer.write(0x1B);
        writer.write(0x24);
        writer.write(nL);
        writer.write(nH);
        writer.flush();

    }


    /**
     * 设置横向跳格位置
     * 应该要配合 setHorizaontalLocation() 一起使用.eg:
     * printText("名称");
     * <p>
     * setHorizontalJump(9);
     * setHorizaontalLocation();
     * printText("数量");
     * <p>
     * setHorizontalJump(18);
     * setHorizaontalLocation();
     * printText("价格");
     * <p>
     * setHorizontalJump(28);
     * setHorizaontalLocation();
     * printText("小计");
     *
     * @param step 取值范围是：[0,32]，因为小票的宽度最大就是32列。当step取值为8时，后面的字符位置是从第九列开始的。
     * @throws IOException
     */
    public void setHorizontalJump(int step) throws IOException {
        // 1B 44 n1... nk 00
        writer.write(0x1B);
        writer.write(0x44);

        writer.write(step); // 这个其实是可以设置多个的

        writer.write(0x00);
        writer.flush();
    }

    /**
     * 水平定位
     * 移动打印位置到下一个水平定位点的位置。如果没有设置下一个水平定位点的位置，则该命令被忽略。
     *
     * @throws IOException
     */
    public void setHorizaontalLocation() throws IOException {
        writer.write(0x09);
        writer.flush();
    }

    public void printMyContent() throws IOException {
        initPrinter();

        // 测试小票一行最多可以打印多少个英文或中文字符
//        printText("1234567890123456789012345678901234567890");
//        printLine();
//
//        printText("一二三四五六七八九十一二三四五六七八九十");
//        printLine();


        // 测试标题居中
//        setAlignPosition(1);
//        setDoubleSize_FS(true);
//        printText("理工饭店");
//        setDoubleSize_FS(false);
//        printLine();


        // 打印小票表头
//        setHorizontalJump(0);
//        setHorizaontalLocation();
//        printText("名称");
//
//        setHorizontalJump(9);
//        setHorizaontalLocation();
//        printText("数量");
//
//        setHorizontalJump(18);
//        setHorizaontalLocation();
//        printText("价格");
//
//        setHorizontalJump(28);
//        setHorizaontalLocation();
//        printText("小计");


        // 打印条形码
//        printBarcode(162,2 , 4, "1234567890");

        // 测试打印二维码
        //printQRCode("123456");

        // 走纸
//        skipPaper(10);

        //打印分割线
//        printDivider();


        //测试绝对定位，但是效果很奇怪，不太懂它的两个形参的意思
//        setAbsolutePosition(0, 0);
//        printText("数量");
//
//        setAbsolutePosition(100, 1);
//        printText("金额");
//
//        setAbsolutePosition(200, 2);
//        printText("小计");


        // 测试制表符 无效
//        printText("名称");
//        printTab(1);
//        //setHorizaontalLocation();
//        printText("数量");
//        printTab(2);
//        printText("小计");
//        printTab(3);
//        printText("金额");


        printLine();


    }


    /**
     * 打印二维码
     *
     * @param qrData 二维码的内容
     * @throws IOException
     */
    protected void printQRCode(String qrData) throws IOException {
        int moduleSize = 8;
        int length = qrData.getBytes(CHARSET_NAME).length;

        //打印二维码矩阵
        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

    }

    public void printQRCode1(String msg) {
        try {
            // 因为 QRCodeUtil.createQRCode() 生成的背景是透明的，所以必须要去除透明度，使用 compressPic()
            mOutputStream.write(draw2PxPoint(compressPic(QRCodeUtil.createQRCode(msg, 240))));
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printImage(Bitmap bitmap){
        try {
            // 去除透明度，使用 compressPic()
            mOutputStream.write(draw2PxPoint(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //====================打印图片相关 http://www.jianshu.com/p/c0b6d1a4823b =====================

    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param bitmap
     */
    public static Bitmap compressPic(Bitmap bitmap) {
        // 获取这个图片的宽和高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 指定调整后的宽度和高度
        int newWidth = 240;
        int newHeight = 240;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmap, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }

    /**
     * 灰度图片黑白化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    public static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    public static int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);  //灰度转化公式
        return gray;
    }

    /*************************************************************************
     * 假设一个240*240的图片，分辨率设为24, 共分10行打印
     * 每一行,是一个 240*24 的点阵, 每一列有24个点,存储在3个byte里面。
     * 每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     **************************************************************************/
    /**
     * 把一张Bitmap图片转化为打印机可以打印的字节流
     *
     * @param bmp
     * @return
     */
    public static byte[] draw2PxPoint(Bitmap bmp) {
        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] data = new byte[size];
        int k = 0;
        //设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            //打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); //nL
            data[k++] = (byte) (bmp.getWidth() / 256); //nH
            //对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                //每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    //每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;//换行
        }
        //System.arraycopy();
        return data;
    }
}
