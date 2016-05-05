package com.zyyoona7.androidserialport;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.zyyoona7.androidserialport.bean.MsgBean;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * Created by User on 2016/5/4.
 */
public class MsgAdapter extends BGARecyclerViewAdapter<MsgBean> {

    public MsgAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_form);
    }

    @Override
    protected void fillData(BGAViewHolderHelper bgaViewHolderHelper, int i, MsgBean msgBean) {
        RelativeLayout layoutFrom=bgaViewHolderHelper.getView(R.id.layout_from);
        RelativeLayout layoutTo=bgaViewHolderHelper.getView(R.id.layout_to);
        if(msgBean.isFrom()){
            layoutFrom.setVisibility(View.VISIBLE);
            layoutTo.setVisibility(View.GONE);
            bgaViewHolderHelper.setText(R.id.text_msg,msgBean.getMsg());
            bgaViewHolderHelper.setImageResource(R.id.image_head,msgBean.getImgId());
        }else {
            layoutFrom.setVisibility(View.GONE);
            layoutTo.setVisibility(View.VISIBLE);
            bgaViewHolderHelper.setText(R.id.text_msg_to,msgBean.getMsg());
            bgaViewHolderHelper.setImageResource(R.id.image_head_to,msgBean.getImgId());
        }
    }
}
