package com.vhyu.get_hpa;

/**
 * Created by vhyu on 2018/1/29.
 */


public class Struct_Record {
    public int action_name;
    public float pressure1;//pressure1 表示的是单击的压力，或是滑动的开始压力
    public float pressure2 ;//pressure2 表示的是滑动过程中的压力
    public float pressure3;//pressure3 表示的是滑动的结束压力
    public float speeed;
    public float startX;
    public float startY;
    public float endX;
    public float endY;
    public float extentX;//幅度X
    public float extentY;//幅度Y
    public String rec;//将所有的属性变成字符串
    public int record_num;//当前record数目

    public Struct_Record(){
        action_name = 0;
        pressure1 = -1;
        pressure2 = -1;
        pressure3 = -1;
        speeed = -1;
        startX = -1;
        startY = -1;
        endX = -1;
        endY = -1;
        extentX = -1;
        extentY = -1;
        record_num = 0;
    }

    public String getStr(int label){
//        return action_name+" \n"+pressure1+" \n"+pressure2+" \n"+pressure3+" \n"+speeed+" \n"+"("+startX+","+startY+")\n"+"("+endX+","+endY+")\n["+extentX+","+extentY+"]\n";
        //第一个参数是label
        return String.valueOf(label)+" "+ "1:"+action_name+" "+"2:"+pressure1+" "+"3:"+pressure2+" "+"4:"+pressure3+" "+"5:"+speeed+" "+"6:"+startX+" "+"7:"+startY+" "+"8:"+endX+" "+"9:"+endY+" "+"10:"+extentX+" "+"11:"+extentY+"\n";
    }
    public String getStr(){
//        return action_name+" \n"+pressure1+" \n"+pressure2+" \n"+pressure3+" \n"+speeed+" \n"+"("+startX+","+startY+")\n"+"("+endX+","+endY+")\n["+extentX+","+extentY+"]\n";
        //第一个参数是label
        return "1"+" "+ "1:"+action_name+" "+"2:"+pressure1+" "+"3:"+pressure2+" "+"4:"+pressure3+" "+"5:"+speeed+" "+"6:"+startX+" "+"7:"+startY+" "+"8:"+endX+" "+"9:"+endY+" "+"10:"+extentX+" "+"11:"+extentY+"\n";
    }
}
