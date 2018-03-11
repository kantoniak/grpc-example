package com.kantoniak.examplegrpc.server;

import com.kantoniak.examplegrpc.proto.Entry;
import com.kantoniak.examplegrpc.proto.EntryServiceGrpc;
import com.kantoniak.examplegrpc.proto.GetAllRequest;
import com.kantoniak.examplegrpc.proto.GetAllResponse;

import java.io.IOException;
import java.util.Arrays;

import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import static io.grpc.Metadata.BINARY_BYTE_MARSHALLER;

class ApiServer {
    private final static int PORT = Integer.parseInt(System.getProperty("RpcServerPort"));
    private Server grpcServer;

    private void start() throws IOException {
        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new EntryServiceImpl())
                .intercept(new ServerInterceptor() {
                    @Override
                    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                        System.out.println("ServerInterceptor: Intercepting call to " + call.getMethodDescriptor().getFullMethodName() + "...");

                        String username = new String(headers.get(Metadata.Key.of("username-bin", BINARY_BYTE_MARSHALLER)));
                        String password = new String(headers.get(Metadata.Key.of("password-bin", BINARY_BYTE_MARSHALLER)));
                        System.out.println("Auth try: " + username + ", " + password);

                        if (username.equals("user") && password.equals("pass")) {
                            return next.startCall(call, headers);
                        } else {
                            call.close(Status.UNAUTHENTICATED, headers);
                            return new ServerCall.Listener<ReqT>() {};
                        }
                    }
                })
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(ApiServer.this::stop));
    }

    private void stop() {
        if (grpcServer == null) {
            System.out.println("Server shutting down...");
            grpcServer.shutdown();
            System.out.println("Server shut down.");
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    public static void main(String[] args) {

        final ApiServer apiServer = new ApiServer();
        try {
            System.out.println("Starting server...");
            apiServer.start();
            System.out.println(String.format("The server is now running on port %s", PORT));
            apiServer.blockUntilShutdown();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    static class EntryServiceImpl extends EntryServiceGrpc.EntryServiceImplBase {

        @Override
        public void getAll(GetAllRequest request, StreamObserver<GetAllResponse> responseObserver) {
            GetAllResponse response = GetAllResponse.newBuilder()
                    .addEntries(Entry.newBuilder().setId(1).setName("Entry 1"))
                    .addEntries(Entry.newBuilder().setId(2).setName("Entry 2"))
                    .addEntries(Entry.newBuilder().setId(3).setName("Entry 3"))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}