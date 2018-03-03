package com.kantoniak.examplegrpc.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.kantoniak.examplegrpc.client.data.DataProvider;

import io.grpc.StatusRuntimeException;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private class NetworkUpdatesHandler extends Handler {
        NetworkUpdatesHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_COUNT_ENTRIES:
                    Log.v(TAG, "Making call...");
                    try {
                        final int count = dataProvider.getEntriesCount();
                        final String message = "It worked!\nServer said, that entry count is " + count + "!";
                        Log.v(TAG, message);
                        runOnUiThread(() -> mTextView.setText(message));
                    } catch (StatusRuntimeException e) {
                        final String message = "RPC failed: " + e.getStatus().getCode();
                        Log.e(TAG, message, e);
                        runOnUiThread(() -> mTextView.setText(message));
                    }
                    break;
            }
        }
    }

    private final DataProvider dataProvider = new DataProvider();
    private final HandlerThread networkUpdatesThread = new HandlerThread("NetworkUpdatesThread");
    private Handler networkUpdatesHandler;

    private static final int MESSAGE_COUNT_ENTRIES = 1000;

    private TextView mTextView;
    private Button mRefreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start networking thread
        networkUpdatesThread.start();
        networkUpdatesHandler = new NetworkUpdatesHandler(networkUpdatesThread.getLooper());

        mTextView = findViewById(R.id.main_text_view);
        mRefreshButton = findViewById(R.id.refresh_button);

        mRefreshButton.setOnClickListener(view -> startTest());

        startTest();
    }

    private void startTest() {
        mTextView.setText("Making RPC...");
        networkUpdatesHandler.sendMessage(
                networkUpdatesHandler.obtainMessage(MESSAGE_COUNT_ENTRIES));
    }
}
