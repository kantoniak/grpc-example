package com.kantoniak.examplegrpc.server;

import com.kantoniak.examplegrpc.proto.Entry;
import com.kantoniak.examplegrpc.proto.EntryServiceGrpc;
import com.kantoniak.examplegrpc.proto.GetAllRequest;
import com.kantoniak.examplegrpc.proto.GetAllResponse;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

class ApiServer {
    private final static int PORT = 8800;
    private Server grpcServer;

    private void start() throws IOException {
        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new EntryServiceImpl())
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