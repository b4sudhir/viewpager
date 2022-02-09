package com.iot4all.in.veiwpager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttToken;

public class MainActivity extends AppCompatActivity implements MqttCallback,RecyclerViewClickInterface {
    LinearLayout dotslayout;
    SliderAdapter adapter;
    ViewPager2 viewPager2;
    MqttAndroidClient client;

    int list[];
    int listofcolor[];

    TextView[] dots;
    String dname[];
    String name1[];
    String name2[];
    String name3[];
    String name4[];
    String ipadd[];
    String result[];

    boolean enabled[];//enabled or disabled
    boolean swt1val[];
    boolean swt2val[];
    boolean swt3val[];
    boolean swt4val[];
    boolean swt5val[];
    int fanspeed[];


    String sub_topic[][];
    String pub_topic[][];


    final String clientId = MqttClient.generateClientId();
    final String serverUri = "tcp://iot4all.in:1883";
    private String history ="";

    TextView d_status;
    ImageView img_con;
    int pos;
    String device_id[];
    int nod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("DeviceID", "18CA31C4AC24~Device2~Device3");
        editor.putInt("nod",3);
        editor.apply();  /* Edit the value here*/
        int nod = sp.getInt("nod",0);
        String Device_id= sp.getString("DeviceID", "");
        if(nod>0)
        {
            device_id=Device_id.split("~");
        }
        dotslayout = findViewById(R.id.dots_container);
        viewPager2 = findViewById(R.id.view_pager2);
        d_status=findViewById(R.id.dstat);
        img_con=findViewById(R.id.img_con);

        connectMqtt(nod);

        d_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d_status.setText(history);
            }
        });

        d_status.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                swt1val[0]= !swt1val[0];
                adapter.add(1,swt1val);
                return false;
            }
        });
        img_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(client.isConnected()) {
                    try {
                        disConnectMqtt();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    connectMqtt(nod);
                }
            }
        });

        listofcolor=new int[10];
        listofcolor[0]=getResources().getColor(R.color.LightBlue);
        listofcolor[1]=getResources().getColor(R.color.teal_200);
        listofcolor[2]=getResources().getColor(R.color.LightCoral);
        listofcolor[3]=getResources().getColor(R.color.Lavender);
        listofcolor[4]=getResources().getColor(R.color.LightSalmon);
        listofcolor[5]=getResources().getColor(R.color.MediumAquamarine);
        listofcolor[6]=getResources().getColor(R.color.MediumSpringGreen);
        listofcolor[7]=getResources().getColor(R.color.LightPink);
        listofcolor[8]=getResources().getColor(R.color.LightSkyBlue);
        listofcolor[9]=getResources().getColor(R.color.LightSeaGreen);


        list = new int[nod];
        for(int i=0; i<nod; i++)
        {
            list[i]=listofcolor[i];
        }
        enabled=new boolean[nod];
        swt1val= new boolean[nod];
        swt2val= new boolean[nod];
        swt3val= new boolean[nod];
        swt4val= new boolean[nod];
        swt5val= new boolean[nod];
        fanspeed=new int[nod];

        dname=new String[nod];
        name1=new String[nod];
        name2=new String[nod];
        name3=new String[nod];
        name4=new String[nod];
        ipadd=new String[nod];
        result=new String[nod];

        sub_topic = new String[8][nod];
        pub_topic = new String[8][nod];
        int sub_temp=0;
        for(int i=0; i <nod; i++)
        {
            enabled[i]=false;
            swt1val[i]=false;
            swt2val[i]=false;
            swt3val[i]=false;
            swt4val[i]=false;
            swt5val[i]=false;

            fanspeed[i]=50;

            dname[i]="Device Name";
            name1[i]="Switch 1";
            name2[i]="Switch 2";
            name3[i]="Switch 3";
            name4[i]="Switch 4";
            ipadd[i]="0.0.0.0";
            result[i]="Status :";

            //Subscribe topic
            sub_topic[0][i]="/stat/"+device_id[i]+"/Power1";
            sub_topic[1][i]="/stat/"+device_id[i]+"/Power2";
            sub_topic[2][i]="/stat/"+device_id[i]+"/Power3";
            sub_topic[3][i]="/stat/"+device_id[i]+"/Power4";
            sub_topic[4][i]="/stat/"+device_id[i]+"/Fan";
            sub_topic[5][i]="/stat/"+device_id[i]+"/Speed";
            sub_topic[6][i]="/stat/"+device_id[i]+"/Alldata";
            sub_topic[7][i]="/stat/"+device_id[i]+"/Result";
            //publish topic
            pub_topic[0][i]="/cmd/"+device_id[i]+"/Power1";
            pub_topic[1][i]="/cmd/"+device_id[i]+"/Power2";
            pub_topic[2][i]="/cmd/"+device_id[i]+"/Power3";
            pub_topic[3][i]="/cmd/"+device_id[i]+"/Power4";
            pub_topic[4][i]="/cmd/"+device_id[i]+"/Fan";
            pub_topic[5][i]="/cmd/"+device_id[i]+"/Speed";
            pub_topic[6][i]="/cmd/"+device_id[i]+"/Alldata";
            pub_topic[7][i]="/cmd/"+device_id[i]+"/Result";
        }

        adapter = new SliderAdapter(list,enabled,swt1val,swt2val,swt3val,swt4val,swt5val, fanspeed,
                                    dname,name1,name2,name3,name4,ipadd,result,this);
        viewPager2.setAdapter(adapter);
        dots = new TextView[nod];
        dotsindicator();
        viewPager2.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedIndicator(position);
                super.onPageSelected(position);
            }
        });

    }
///====================================CONNECT MQTT FUNCTION==============================================//

    private void connectMqtt(int nod) {
        client = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("mqttuser");
        options.setPassword("password".toCharArray());
        options.setAutomaticReconnect(true);
        try {
            client.connect(options);
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    if (reconnect) {
                        addToHistory("Reconnected to : " + serverURI);
                        // Because Clean Session is true, we need to re-subscribe

                    } else {
                        addToHistory("Connected to: " + serverURI);
                    }
                    subscribeToTopic(nod,device_id);
                    init_msg(nod);
                    img_con.setImageResource(R.drawable.online);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    addToHistory("connect loss");
                    for(int i=0; i<nod;i++)
                    {
                        enabled[i]=false;
                    }
                    adapter.add(5,enabled);
                    img_con.setImageResource(R.drawable.offline);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    addToHistory("Incoming message: " +topic+" : " +new String(message.getPayload()));
                    mqtt_message(nod, topic,new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    addToHistory("delivery");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    ///====================================DISCONNECT MQTT FUNCTION==============================================//
    private void disConnectMqtt() throws MqttException {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // we are now successfully disconnected
                img_con.setImageResource(R.drawable.offline); }

            @Override
            public void onFailure(IMqttToken asyncActionToken,
                                  Throwable exception) {
                // something went wrong, but probably we are disconnected anyway
                d_status.setText("Error while disconnecting"); }
        });

    }



    ///====================================DOTS COLORS FUNCTION==============================================//
    private void selectedIndicator(int position) {

        for(int i=0; i<dots.length;i++)
        {
            int x= i+1;
            if(i==position)
            {
                dots[i].setTextColor(list[position]);
            }
            else
            {
                dots[i].setTextColor(getResources().getColor(R.color.White));
            }
        }
        for(int i=0; i<dots.length;i++)
        {
            int x= i+1;
            if(i==position)
            {
                dots[i].setTextColor(list[position]);
            }
            else
            {
                dots[i].setTextColor(getResources().getColor(R.color.White));
            }
        }

    }
    ///====================================DOTS CREATE FUNCTION==============================================//
    private void dotsindicator() {
        for(int i=0; i<dots.length;i++)
        {
            dots[i]= new TextView(this);
            dots[i].setText(Html.fromHtml(" &nbsp; &#9632; &nbsp;"));
            dots[i].setTextSize(20);
            dotslayout.addView(dots[i]);
            int finalI = i;
            dots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            viewPager2.setCurrentItem(finalI);
                        }
                    });

                }
            });
        }
    }

    public void changetext(String x)
        {
            Toast.makeText(getApplicationContext(), "x", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    ///====================================SWITCH PUBLISH DATA FUNCTION==============================================//
    @Override
    public void onswitchclick(int position, int swt, boolean swt_val) {
        //Toast.makeText(this, "Postion is "+ toString().valueOf(position)+"Switch:"+toString().valueOf(swt)+"ON:"+toString().valueOf(swt_val) , Toast.LENGTH_SHORT).show();
        String pub_top = pub_topic[swt][position];
        String pub_msg="";
        if(swt_val)
             pub_msg = "1";
        else
             pub_msg = "0";
        addToHistory("Publish date :" +pub_top+" : "+pub_msg);
        publishMessage(pub_top,pub_msg);
    }
    ///====================================FAN SPEED PUBLISH DATA FUNCTION==============================================//
    @Override
    public void onfanclick(int position, int swt, int fan_val) {
        //Toast.makeText(this, "Postion is "+ toString().valueOf(position)+"Switch:"+toString().valueOf(swt)+"ON:"+toString().valueOf(swt_val) , Toast.LENGTH_SHORT).show();
        String pub_top = pub_topic[swt][position];
        String pub_msg = toString().valueOf(fan_val);
        addToHistory("Publish date :" +pub_top+" : "+pub_msg);
        publishMessage(pub_top,pub_msg);
    }
    ///====================================SUBSCRIBE TO TOPIC DATA FUNCTION==============================================//
    public void subscribeToTopic(int nod, String[] device_id) {
        String sub_result;
        try {
            for(int i =0; i<nod;i++)
            {
                int finalI = i;
                client.subscribe(sub_topic[0][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }
                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[1][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[2][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[3][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[4][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[5][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[6][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
                client.subscribe(sub_topic[7][i], 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        addToHistory("Successfully Subscribed : " + sub_topic[0][finalI]);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        addToHistory("Failure Sub topic : " + sub_topic[0][finalI]);
                    }
                });
            }
        } catch (MqttException e) {
            addToHistory("Error while Subscribe : " + e.toString());
            e.printStackTrace();
        }
    }
    ///====================================SUBSCRIBED MESSAGE SEND TO VIEW PAGE FUNCTION==============================================//
    public void mqtt_message(int nod, String topic, String message)
    {
        Toast.makeText(this, "Topic :"+topic+"Message : "+message , Toast.LENGTH_SHORT).show();
        for(int i=0; i<nod; i++)
        {
            if(topic.equals(sub_topic[0][i]))
            {
                if(Integer.parseInt(message)==0) {
                    swt1val[i] = false;
                }else{
                    swt1val[i] = true;
                }
                adapter.add(0,swt1val);
            }
            if(topic.equals(sub_topic[1][i])) {
                if (Integer.parseInt(message) == 0) {
                    swt2val[i] = false;
                } else {
                    swt2val[i] = true;
                }
                adapter.add(1, swt2val);
            }
            if(topic.equals(sub_topic[2][i])) {
                if (Integer.parseInt(message) == 0) {
                    swt3val[i] = false;
                } else {
                    swt3val[i] = true;
                }
                adapter.add(2, swt3val);
            }
            if(topic.equals(sub_topic[3][i])) {
                if (Integer.parseInt(message) == 0) {
                    swt4val[i] = false;
                } else {
                    swt4val[i] = true;
                }
                adapter.add(3, swt4val);
            }
            if(topic.equals(sub_topic[4][i])) {
                if (Integer.parseInt(message) == 0) {
                    swt5val[i] = false;
                } else {
                    swt5val[i] = true;
                }
                adapter.add(4, swt5val);
            }
            if(topic.equals(sub_topic[5][i])) {
                fanspeed[i]=Integer.parseInt(message);
                adapter.addint(fanspeed);
            }
            if(topic.equals(sub_topic[6][i])) {

                String[] data_arr = message.split(",");
                if (Integer.parseInt(data_arr[0]) == 0) {
                    swt1val[i] = false;
                }else{
                    swt1val[i] = true;
                }
                if (Integer.parseInt(data_arr[1]) == 0) {
                    swt2val[i] = false;
                } else {
                    swt2val[i] = true;
                }
                if (Integer.parseInt(data_arr[2]) == 0) {
                    swt3val[i] = false;
                } else {
                    swt3val[i] = true;
                }
                if (Integer.parseInt(data_arr[3]) == 0) {
                    swt4val[i] = false;
                } else {
                    swt4val[i] = true;
                }
                if (Integer.parseInt(data_arr[4]) == 0) {
                    swt5val[i] = false;
                } else {
                    swt5val[i] = true;
                }
                fanspeed[i] = Integer.parseInt(data_arr[5]);
                name1[i]=data_arr[6];
                name2[i]=data_arr[7];
                name3[i]=data_arr[8];
                name4[i]=data_arr[9];
                dname[i]=data_arr[10];
                ipadd[i]=data_arr[11];
                enabled[i]=true;
                adapter.init_page(enabled,swt1val,swt2val,swt3val,swt4val,swt5val,fanspeed,name1,name2,name3,name4,dname,ipadd);
            }
            if(topic.equals(sub_topic[7][i])) {
                result[i]=message;
                adapter.addresult(result);
            }

        }
    }

    ///====================================PUBLISH DATA FUNCTION==============================================//
    public void publishMessage(String topic, String msg){
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            client.publish(topic, message);
            Toast.makeText(this, "Message Published", Toast.LENGTH_SHORT).show();
            if(!client.isConnected()){
                addToHistory(client.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    ///====================================ADD TO HISTORY FOR FIND ERROR FUNCTION==============================================//
    public void addToHistory(String mainText){
        history += mainText + "\n";

    }
    ///====================================FIRST INITIALIZE MQTT MESSAGE FOR ALL DATA FROM DEVICE FUNCTION==============================================//
    private void init_msg(int nod)
    {
        for(int i=0;i<nod;i++)
        {
            String pub_top = pub_topic[6][i];
            String pub_msg = "1";
            addToHistory("Publish date :" +pub_top+" : "+pub_msg);
            publishMessage(pub_top,pub_msg);
        }
    }
}