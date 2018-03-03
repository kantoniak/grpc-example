package com.kantoniak.examplegrpc.server;

import com.kantoniak.examplegrpc.proto.Entry;

class ApiServer {
    public static void main(String[] args) {

        final ApiServer authServer = new ApiServer();
        try {
            System.out.println("Starting API Server...");
            authServer.playWithProtos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playWithProtos() {
        Entry entry = Entry.newBuilder()
                .setId(1)
                .setName("Test entry 1")
                .build();

        String message = "It worked! Entry name is: " + entry.getName();

        System.out.println("ApiServer.main() called.");
        System.out.println(message);
    }
}