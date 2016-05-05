package com.zyyoona7.androidserialport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.zyyoona7.androidserialport.ComAssistant.MyFunc;
import com.zyyoona7.androidserialport.ComAssistant.SerialHelper;
import com.zyyoona7.androidserialport.bean.ComBean;
import com.zyyoona7.androidserialport.bean.MsgBean;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private MaterialSpinner mSpinnerPort;
    private MaterialSpinner mSpinnerRate;
    private RecyclerView mRecyclerMsg;
    private Button mBtnOpen;
    private EditText mEditMsg;
    private Button mBtnSend;
    SerialPortFinder mSerialPortFinder;//串口设备搜索
    //串口设备数组
    String[] entryValues;
    //波特率数组
    String[] rateValues;
    List<MsgBean> listMsg = new ArrayList<>();

    SerialControl mSerialContol;
    MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mSpinnerPort = (MaterialSpinner) findViewById(R.id.spinner_port);
        mSpinnerRate = (MaterialSpinner) findViewById(R.id.spinner_rate);
        mRecyclerMsg = (RecyclerView) findViewById(R.id.recycler_msg);
        mBtnOpen = (Button) findViewById(R.id.button_open_port);
        mBtnSend = (Button) findViewById(R.id.button_send_msg);
        mEditMsg = (EditText) findViewById(R.id.edit_msg);
        mBtnOpen.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);

        mSerialPortFinder = new SerialPortFinder();
        entryValues = mSerialPortFinder.getAllDevicesPath();
        mSpinnerPort.setItems(entryValues);
        rateValues = getResources().getStringArray(R.array.baudrates_value);
        mSpinnerRate.setItems(rateValues);
        mSerialContol = new SerialControl();

        adapter = new MsgAdapter(mRecyclerMsg);
        mRecyclerMsg.setHasFixedSize(true);
        mRecyclerMsg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerMsg.setItemAnimator(new DefaultItemAnimator());
        mRecyclerMsg.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_open_port:
                if ("打开串口监听".equals(mBtnOpen.getText())) {
                    String port = entryValues[mSpinnerPort.getSelectedIndex()];
                    String rate = rateValues[mSpinnerRate.getSelectedIndex()];
                    Log.e(TAG, "onClick: port=" + port + ",rate=" + rate);
                    mSerialContol.setBaudRate(rate);
                    mSerialContol.setPort(port);
                    openComPort(mSerialContol);
                } else {
                    closeComPort(mSerialContol);
                    mBtnOpen.setText("打开串口监听");
                }
                break;
            case R.id.button_send_msg:
                String sendText=mEditMsg.getText().toString();
                if(!TextUtils.isEmpty(sendText)){
                    sendPortData(mSerialContol,sendText);
                    Log.e(TAG, "onClick: "+sendText);
                }
                break;
        }
    }

    //----------------------------------------------------关闭串口
    private void closeComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    //----------------------------------------------------打开串口
    private void openComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
            mBtnOpen.setText("关闭串口监听");
        } catch (SecurityException e) {
            Log.e(TAG, "OpenComPort: 打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            Log.e(TAG, "OpenComPort: 打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            Log.e(TAG, "OpenComPort: 打开串口失败:参数错误!");
        }
    }

    private void sendPortData(SerialHelper ComPort, String sOut) {
        if (ComPort != null && ComPort.isOpen()) {
            ComPort.sendHex(sOut);

            MsgBean msgBean=new MsgBean();
            msgBean.setFrom(false);
            msgBean.setImgId(R.mipmap.ic_launcher);
            msgBean.setMsg(sOut);
            Log.e(TAG, "sendPortData: "+sOut );
            adapter.addLastItem(msgBean);
            mRecyclerMsg.scrollToPosition(adapter.getItemCount()-1);
        }

    }


    //----------------------------------------------------串口控制类
    private class SerialControl extends SerialHelper {

        //		public SerialControl(String sPort, String sBaudRate){
//			super(sPort, sBaudRate);
//		}
        public SerialControl() {
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData) {
            //数据接收量大或接收时弹出软键盘，界面会卡顿,可能和6410的显示性能有关
            //直接刷新显示，接收数据量大时，卡顿明显，但接收与显示同步。
            //用线程定时刷新显示可以获得较流畅的显示效果，但是接收数据速度快于显示速度时，显示会滞后。
            //最终效果差不多-_-，线程定时刷新稍好一些。
//            DispQueue.AddQueue(ComRecData);//线程定时刷新显示(推荐)
            /*
			runOnUiThread(new Runnable()//直接刷新显示
			{
				public void run()
				{
					DispRecData(ComRecData);
				}
			});*/
            MsgBean msgBean = new MsgBean();
            msgBean.setFrom(true);
            msgBean.setMsg(MyFunc.ByteArrToHex(ComRecData.bRec));
            msgBean.setImgId(R.mipmap.ic_launcher);
            adapter.addLastItem(msgBean);
            mRecyclerMsg.scrollToPosition(adapter.getItemCount()-1);
        }
    }
}
