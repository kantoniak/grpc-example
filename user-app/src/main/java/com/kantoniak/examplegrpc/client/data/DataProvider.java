package com.kantoniak.examplegrpc.client.data;

import android.util.Log;

import com.kantoniak.examplegrpc.client.BuildConfig;
import com.kantoniak.examplegrpc.proto.EntryServiceGrpc;
import com.kantoniak.examplegrpc.proto.GetAllRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.Attributes;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;

import static io.grpc.Metadata.BINARY_BYTE_MARSHALLER;

public class DataProvider {

    private static final String TAG = "DataProvider";

    private static final String HOST = BuildConfig.RPC_SERVER_HOST;
    private static final int PORT = BuildConfig.RPC_SERVER_PORT;
    private static final int CALL_DEADLINE_SECS = 5;

    private final ManagedChannel channel;
    private final EntryServiceGrpc.EntryServiceBlockingStub blockingStub;

    public DataProvider() {

        // TODO(kantoniak): Remove once authentication system gets established.
        CallCredentials passwordCreds = new CallCredentials() {
            @Override
            public void applyRequestMetadata(MethodDescriptor<?, ?> method, Attributes attrs, Executor appExecutor, MetadataApplier applier) {
                Metadata metadata = new Metadata();
                metadata.put(Metadata.Key.of("username-bin", BINARY_BYTE_MARSHALLER), "user".getBytes());
                metadata.put(Metadata.Key.of("password-bin", BINARY_BYTE_MARSHALLER), "pass".getBytes());
                applier.apply(metadata);
            }

            @Override
            public void thisUsesUnstableApi() {}
        };

        this.channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext(true) // Turn off SSL
                .build();
        this.blockingStub = EntryServiceGrpc.newBlockingStub(channel).withCallCredentials(passwordCreds);
        Log.i(TAG, "Created channel for " + HOST + ":" + PORT);
    }

    public int getEntriesCount() throws StatusRuntimeException {
        GetAllRequest request = GetAllRequest.newBuilder().build();
        return blockingStub.withDeadlineAfter(CALL_DEADLINE_SECS, TimeUnit.SECONDS)
                .getAll(request).getEntriesCount();
    }
}
