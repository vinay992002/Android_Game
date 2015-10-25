package com.alexkang.bluechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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


public class MainActivity extends Activity {

    public static final String UUID = "28286a80-137b-11e4-bbe8-0002a5d5c51b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button tHostButton = (Button) findViewById(R.id.thost_button);
        Button tJoinButton = (Button) findViewById(R.id.tjoin_button);
        Button bHostButton = (Button) findViewById(R.id.bhost_button);
        Button bJoinButton = (Button) findViewById(R.id.bjoin_button);
        tHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostRoom();
            }
        });

        tJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRoom();
            }
        });
        bHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bhostRoom();
            }
        });

        bJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bjoinRoom();
            }
        });
    }

    private void hostRoom() {
        Intent i = new Intent(this, hosttic.class);
        startActivity(i);
    }

    private void joinRoom() {
        Intent i = new Intent(this, clienttic.class);
        startActivity(i);
    }
    private void bhostRoom() {
        Intent i = new Intent(this, bingohost.class);
        startActivity(i);
    }

    private void bjoinRoom() {
        Intent i = new Intent(this, bingoclient.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_name) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String username = sharedPref.getString("username", bluetoothAdapter.getName());
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            final EditText nameInput = new EditText(this);
            nameInput.setSingleLine();
            nameInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            nameInput.setText(username);
            nameInput.setSelectAllOnFocus(true);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Enter your username");
            builder.setView(nameInput);
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    imm.hideSoftInputFromWindow(nameInput.getWindowToken(), 0);
                    sharedPref.edit().putString("username", nameInput.getText().toString()).apply();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    imm.hideSoftInputFromWindow(nameInput.getWindowToken(), 0);
                }
            });

            final AlertDialog dialog = builder.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            nameInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (charSequence.length() > 0 && charSequence.length() <= 22) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device, now exiting.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
