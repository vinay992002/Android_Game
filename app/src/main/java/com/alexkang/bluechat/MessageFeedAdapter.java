package com.alexkang.bluechat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessageFeedAdapter extends ArrayAdapter<MessageBox> {

    Context mContext;
    ClipboardManager clipboard;
    MessageBox last_message;
    clienttic ctic;
    hosttic htic;
    Button b[] = new Button[9] ;
    int turn;
    public MessageFeedAdapter(Context context, ArrayList<MessageBox> messages) {
        super(context, R.layout.message_row, messages);
        mContext = context;
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MessageBox message = getItem(position);
        if(position >0)
        {
            last_message = getItem(position-1);
        }
        else
        last_message = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_row, parent, false);
        }


        turn =0;
        ctic = new clienttic();
        htic = new hosttic();

       TextView senderView = (TextView) convertView.findViewById(R.id.name);
        final TextView messageView = (TextView) convertView.findViewById(R.id.message);
        TextView timeView = (TextView) convertView.findViewById(R.id.time);
    //    ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        b[0] =(Button) rootView.findViewById(R.id.Button00);
        b[1] =(Button) rootView.findViewById(R.id.Button01);
        b[2] =(Button) rootView.findViewById(R.id.Button02);
        b[3] =(Button) rootView.findViewById(R.id.Button10);
        b[4] =(Button) rootView.findViewById(R.id.Button11);
        b[5] =(Button) rootView.findViewById(R.id.Button12);
        b[6] =(Button) rootView.findViewById(R.id.Button20);
        b[7] =(Button) rootView.findViewById(R.id.Button21);
        b[8] =(Button) rootView.findViewById(R.id.Button22);

        if (message.isSelf()) {
            senderView.setGravity(Gravity.RIGHT);
            messageView.setGravity(Gravity.RIGHT);

            RelativeLayout.LayoutParams rightAlign =
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
            rightAlign.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

      //      imageView.setLayoutParams(rightAlign);

        } else if(!message.isSelf() ){
            senderView.setGravity(Gravity.LEFT);
            messageView.setGravity(Gravity.LEFT);

            RelativeLayout.LayoutParams leftAlign =
                    new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
            leftAlign.addRule(RelativeLayout.ALIGN_PARENT_LEFT);


        //    imageView.setLayoutParams(leftAlign);
        }

        if (!message.isImage()) {

            String m = message.getMessage().toString();
            if(m.equals("reset")) {
                for(int i=0;i<3;i++)
                    for(int j=0;j<3;j++)
                    {
                        ctic.board[i][j]=0;
                        htic.board[i][j]=0;
                        b[3*i+j].setText("");
                    }
                ctic.cflg=0;
                htic.hflg=0;
            }
            else{
                if(!message.isSelf()){
                    System.out.println("hello");
                    ctic.cflg=0;
                    htic.hflg=0;
                }
                    System.out.println("fuckIT");
                char t;
                System.out.println(m);
                if (m.length() > 2)
                    t = m.charAt(2);
                else
                    t = 0;
                if (t == '0')
                    turn = 1;
                else
                    turn = 2;
                if (m.charAt(1) == 'X' || m.charAt(1) == 'O') {
                    char c = m.charAt(0);
                    int loc = c - '0';

                    ctic.board[loc / 3][loc % 3] = turn;
                    htic.board[loc / 3][loc % 3] = turn;
                    if (turn == 1)
                        b[loc].setText("X");
                    else
                        b[loc].setText("O");


                    if (ctic.isGameOver() == 1 || htic.isGameOver() == 1)
                        Toast.makeText(mContext, "Player X Won!!", Toast.LENGTH_SHORT).show();
                    else if (ctic.isGameOver() == 2 || htic.isGameOver() == 2)
                        Toast.makeText(mContext, "Player O Won!!", Toast.LENGTH_SHORT).show();
                    else if (ctic.isGameOver() == -1 || htic.isGameOver() == -1)
                        Toast.makeText(mContext, "Game Draw", Toast.LENGTH_SHORT).show();
                } else {
                    messageView.setText(message.getMessage());
                    //imageView.setImageDrawable(null);
                }
            }

        } else {
            messageView.setText("");
            //imageView.setImageBitmap(message.getImage());
        }

        senderView.setText(message.getSender());
        timeView.setText(message.getTime());

        if (!message.isImage() && message.getMessage().length() > 0) {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData clip = ClipData.newPlainText("message", message.getMessage());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mContext, "Message copied to clipboard", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        } else {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // do nothing.
                }
            });
        }

        return convertView;
    }

}
