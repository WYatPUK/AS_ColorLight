package com.example.a37754.cl1;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 37754 on 2016/10/21.
 * LED Virtual Machine 静态类
 * 为了保证信息不堆积和纠错能力，每1s会进行一次状态的发送，一次性通知比如微信和短信，可以用1s来展示（仅有1s哦~~）
 * 而对于设置，每次保存设置的时候应该去发送一下信息的，而如果发生小概率事件没有更新，
 *          则给用户一个按钮，把所有设置堆到信息队列里
 * 信息队列用于维护优先级，信息队列的第0位永远是“更新状态”或“default”，判断如果不是default，则
 *          判断是否有后面的更新事件，比如更新设置。
 * 信息事件用String类，没有其他的信息
 */

public class LEDVM {
    static public boolean telIn = true;
    static public String telNum = "000";
    static Activity x;

    static ArrayList<String> MsgQueue;
    static String[] StateString; //优先级：0位是电话，1位是短信，2位是微信，3位是基础状态

    static public void Set_Activity(Activity x_) {
        x = x_;
    }

    static public void Initiate(Activity x_) {
        x = x_;
        MsgQueue = new ArrayList<String>();
        MsgQueue.add("default");
        StateString = new String[4];
        for (int i=0; i<4; i++) {
            StateString[i] = null;
        }
        StateString[3] = "Learning";//这一位是基础状态，不能为null，ColorLight也因此判断基础状态
    }

    static public void setTel (boolean telIn_, String telNum_) {
        Log.d(ColorLight.TAG, "setTel: " + telNum_);
        if (telIn_) {
            telIn = true;
            telNum = telNum_;
            if (telNum.length() == 11) {
                StateString[0] = "Tel:" + telNum;
            } else if (telNum.length() == 8) {
                StateString[0] = "Tel:" + "000" + telNum;
            } else {
                //电话号码错误
                //这里根据需要来处理，一般接常用电话都是11或8位
            }
            Refresh_Queue();
        } else {
            if (telIn) {
                //挂断了
                telIn = false;
                StateString[0] = "TelDown";
                Refresh_Queue();
            } else {
                //什么都不用做
            }
        }
    }

    static void Refresh_Queue() {
        //根据StateString的形式，判断要不要加消息到MsgQueue里
        if (StateString[0]!=null && StateString[0].startsWith("Tel:")) {
            MsgQueue.set(0, StateString[0]); //打电话的时候不能进行设置的上传，保证用户一定能接收到来电信息
        } else if (StateString[0]!=null && StateString[0].equals("TelDown")) {
            MsgQueue.set(0, StateString[0]);
            StateString[0] = null; //释放掉（其实不做teldown也无所谓，后面的状态会覆盖他的，只是这样可以加一次特技（比如闪烁500ms）
        }  else if (StateString[1]!=null && StateString[1].startsWith("Msg:")) {
            MsgQueue.set(0, StateString[1]);
            StateString[1] = null; //释放掉，短信只展示一次
        } else if (StateString[2]!=null && StateString[2].startsWith("WeC:")) {
            MsgQueue.set(0, StateString[2]);
            StateString[2] = null; //释放掉，微信只展示一次
        } else {
            //只剩基础状态了，就default就好
            MsgQueue.set(0, "default");
        }
    }

    static public String Get_Next_Msg() {
        if (!MsgQueue.get(0).equals("default")) {
            String x = MsgQueue.get(0);
            Refresh_Queue();//保证电话事件能够一直提醒
            return x;
        } else {
            if (MsgQueue.size() == 1) {
                return StateString[3];
            } else {
                String x = MsgQueue.get(1);
                MsgQueue.remove(1);
                return x;
            }
        }
    }

    static public void Add_Setting(String x) {
        //为了安全起见，最好在这里判断一下是否是标准命令
        MsgQueue.add(x);
    }
}
