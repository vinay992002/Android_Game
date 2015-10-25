package com.alexkang.bluechat;

/**
 * Created by vinay on 20/10/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class hosttic extends Activity {

    public static final int REQUEST_DISCOVERABLE = 1;
    public static final int PICK_IMAGE = 2;
    private   Button tictacbuttons[];
    private EditText mMessage;
    static int hflg=0;
    private String mUsername;
    private String mChatRoomName;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothSocket> mSockets;
    private AcceptThread mAcceptThread;
        int turn =0,flag=0;
    private ChatManager mChatManager;
    static int board[][] = new int[3][3];
    clienttic cs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactooe);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                board[i][j]=0;
        cs  = new clienttic();
        hflg = 0;
        tictacbuttons= new Button[9];
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
        mChatManager = new ChatManager(this, true);

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

        initializeRoom();
    }


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
    private void processTic(int b){
        int row = b/3;
        int col = b%3;
        byte [] byteArray;

        if (board[row][col]==0 && hflg==0 ) {
           // board[row][col]=2;
            hflg=1;

            try {
                byte[] messagebyts = (b+"O"+'1').getBytes();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        } else if (id == R.id.action_reopen) {
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
            }
            initializeBluetooth();
            return true;
        }
        if(id == R.id.hstart)
        {
            System.out.print("clicked");
            for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                {
                    board[i][j]=0;
                    cs.board[i][j]=0;
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
            hflg=0;
            flag=0;
            turn =0;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeRoom() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Retrieve username
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = sharedPref.getString("username", mBluetoothAdapter.getName());

        // Set up ChatRoom naming input
        final EditText nameInput = new EditText(this);
        nameInput.setSingleLine();
        nameInput.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Set up ChatRoom naming dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter your ChatRoom name");
        builder.setView(nameInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                mChatRoomName = nameInput.getText().toString();

                if (getActionBar() != null) {
                    getActionBar().setTitle(mChatRoomName);
                }

                imm.hideSoftInputFromWindow(nameInput.getWindowToken(), 0);
                initializeBluetooth();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                imm.hideSoftInputFromWindow(nameInput.getWindowToken(), 0);
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });

        // Show the dialog and disable the submit button until the name is longer than 0 characters
        final AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() > 0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void initializeBluetooth() {
        mSockets = new ArrayList<BluetoothSocket>();

        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(i, REQUEST_DISCOVERABLE);
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
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_DISCOVERABLE) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
            Toast.makeText(this, "Searching for users...", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri image = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            new SendImageThread(picturePath).start();
            cursor.close();
        } else if (requestCode == REQUEST_DISCOVERABLE) {
            Toast.makeText(this, "New users cannot join your chat room", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }

        if (mSockets != null) {
            for (BluetoothSocket socket : mSockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Failed to close socket");
                    System.err.println(e.toString());
                }
            }
        }
    }

    private void manageSocket(BluetoothSocket socket) {
        mChatManager.startConnection(socket);
        mSockets.add(socket);
        byte[] byteArray;

        byteArray = mChatManager.buildPacket(
                ChatManager.MESSAGE_NAME,
                mUsername,
                mChatRoomName.getBytes()
        );

        Toast.makeText(this, "User connected", Toast.LENGTH_SHORT).show();
        mChatManager.writeChatRoomName(byteArray);
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

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;
        private boolean isAccepting;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            isAccepting = true;

            try {
                tmp = mBluetoothAdapter.
                        listenUsingRfcommWithServiceRecord(
                                mChatRoomName, java.util.UUID.fromString(MainActivity.UUID)
                        );
            } catch (IOException e) {
                System.err.println("Failed to set up Accept Thread");
                System.err.println(e.toString());
            }

            mmServerSocket = tmp;
        }

        public void run() {
            while (isAccepting) {
                final BluetoothSocket socket;

                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            manageSocket(socket);
                        }
                    });
                }
            }
        }

        public void cancel() {
            try {
                isAccepting = false;
                mmServerSocket.close();
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }

    }

}

