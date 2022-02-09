package com.iot4all.in.veiwpager;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.MyviewHolder> {
    int list[];
    String dname[];
    String name1[];
    String name2[];
    String name3[];
    String name4[];
    String result[];
    String ipadd[];

    boolean swt1val[];
    boolean swt2val[];
    boolean swt3val[];
    boolean swt4val[];
    boolean swt5val[];
    boolean enabled[];

    int fanspeed[];
    private Context context;
    private RecyclerViewClickInterface recyclerViewClickInterface;


    public SliderAdapter(int[] list, boolean[] enabled, boolean[] swt1val, boolean[] swt2val, boolean[] swt3val, boolean[] swt4val, boolean[] swt5val, int[] fanspeed, String[] dname, String[] name1, String[] name2, String[] name3, String[] name4, String[] ipadd, String[] result, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.list = list;
        this.enabled=enabled;
        this.dname = dname;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.name4 = name4;
        this.result = result;
        this.swt1val = swt1val;
        this.swt2val = swt2val;
        this.swt3val = swt3val;
        this.swt4val = swt4val;
        this.swt5val = swt5val;
        this.fanspeed = fanspeed;
        this.ipadd = ipadd;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    public void add(int id, boolean[] swtval){
        if(id==0) {
            this.swt1val = swtval;
        }else if(id==1) {
            this.swt2val = swtval;
        }else if(id==2) {
            this.swt3val = swtval;
        }else if(id==3) {
            this.swt4val = swtval;
        }else if(id==4) {
            this.swt5val = swtval;
        }else if(id==5) {
            this.enabled = swtval;
        }
        this.notifyDataSetChanged();
    }

    public void addint(int[] fanval){
        this.fanspeed=fanval;
        this.notifyDataSetChanged();
    }
    public void addresult(String[] val){
        this.result=val;
        this.notifyDataSetChanged();
    }
    public void init_page(boolean[] enabled, boolean[] swt1,boolean[] swt2,boolean[] swt3,boolean[] swt4,boolean[] swt5, int[] fan_val,
                          String[] name1,String[] name2,String[] name3,String[] name4,String[] dname,String[] ipadd){
        this.enabled=enabled;
        this.swt1val = swt1;
        this.swt2val = swt2;
        this.swt3val = swt3;
        this.swt4val = swt4;
        this.swt5val = swt5;
        this.fanspeed = fan_val;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.name4 = name4;
        this.dname = dname;
        this.ipadd = ipadd;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.view.setCardBackgroundColor(list[position]);
        if(enabled[position])
        {
            holder.swt1.setEnabled(true);
            holder.swt2.setEnabled(true);
            holder.swt3.setEnabled(true);
            holder.swt4.setEnabled(true);
            holder.swt5.setEnabled(true);
            holder.fanslider.setEnabled(true);
            holder.statimg.setImageResource(R.drawable.green_dot);
        }else {
            holder.swt1.setEnabled(false);
            holder.swt2.setEnabled(false);
            holder.swt3.setEnabled(false);
            holder.swt4.setEnabled(false);
            holder.swt5.setEnabled(false);
            holder.fanslider.setEnabled(false);
            holder.statimg.setImageResource(R.drawable.red_dot);}


        holder.text_sw1.setText(name1[position].toString());
        holder.text_sw2.setText(name2[position].toString());
        holder.text_sw3.setText(name3[position].toString());
        holder.text_sw4.setText(name4[position].toString());

        holder.text_dev.setText(dname[position].toString());
        holder.result.setText(result[position].toString());
        holder.ipadd.setText(ipadd[position].toString());

        holder.swt1.setChecked(swt1val[position]);
        holder.swt2.setChecked(swt2val[position]);
        holder.swt3.setChecked(swt3val[position]);
        holder.swt4.setChecked(swt4val[position]);
        holder.swt5.setChecked(swt5val[position]);

        holder.fanslider.setValue(Float.parseFloat(String.valueOf(fanspeed[position])));
        //===============================Fan speed Value================//
        int val = (int) holder.fanslider.getValue();
        if (val > 89) { holder.fanspd.setText("F"); }
        else { if (val < 20) { val = 1; } else { val = val / 10; }
            holder.fanspd.setText(String.valueOf(val));}

        ///=============================Click Listener==============================================================//
        holder.swt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.swt1.isChecked())
                    recyclerViewClickInterface.onswitchclick(position,0,true);
                else
                    recyclerViewClickInterface.onswitchclick(position,0,false);
            }
        });
        holder.swt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.swt2.isChecked())
                    recyclerViewClickInterface.onswitchclick(position,1,true);
                else
                    recyclerViewClickInterface.onswitchclick(position,1,false);
            }
        });
        holder.swt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.swt3.isChecked())
                    recyclerViewClickInterface.onswitchclick(position,2,true);
                else
                    recyclerViewClickInterface.onswitchclick(position,2,false);
            }
        });
        holder.swt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.swt4.isChecked())
                    recyclerViewClickInterface.onswitchclick(position,3,true);
                else
                    recyclerViewClickInterface.onswitchclick(position,3,false);
            }
        });
        holder.swt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.swt5.isChecked())
                    recyclerViewClickInterface.onswitchclick(position,4,true);
                else
                    recyclerViewClickInterface.onswitchclick(position,4,false);
            }
        });
        holder.fanslider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int val = (int) holder.fanslider.getValue();
                recyclerViewClickInterface.onfanclick(position,5,val);
                if (val > 89) { holder.fanspd.setText("F"); }
                else { if (val < 20) { val = 1; } else { val = val / 10; }
                    holder.fanspd.setText(String.valueOf(val)); }
            }
        });
        holder.ipadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context = view.getContext();
                Intent intent = new Intent(context, webpage.class);
                intent.putExtra("url",ipadd[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        CardView view;
        private TextView text_sw1,text_sw2,text_sw3,text_sw4,text_dev,fanspd,result,ipadd;
        private androidx.appcompat.widget.SwitchCompat swt1,swt2,swt3,swt4,swt5;
        private com.google.android.material.slider.Slider fanslider;
        private ImageView statimg;
        private WebView webView;

        EditText editbox;
        Button btn;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);
            text_sw1 = itemView.findViewById(R.id.textView1);
            text_sw2 = itemView.findViewById(R.id.textView2);
            text_sw3 = itemView.findViewById(R.id.textView3);
            text_sw4 = itemView.findViewById(R.id.textView4);

            text_dev = itemView.findViewById(R.id.devicename);
            statimg =itemView.findViewById(R.id.img_dev_stat);

            fanspd = itemView.findViewById(R.id.fanspeed_val);
            fanslider = itemView.findViewById(R.id.fanslider);

            result = itemView.findViewById(R.id.result_stat);
            ipadd = itemView.findViewById(R.id.ipadd);

            swt1 = itemView.findViewById(R.id.sw1);
            swt2 = itemView.findViewById(R.id.sw2);
            swt3 = itemView.findViewById(R.id.sw3);
            swt4 = itemView.findViewById(R.id.sw4);
            swt5 = itemView.findViewById(R.id.sw5);
        }
    }
}