# gRPC Example

A simple project to create a simple bootstrap for Android apps using gRPC and Protocol Buffers.

## Submodules

* `proto` contains proto definitions and generated classes
* `user-app` is an android application

## Dependency on proto

If your module needs to use classes generated from proto, remember to add `apply from: file("${project(':proto').projectDir}/proto-deps.gradle")` to your module `build.gradle`
