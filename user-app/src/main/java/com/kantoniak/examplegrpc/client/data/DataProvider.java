package com.kantoniak.examplegrpc.client.data;

import android.util.Log;

import com.kantoniak.examplegrpc.client.BuildConfig;
import com.kantoniak.examplegrpc.proto.EntryServiceGrpc;
import com.kantoniak.examplegrpc.proto.GetAllRequest;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class DataProvider {

    private static final String TAG = "DataProvider";

    private static final String HOST = BuildConfig.RPC_SERVER_HOST;
    private static final int PORT = BuildConfig.RPC_SERVER_PORT;
    private static final int CALL_DEADLINE_SECS = 5;

    private final ManagedChannel channel;
    private final EntryServiceGrpc.EntryServiceBlockingStub blockingStub;

    public DataProvider() {
        this.channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext(true) // Turn off SSL
                .build();
        this.blockingStub = EntryServiceGrpc.newBlockingStub(channel);
        Log.i(TAG, "Created channel for " + HOST + ":" + PORT);
    }

    public int getEntriesCount() throws StatusRuntimeException {
        GetAllRequest request = GetAllRequest.newBuilder().build();
        return blockingStub.withDeadlineAfter(CALL_DEADLINE_SECS, TimeUnit.SECONDS)
                .getAll(request).getEntriesCount();
    }
}
