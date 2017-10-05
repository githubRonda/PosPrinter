package com.ronda.posprinter.engine;


import android.graphics.BitmapFactory;

import com.ronda.posprinter.R;
import com.ronda.posprinter.base.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/06/20
 * Version: v1.0
 * <p>
 * 职责：使用 PosCmd 指令，把待打印的源数据封装成打印数据(byte[])，然后直接使用流的 write() 方法即可
 */

public class PrintUtils {


    public static byte[] generateBillData(String orderNumber, /*List<CartBean> cartData,*/ String discount) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PosCmd cmd = new PosCmd(out);

        try {
            cmd.initPrinter();

            //标题
            cmd.setAlignPosition(1);
            cmd.setDoubleSize_FS(true);
            cmd.printText("水果店");
            cmd.setDoubleSize_FS(false);
            cmd.printLine();

            cmd.setAlignPosition(1);
            cmd.printText("结账清单");
            cmd.printLine();

            cmd.setAlignPosition(0);
            cmd.printText("结账时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            cmd.printLine();

            cmd.printText("单号：" + orderNumber);
            cmd.printLine();

            //收银员 日期 时间
            cmd.printText("收银员:0001");
            cmd.printLine();


            // 打印小票表头
            cmd.printDivider();
            cmd.setHorizontalJump(0);
            cmd.setHorizaontalLocation();
            cmd.printText("名称");
            cmd.setHorizontalJump(9);
            cmd.setHorizaontalLocation();
            cmd.printText("数量");
            cmd.setHorizontalJump(18);
            cmd.setHorizaontalLocation();
            cmd.printText("价格");
            cmd.setHorizontalJump(28);
            cmd.setHorizaontalLocation();
            cmd.printText("小计");
            cmd.printLine();
            cmd.printDivider();


            // 数据部分
            String totalNumber = "0";
            String totalMoney = "0";

//            for (CartBean bean : cartData) {
//                cmd.setHorizontalJump(0);
//                cmd.setHorizaontalLocation();
//                cmd.printText(bean.getName());
//                cmd.setHorizontalJump(9);
//                cmd.setHorizaontalLocation();
//                cmd.printText(bean.getWeight());
//                cmd.setHorizontalJump(18);
//                cmd.setHorizaontalLocation();
//                cmd.printText(bean.getSell_price());
//                cmd.setHorizontalJump(28);
//                cmd.setHorizaontalLocation();
//                cmd.printText(bean.getMoney());
//
//                cmd.printLine();
//
//                totalNumber = MathCompute.add(totalNumber, bean.getWeight());
//                totalMoney = MathCompute.add(totalMoney, bean.getMoney());
//            }


            cmd.printDivider();

            cmd.printText("总数量：" + totalNumber);
            cmd.printWordSpace(1);
            cmd.printText("总金额：" + totalMoney);
            cmd.printLine();

            cmd.printText("折扣：" + discount);


            //分割线
            cmd.printDivider();
            cmd.printLine();

            cmd.printText("谢谢光临 欢迎下次再来");
            cmd.printLine();
            cmd.printText("技术支持：xxxx科技有限公司");

            cmd.printLine();
            cmd.skipPaper(25);

            cmd.flush();
            cmd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }


    public static byte[] generateQRCodeData(String msg){


        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PosCmd cmd = new PosCmd(out);

        try {
            cmd.initPrinter();
//            cmd.printQRCode1(msg);
            cmd.printImage(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.ic_launcher));
        }
        catch(Exception e){

        }

        return out.toByteArray();
    }

}
