package com.kantoniak.examplegrpc.client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kantoniak.examplegrpc.proto.Entry;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playWithProtos();
    }

    private void playWithProtos() {
        Entry entry = Entry.newBuilder()
                .setId(1)
                .setName("Test entry 1")
                .build();

        String message = "It worked! Entry name is: " + entry.getName();

        TextView mTextView = (TextView) findViewById(R.id.main_text_view);
        mTextView.setText(message);

        Log.v(TAG, message);
    }
}
