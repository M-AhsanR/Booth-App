package com.schopfen.Booth.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.schopfen.Booth.DataClasses.MyOrderModel;
import com.schopfen.Booth.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    private List<MyOrderModel> myOrderModels;
    private Response.Listener<String> context;
    private CRLCallbacks callbacks;
    ArrayList<MyOrderModel> arraylistadapter;

    public MyOrderAdapter(Response.Listener<String> context, ArrayList<MyOrderModel> arraylistadapter, CRLCallbacks callbacks) {
        this.myOrderModels = arraylistadapter;
        this.context = context;
        this.callbacks = callbacks;
    }

    @Override
    public MyOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_adapter, parent, false);
        return new MyOrderAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOrderAdapter.MyViewHolder holder, int position) {
        MyOrderModel lisadapter = myOrderModels.get(position);
     //   holder.name.setText(lisadapter.getName());
    }

    @Override
    public int getItemCount() {
        return myOrderModels.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.ll_moa);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.onItemClick(getLayoutPosition());
                }
            });
        }
    }

    public interface CRLCallbacks {
        void onItemClick(int position);
    }

}
