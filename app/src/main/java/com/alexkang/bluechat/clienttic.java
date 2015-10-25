package com.alexkang.bluechat;

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

/**
 * Created by vinay on 18/10/15.
 */
public class clienttic extends Activity {
    public static final int PICK_IMAGE = 1;
   static  int board[][]=new int[3][3];
    private ArrayList<Integer> acceptableDevices = new ArrayList<Integer>();

    private EditText mMessage;
    private ProgressDialog mProgressDialog;

    private String mUsername;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private  Button tictacbuttons[];
    public Context clients;
    hosttic hs;
    static int cflg=0;
    private ChatManager mChatManager;
    int turn =0,flag=0;
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

    public int isGameOver(){
        for(int i=0;i<3;i++)
        {
            if((board[i][0] ==1 && board[i][1] ==1  && board[i][2]==1) ||(board[i][0] ==2 && board[i][1] ==2  &&board[i][2]==2))
                if(board[i][0]==1)
                    return 1;
                else if(board[i][0]==2)
                    return 2;
            if((board[0][i] ==1 && board[1][i] ==1  &&board[2][i]==1) ||(board[0][i] ==2 && board[1][i] ==2  &&board[2][i]==2))
                if(board[0][i]==1)
                    return 1;
                else if(board[i][0]==2)
                    return 2;

        }


            if((board[0][0] ==1 && board[1][1] ==1  &&board[2][2]==1) ||(board[0][0] ==2 && board[1][1] ==2  &&board[2][2]==2))
                if(board[0][0]==1)
                    return 1;
                else if(board[0][0]==2)
                    return 2;
            if((board[0][2] ==1 && board[1][1] ==1  &&board[2][0]==1) ||(board[0][2] ==2 && board[1][1] ==2  &&board[2][0]==2))
                if(board[0][2]==1)
                    return 1;
                else if(board[0][2]==2)
                    return 2;

        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(board[i][j]==0)
                    return 0;
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactooe);

        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                board[i][j]=0;
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        hs = new hosttic();
        cflg=0;
        acceptableDevices.add(BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA);
        acceptableDevices.add(BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA);
        acceptableDevices.add(BluetoothClass.Device.PHONE_SMART);
        tictacbuttons = new Button[9];
        tictacbuttons[0] = (Button)findViewById(R.id.Button00);
        tictacbuttons[1] = (Button)findViewById(R.id.Button01);
        tictacbuttons[2] = (Button)findViewById(R.id.Button02);
        tictacbuttons[3] = (Button)findViewById(R.id.Button10);
        tictacbuttons[4] = (Button)findViewById(R.id.Button11);
        tictacbuttons[5] = (Button)findViewById(R.id.Button12);
        tictacbuttons[6] = (Button)findViewById(R.id.Button20);
        tictacbuttons[7] = (Button)findViewById(R.id.Button21);
        tictacbuttons[8] = (Button)findViewById(R.id.Button22);
       // Button mAttachButton = (Button) findViewById(R.id.attach);
        Button mSendButton = (Button) findViewById(R.id.send);
        mMessage = (EditText) findViewById(R.id.message);
        mChatManager = new ChatManager(this, false);
        mProgressDialog = new ProgressDialog(this);


       /* mAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAttachment();
            }
        });*/

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        tictacbuttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(0);
            }
        });
        tictacbuttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(1);
            }
        });
        tictacbuttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(2);
            }
        });
        tictacbuttons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(3);
            }
        });
        tictacbuttons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(4);
            }
        });
        tictacbuttons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(5);
            }
        });
        tictacbuttons[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(6);
            }
        });
        tictacbuttons[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(7);
            }
        });
        tictacbuttons[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTic(8);
            }
        });


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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client, menu);
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
        if(id== R.id.cstart);
        {
            for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                {
                    board[i][j]=0;
                    hs.board[i][j]=0;
                    tictacbuttons[3*i+j].setText("");
                }
            }
            byte[] byteArray;
            try {
                byte[] messagebyts = ("reset").toString().getBytes();
                System.out.println(messagebyts);
                byteArray = mChatManager.buildPacket(
                        ChatManager.MESSAGE_SEND,
                        mUsername,
                        messagebyts
                );
                mChatManager.writeMessage(byteArray);
            }
            catch(Exception e){e.printStackTrace(); }

            cflg=0;
            flag=0;
            turn =0;
        }
        return super.onOptionsItemSelected(item);
    }
    private void processTic(int b){
           int row = b/3;
        int col = b%3;
        byte [] byteArray;

        if (board[row][col]==0 &&cflg==0 ) {


            cflg=1;

            try {
                byte[] messagebyts = (b+"X"+'0').toString().getBytes();
                System.out.println(messagebyts);
                byteArray = mChatManager.buildPacket(
                        ChatManager.MESSAGE_SEND,
                        mUsername,
                        messagebyts
                );
            }
            catch(Exception e){e.printStackTrace(); return;}
            mChatManager.writeMessage(byteArray);
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
        mMessage.setText("");
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
