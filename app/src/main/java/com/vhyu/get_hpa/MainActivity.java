package com.vhyu.get_hpa;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.LineNumberInputStream;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberInputStream;
import java.io.OutputStreamWriter;


public class MainActivity extends Activity {
    private TextView textView1 = null;
    private int  flag_on= 1;//开始的时候要直接写入训练集      flag_on = 0;测试好写入训练集      flag_on = 2;//直接写入测试集
    public static final int TRAINNUMBER = 100;
    public int train_num = 0;//训练集文件中的记录数目

    public TextView result_view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.textView1);
        result_view = (TextView) findViewById(R.id.result);
    }

    static int record_num = 1;
    static Struct_Record[] arrayEvent_Train=new Struct_Record[10];//10个Struct_Record元素的数组，用于训练
    static Struct_Record[] arrayEvent_Test = new  Struct_Record[10];//10个元素用于测试
    //将文件写入TXT文件
    // 要保存的文件名
    int writeStyle = MODE_APPEND;//写入文件的方式，追加

    //flag的意思是直接写入数据还是写入训练后的数据
    //flag = 1 表示直接做训练集，调用无参的getStr()
    //flag = 0 表示预测好的数据做训练集，调用有参的getStr(int )
    public boolean saveTofile(String fileName,int file_Style,Struct_Record[] records,int flag) {
        try {
      /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的，
       * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的
       *  public abstract FileOutputStream openFileOutput(String name, int mode)
       *  throws FileNotFoundException;
       * openFileOutput(String name, int mode);
       * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名
       *     该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt
       * 第二个参数，代表文件的操作模式
       *     MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖
       *     MODE_APPEND 私有  重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件
       *     MODE_WORLD_READABLE 公用 可读
       *     MODE_WORLD_WRITEABLE 公用 可读写
       * */

//            测试过程中删除文件
//                File file = new File(MainActivity.this.getFilesDir().getAbsolutePath()+"/"+"test.txt");
////                System.out.println(MainActivity.this.getFilesDir().getAbsoluteFile()+fileName);
//                if (file == null || !file.exists() || file.isDirectory())
//                {
//                    System.out.println("file is not exist!!");
//                }
//                System.out.println("file delete result:"+file.delete()+"##############");

            //打开文件读写流
            FileOutputStream outputStream = openFileOutput(fileName, file_Style);

            //循环从数组中取出每一条数据，并写入文件
            if(flag == 1)//直接做训练集
            {
                for(int i =0;i<10;i++)
                {
                    //Maybe exist problem，训练集数据，自己直接添加标签
                    outputStream.write(records[i].getStr().getBytes());
                    outputStream.flush();
                }
            }
            else if(flag == 0)//预测好的做训练集
            {
                for(int i =0;i<10;i++)
                {
                    //Maybe exist problem，训练集数据，需要重新添加标签
                    outputStream.write(records[i].getStr(1).getBytes());
                    outputStream.flush();
                }
            }
            else // flag == 2直接写入测试集
            {
                for(int i =0;i<10;i++)
                {
                    //Maybe exist problem，不用加标签
                    outputStream.write(records[i].getStr().getBytes());
                    outputStream.flush();
                }
            }

            outputStream.close();

//            //txt文件第一行是当前的行数，每次追加一条数据
//            FileInputStream in = openFileInput(fileName);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String readFromfile(String fileName) {

        /**
         * @author chenzheng_java
         * 读取刚才用户保存的内容
         */
        try {

            FileInputStream inputStream = this.openFileInput(fileName);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String content = new String(arrayOutputStream.toByteArray());
            return content;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    //数组的格式
    //[标签,事件名称,压力1，压力2，压力3，速度，起点X,起点Y,止点X,止点Y,幅度X,幅度Y] 12个参数
    //将产生的数据先写入到数组，数组的容量是10，然后将10条数据一次写入文件（若是训练阶段，那么需要自己添加标记）
        //预测的时候中10条数据进行预测，当其中的识别出是合法用户占的比重大于50%的时候,认为是合法用户，否则属于非法用户。
        //将预测之后的数据结果写入到“结果数组”中去，将结果数组写入训练集中。
    public boolean onTouchEvent(MotionEvent event) {

        ////属性获得开始
        int act = 0;//act 的取值有：0点击事件；1移动事件
        float start_x = -1;
        float start_y = -1;
        float end_x = -1;
        float end_y = -1;
        float pre = event.getPressure();
        float size = event.getSize();//事件的面积，0-1归一化了-----压强不一定准确
        float hPa = (float) (pre * 1.0 / size);

        float down_time = 0;
        float up_time = 0;
        boolean flag_move = false;//false表示的是没有移动，也就是点击事件；true表示的滑动事件
        Struct_Record record = new Struct_Record();

        //考虑单点触摸
        //用户按下，移动，抬起
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                record.pressure1 = event.getPressure();
                down_time = event.getDownTime();
                start_x = event.getRawX();
                start_y = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                flag_move = true;
                record.action_name = 1;
                record.pressure2 = event.getPressure();
                break;
            case MotionEvent.ACTION_UP:
                record.pressure3 = event.getPressure();
                up_time = event.getEventTime();
                end_x = event.getRawX();
                end_y = event.getRawY();
                break;
            default:
                break;
        }
        //constuct the record
//        record.speeed = ((float) Math.sqrt(Math.pow((end_y - start_y), 2) + Math.pow((end_x - start_x), 2)))*100/(up_time-down_time)*100;
        record.speeed = ((float) Math.sqrt(Math.pow((end_y - start_y), 2) + Math.pow((end_x - start_x), 2)));
        record.startX = start_x;
        record.startY = start_y;
        record.endX = end_x;
        record.endY = end_y;
//        record.record_num ;

        if (flag_move) {
            act = 1;//点击事件，只可获得压力（由于点击一般属于点击控件，所以点击的坐标不作为行为识别的标准）
        }
        record.action_name = act;
        textView1.setText(record.getStr());//训练集,没有参数，的默认的label是1
        //属性获得结束

        String writeFileName = null;

        //开始测试集的准备
        if(record_num >=  TRAINNUMBER){
            //测试集阶段
            writeFileName = "test.txt";
            writeStyle = MODE_PRIVATE;
            //收集数据
            //先写入数组
            if (record_num % 11 != 0) {
                arrayEvent_Test[record_num % 11 - 1] = record;
            }
            //数组满了，则写入文件
            else {
                //准备收集测试集
                    saveTofile(writeFileName, writeStyle, arrayEvent_Test,2);//flag_on == 2
                //开始预测

                //参数为0 表示调用预测
                MLoperation(0);

                //显示结果
//                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@222\n"+readFromfile("result.txt")+"%%%%%%%%%%%%%%%%%%%%%%%%5");
                result_view.setText(readFromfile("result.txt"));

//                System.out.println("test is ok!!!!!!!!!!!!!1");
                //预测完成之后，将预测结果的文件读出来，将得到的label加入到测试数据中，然后写入train文件中

                /***此文件读出来的内容是贼安慰标签的，但是现在没有读的很好，暂时手动添加标签****/
//                String fileName = "result.txt";//输出结果的filename
//                readFromfile(fileName);

                //写入训练集
                writeFileName = "train.txt";
                writeStyle = MODE_APPEND;
                saveTofile(writeFileName, writeStyle, arrayEvent_Test,0);//flag_on == 0，预测好洗后写入训练集

                //写入train文件之后置空（其实可以不置空，以后直接写也会覆盖）
//                arrayEvent_Test = {};
            }
        }


        //开始训练集的准备
        else
        {
            //训练集准备阶段（文件类型，读写方式）
            writeFileName = "train.txt";
            writeStyle = MODE_APPEND;
            flag_on = 1;//flag = 1的时候直接写入训练集
            //先写入数组
            if (record_num % 11 != 0) {
                arrayEvent_Train[record_num % 11 - 1] = record;
            }
            //数组满了，则写入文件
            else {
                saveTofile(writeFileName, writeStyle, arrayEvent_Train,flag_on);
                //写入文件之后置空（其实可以不置空，以后直接写也会覆盖）
//                arrayEvent_Train = null;
            }

            //可能有问题
            //应该 用 添加完预测好的数据 之后的数据集 在进行训练的过程
            if(record_num==TRAINNUMBER-1 ){
                //开始训练
                //参数为1表示训练模型
                MLoperation(1);

                //训练完之后开始预测
            }
        }



              //System.out.println(readFromfile(file_name));
        ++record_num;
        return super.onTouchEvent(event);
    }

    //train
    public void Train() throws IOException {
        try {
            String path = MainActivity.this.getFilesDir().getAbsolutePath();
            String[] arg = {path + "/train.txt", //存放SVM训练模型用的数据的路径
                    path + "/model.txt"};  //存放SVM通过训练数据训/ //练出来的模型的路径
            System.out.println("........SVM运行开始训练模型..........");
            svm_train train = new svm_train();
            train.main(arg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //predict
    public void Predict() throws IOException{
        try {
            String path = MainActivity.this.getFilesDir().getAbsolutePath();
            String[] parg = {path + "/test.txt",   //这个是存放测试数据
                    path + "/model.txt",  //调用的是训练以后的模型
                    path + "/result.txt"};  //生成的结果的文件的路径
            System.out.println("........SVM运行开始预测数据..........");
            svm_predict predict = new svm_predict();
            predict.main(parg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //run_int = 1 表示调用训练函数
    //run_int = 0 表示调用预测函数
    private void MLoperation(final int run_int){
        // 直接new 一个线程类，传入参数实现Runnable接口的对象（new Runnable），相当于方法二
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 写子线程中的操作
                try {
                    if (run_int == 1)
                    {
                        Train();
                    }else {
                        Predict();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}