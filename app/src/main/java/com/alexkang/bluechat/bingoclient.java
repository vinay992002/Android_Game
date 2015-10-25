package com.alexkang.bluechat;

/**
 * Created by vinay on 21/10/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class bingoclient extends Activity {

    public static final int PICK_IMAGE = 1;

    private ArrayList<Integer> acceptableDevices = new ArrayList<Integer>();

    private EditText mMessage;
    private ProgressDialog mProgressDialog;
    public static Button btn[] ;
    int setup =0;
    int startup =0;
    private String mUsername;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    static ArrayList<Integer> arr = new ArrayList<>();
    static int board[] = new int [25];
    static int flag[][] = new int[5][5];
    private bingoManager mChatManager;
    static int bcflg=0;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int deviceClass = device.getBluetoothClass().getDeviceClass();

                if (acceptableDevices.contains(deviceClass)) {
                    new ConnectThread(device).start();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bingo);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        bcflg=0;
        acceptableDevices.add(BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA);
        acceptableDevices.add(BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA);
        acceptableDevices.add(BluetoothClass.Device.PHONE_SMART);
        btn = new Button[25];
        btn[0] = (Button) findViewById(R.id.Bt00);
        if(btn[0]!=null)
            System.out.println("i was wrong");
        btn[1] = (Button) findViewById(R.id.Bt01);
        btn[2] = (Button) findViewById(R.id.Bt02);
        btn[3] = (Button) findViewById(R.id.Bt03);
        btn[4] = (Button) findViewById(R.id.Bt04);
        btn[5] = (Button) findViewById(R.id.Bt10);
        btn[6] = (Button) findViewById(R.id.Bt11);
        btn[7] = (Button) findViewById(R.id.Bt12);
        btn[8] = (Button) findViewById(R.id.Bt13);
        btn[9] = (Button) findViewById(R.id.Bt14);
        btn[10] = (Button) findViewById(R.id.Bt20);
        btn[11] = (Button) findViewById(R.id.Bt21);
        btn[12] = (Button) findViewById(R.id.Bt22);
        btn[13] = (Button) findViewById(R.id.Bt23);
        btn[14] = (Button) findViewById(R.id.Bt24);
        btn[15] = (Button) findViewById(R.id.Bt30);
        btn[16] = (Button) findViewById(R.id.Bt31);
        btn[17] = (Button) findViewById(R.id.Bt32);
        btn[18] = (Button) findViewById(R.id.Bt33);
        btn[19] = (Button) findViewById(R.id.Bt34);
        btn[20] = (Button) findViewById(R.id.Bt40);
        btn[21] = (Button) findViewById(R.id.Bt41);
        btn[22] = (Button) findViewById(R.id.Bt42);
        btn[23] = (Button) findViewById(R.id.Bt43);
        btn[24] = (Button) findViewById(R.id.Bt44);



        mChatManager = new bingoManager(this, false,btn);
        mProgressDialog = new ProgressDialog(this);
        for(int i=0;i<25;i++)
            btn[i].setId(i);
        MyClickHandler handler = new MyClickHandler();
        for(Button b:btn){
            b.setOnClickListener(handler);
        }



        mProgressDialog.setMessage("Looking for ChatRoom...");
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        mProgressDialog.show();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = sharedPref.getString("username", mBluetoothAdapter.getName());

        startDeviceSearch();
    }
    class MyClickHandler implements View.OnClickListener {
        public void onClick(View v) {

            if(startup==1)
                cross(v.getId());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.host, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        if(id ==R.id.hset)
        {
            if(setup==0)
            {
                setup();
                bcflg=0;
                byte[] byteArray;


                try {
                    String m = "reset";
                    System.out.print(m);
                    byte[] messageBytes = m.toString().getBytes();
                    byteArray = mChatManager.buildPacket(
                            ChatManager.MESSAGE_SEND,
                            mUsername,
                            messageBytes
                    );
                    mChatManager.writeMessage(byteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(this,"Set Bingo pad  by clicking buttons",Toast.LENGTH_SHORT).show();
            }
            else
            {

                Toast.makeText(this,"cannot Set plz click reset.",Toast.LENGTH_SHORT).show();
            }
        }
        if(id == R.id.hstart)
        {
                setup=0;
                Toast.makeText(this,"Click set!!",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
        public void setup(){
                    if(setup==0)
                    {
                        for(int i=10;i<35;i++)
                        {
                            arr.add(i);
                        }
                        Collections.shuffle(arr);
                        for(int i=0;i<25;i++)
                            board[i]=arr.get(i);
                        for(int i=0;i<25;i++)
                        {
                            flag[i/5][i%5]=0;
                            btn[i].setText(arr.get(i)+"");
                            btn[i].setEnabled(true);

                        }
                        setup =1;
                        startup =1;
                    }
        }
        public void cross(int loc){
                    if(startup==1 && flag[loc/5][loc%5]==0 && bcflg==0)
                    {
                        byte[] byteArray;
                        bcflg=1;

                        try {
                            String m = arr.get(loc)+"";
                            System.out.println(m);
                            byte[] messageBytes = m.toString().getBytes();
                            byteArray = mChatManager.buildPacket(
                                    ChatManager.MESSAGE_SEND,
                                    mUsername,
                                    messageBytes
                            );
                        } catch (Exception e) {
                            return;
                        }

                        mChatManager.writeMessage(byteArray);
                       // mMessage.setText("");
                    }
            else
                    {
                        Toast.makeText(this,"Wait for ur turn!!",Toast.LENGTH_SHORT).show();
                    }
        }
    private void startDeviceSearch() {
        mBluetoothAdapter.enable();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        new WaitForBluetoothThread().start();
    }

    private void sendMessage() {
        byte[] byteArray;

        if (mMessage.getText().toString().length() == 0) {
            return;
        }

        try {
            byte[] messageBytes = mMessage.getText().toString().getBytes();
            byteArray = mChatManager.buildPacket(
                    ChatManager.MESSAGE_SEND,
                    mUsername,
                    messageBytes
            );
        } catch (Exception e) {
            return;
        }

        mChatManager.writeMessage(byteArray);
        //mMessage.setText("");
    }

    private void uploadAttachment() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri image = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);

                new SendImageThread(picturePath).start();
                cursor.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket");
                System.err.println(e.toString());
            }
        }

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mBluetoothAdapter.cancelDiscovery();
    }

    private void manageSocket(BluetoothSocket socket) {
        mSocket = socket;
        mChatManager.startConnection(socket, mProgressDialog);
    }

    private class WaitForBluetoothThread extends Thread {

        public void run() {
            while (true) {
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.startDiscovery();
                    break;
                }
            }
        }

    }

    private class SendImageThread extends Thread {

        private Bitmap bitmap;

        public SendImageThread(String picturePath) {
            this.bitmap = BitmapFactory.decodeFile(picturePath);
        }

        public void run() {
            if (bitmap == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "Image is incompatible or not locally stored", Toast.LENGTH_SHORT).show();
                    }
                });

                return;
            }

            if (bitmap.getWidth() > 1024 || bitmap.getHeight() > 1024) {
                float scalingFactor;

                if (bitmap.getWidth() >= bitmap.getHeight()) {
                    scalingFactor = 1024f / bitmap.getWidth();
                } else {
                    Matrix fixRotation = new Matrix();
                    fixRotation.postRotate(90);
                    scalingFactor = 1024f / bitmap.getHeight();
                }

                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (int) (bitmap.getWidth() * scalingFactor),
                        (int) (bitmap.getHeight() * scalingFactor),
                        false
                );
            }

            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, output);
                byte[] imageBytes = output.toByteArray();
                byte[] packet = mChatManager.buildPacket(
                        ChatManager.MESSAGE_SEND_IMAGE,
                        mUsername,
                        imageBytes
                );
                mChatManager.writeMessage(packet);
            } catch (Exception e) {
                System.err.println("Failed to send image");
                System.err.println(e.toString());
            }
        }

    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(
                        java.util.UUID.fromString(MainActivity.UUID));
            } catch (Exception e) {
                System.err.println("Failed to connect");
                System.err.println(e.toString());
            }

            mmSocket = tmp;
        }

        public void run() {
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    return;
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    manageSocket(mmSocket);
                }
            });
        }

    }

}