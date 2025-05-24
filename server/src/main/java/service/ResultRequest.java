package service;

record RegisterResult(String username, String authToken) {}
record RegisterRequest(String username, String password, String email) {}
record LoginResult(String username, String authToken) {}
record LoginRequest(String username, String password) {}