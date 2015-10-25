package com.alexkang.bluechat;

/**
 * Created by vinay on 21/10/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class bingoManager {

    public static final int BODY_LENGTH_END = 255;
    public static final int BODY_LENGTH_END_SIGNED = -1;

    public static final int MESSAGE_ID = 1;
    public static final int MESSAGE_NAME = 2;
    public static final int MESSAGE_SEND = 3;
    public static final int MESSAGE_RECEIVE = 4;


    private boolean isHost;
    private boolean isInitialized = false;
    private ArrayList<ConnectedThread> connections;
    private int id;
    Button bx[];
    bingoclient bc;
    bingohost bh;
    private ArrayList<MessageBox> mMessageList;

    private ListView mMessageFeed;

    private Activity mActivity;
    private ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            byte[] packet = (byte[]) msg.obj;
            int senderLength = msg.arg1;
            int senderId = msg.arg2;

            String sender = new String(Arrays.copyOfRange(packet, 0, senderLength));
            byte[] body = Arrays.copyOfRange(packet, senderLength, packet.length);

            boolean isSelf = senderId == id;

            switch (msg.what) {
                case MESSAGE_ID:
                    id = body[0];
                case MESSAGE_NAME:
                    if (!isHost && !isInitialized) {
                        String chatRoomName = new String(body);

                        if (mActivity.getActionBar() != null) {
                            mActivity.getActionBar().setTitle(chatRoomName);
                        }

                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        mProgressDialog.dismiss();

                        Toast.makeText(mActivity, "Connected!", Toast.LENGTH_SHORT).show();

                        isInitialized = true;
                    }
                    break;
                case MESSAGE_SEND:
                    if (isHost) {
                        byte[] sendPacket = buildPacket(MESSAGE_SEND, senderId, sender, body);
                        writeMessage(sendPacket, senderId);
                    }
                    break;
                case MESSAGE_RECEIVE:
                    MessageBox messageBox = new MessageBox(sender, new String(body), new Date(), isSelf);
                    addMessage(messageBox);
                    break;
            }
        }

    };

    public bingoManager(Activity activity, boolean isHost,Button bingobtn[]) {
        mActivity = activity;
        mMessageFeed = (ListView) mActivity.findViewById(R.id.m_feed);
        this.isHost = isHost;
        bx = bingobtn;
        bc = new bingoclient();
        bh = new bingohost();
        if (isHost) {
            id = 0;
            connections = new ArrayList<ConnectedThread>();
        }


        mMessageList = new ArrayList<MessageBox>();



    }

    public void startConnection(BluetoothSocket socket) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String username = sharedPref.getString("username", BluetoothAdapter.getDefaultAdapter().getName());
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        if (isHost) {
            connections.add(mConnectedThread);
            byte[] idAssignmentPacket = buildPacket(
                    MESSAGE_ID,
                    username,
                    new byte[] { (byte) connections.size() }
            );
            mConnectedThread.write(idAssignmentPacket);
        }
    }

    public void startConnection(BluetoothSocket socket, ProgressDialog progressDialog) {
        startConnection(socket);
        mProgressDialog = progressDialog;
    }

    public byte[] buildPacket(int type, int senderId, String sender, byte[] body) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(type);
        output.write(sender.length());

        int bodyLength = body.length;
        do {
            output.write(bodyLength % 10);
            bodyLength = bodyLength / 10;
        } while (bodyLength > 0);

        try {
            output.write(BODY_LENGTH_END);
            output.write(senderId);
            output.write(sender.getBytes());
            output.write(body);
        } catch (IOException e) {
            System.err.println("Error in building packet.");
            return null;
        }

        return output.toByteArray();
    }

    public byte[] buildPacket(int type, String sender, byte[] body) {
        return buildPacket(type, id, sender, body);
    }

    public void writeChatRoomName(byte[] byteArray) {
        connections.get(connections.size() - 1).write(byteArray);
    }

    public void writeMessage(byte[] byteArray, int senderId) {
        int type = byteArray[0];
        int receiveType = 0;
        if (type == MESSAGE_SEND) {
            receiveType = MESSAGE_RECEIVE;
        }

        int senderLength = byteArray[1];

        int currIndex = 2;
        do {
            currIndex++;
        } while (byteArray[currIndex] != BODY_LENGTH_END_SIGNED);

        mHandler.obtainMessage(receiveType, senderLength, senderId, Arrays.copyOfRange(byteArray, currIndex + 2, byteArray.length))
                .sendToTarget();

        if (isHost) {
            new DistributeThread(receiveType, senderId, byteArray).start();
        } else {
            mConnectedThread.write(byteArray);
        }
    }

    public void writeMessage(byte[] byteArray) {
        writeMessage(byteArray, id);
    }

    private void addMessage(MessageBox message) {
        mMessageList.add(message);
        if(!message.isSelf())
        {
            bc.bcflg=0;
            bh.bhflg=0;
        }
        String m = message.getMessage().toString();

        if(m.equals("reset")) {
            for(int i=0;i<5;i++)
                for(int j=0;j<5;j++)
                {
                    System.out.print("fuck");
                    bc.flag[i][j]=0;
                    bh.flag[i][j]=0;
                    bx[5*i+j].setEnabled(true);
                }
        }
        else{
            int loc = Integer.parseInt(m);
           // System.out.print(bh.board[5]+"fuck this");
            System.out.println(loc);

            int f=0;
            for(int i=0;i<5;i++)
            {
                for(int j=0;j<5;j++)
                {

                    if(bc.board[5*i+j]==loc)
                    {
                        f++;
                        bc.flag[i][j]=1;
                        if(bx[5*i+j].isEnabled()){
                            bx[5*i+j].setEnabled(false);
                           // bx[5*i+j].setBackgroundColor(0xFF0066);
                        }
                    }
                    if(bh.board[5*i+j]==loc)
                    {
                        f++;
                        bh.flag[i][j]=1;
                        if(bx[5*i+j].isEnabled()){
                            bx[5*i+j].setEnabled(false);
                           // bx[5*i+j].setBackgroundColor(0xFF0066);
                           }
                    }
                    if(f==2)
                        break;
                }
                if(f==2)
                    break;
            }
            f=0;
            for(int i=0;i<5;i++)
            {
                if(bc.flag[i][0]==1 && bc.flag[i][1]==1 &&bc.flag[i][2]==1 && bc.flag[i][3]==1 &&bc.flag[i][4]==1  )
                    f++;
                if(bc.flag[0][i]==1 && bc.flag[1][i]==1 &&bc.flag[2][i]==1 && bc.flag[3][i]==1 &&bc.flag[4][i]==1)
                    f++;
            }
            if(bc.flag[0][0]==1 && bc.flag[1][1] == 1 && bc.flag[2][2]==1 && bc.flag[3][3]==1&& bc.flag[4][4]==1)
                f++;
            if(bc.flag[0][4]==1 && bc.flag[1][3]==1 && bc.flag[2][2]==1 && bc.flag[3][1]==1 && bc.flag[4][0]==1)
                f++;
            if(f==5)
                Toast.makeText(mActivity,"BINGO!!",Toast.LENGTH_LONG).show();
            f=0;
            for(int i=0;i<5;i++)
            {
                if(bh.flag[i][0]==1 && bh.flag[i][1]==1 &&bh.flag[i][2]==1 && bh.flag[i][3]==1 &&bh.flag[i][4]==1  )
                    f++;
                if(bh.flag[0][i]==1 && bh.flag[1][i]==1 &&bh.flag[2][i]==1 && bh.flag[3][i]==1 &&bh.flag[4][i]==1)
                    f++;
            }
            if(bh.flag[0][0]==1 && bh.flag[1][1] == 1 && bh.flag[2][2]==1 && bh.flag[3][3]==1&& bh.flag[4][4]==1)
                f++;
            if(bh.flag[0][4]==1 && bh.flag[1][3]==1 && bh.flag[2][2]==1 && bh.flag[3][1]==1 && bh.flag[4][0]==1)
                f++;
            if(f==5)
                Toast.makeText(mActivity,"BINGO!!",Toast.LENGTH_LONG).show();

        }
    }

    private class DistributeThread extends Thread {

        int mReceiveType;
        int mSenderId;
        private byte[] mByteArray;

        public DistributeThread(int receiveType, int senderId, byte[] byteArray) {
            mReceiveType = receiveType;
            mSenderId = senderId;
            mByteArray = byteArray;
        }

        public void run() {
            mByteArray[0] = (byte) mReceiveType;
            for (int i = 0; i < connections.size(); i++) {
                if (i + 1 != mSenderId) {
                    connections.get(i).write(mByteArray);
                }
            }
        }

    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(mActivity, "Could not connect to ChatRoom, now exiting", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(i);
                mActivity.finish();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            while (true) {
                try {
                    int type = mmInStream.read();
                    int senderLength = mmInStream.read();

                    /*
                     * Calculate the length of the body in bytes. Each byte read is a digit
                     * in the body length in order of least to most significant digit.
                     *
                     * i.e. Body length of 247 would be read in the form {7, 4, 2}.
                     */
                    int bodyLength = 0;
                    int currPlace = 1;
                    int currDigit = mmInStream.read();
                    do {
                        bodyLength += (currDigit * currPlace);
                        currPlace *= 10;
                        currDigit = mmInStream.read();
                    } while (currDigit != BODY_LENGTH_END);

                    int senderId = mmInStream.read();

                    ByteArrayOutputStream packetStream = new ByteArrayOutputStream();
                    for (int i = 0; i < senderLength + bodyLength; i++) {
                        packetStream.write(mmInStream.read());
                    }
                    byte[] packet = packetStream.toByteArray();

                    mHandler.obtainMessage(type, senderLength, senderId, packet)
                            .sendToTarget();
                } catch (IOException e) {
                    System.err.println("Error in receiving packets");
                    e.printStackTrace();
                    endActivity();
                    break;
                }
            }
        }

        public void write(byte[] byteArray) {
            try {
                mmOutStream.write(byteArray);
                mmOutStream.flush();
            } catch (IOException e) {
                String byteArrayString = "";
                for (byte b : byteArray) {
                    byteArrayString += b + ", ";
                }
                System.err.println("Failed to write bytes: " + byteArrayString);
                System.err.println(e.toString());
                endActivity();
            }
        }

        private void endActivity() {
            if (!isHost) {
                mActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "ChatRoom closed", Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    }

                });
            }
        }

    }

}