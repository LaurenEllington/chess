package service;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends RuntimeException {
    final private int status;
    public ResponseException(String message) {
        super(message);
        status = 500;
    }
    public ResponseException(String message, int status) {
      super(message);
      this.status=status;
    }
    public int getStatus(){
      return status;
    }
    public String toJson() {
      return new Gson().toJson(Map.of("message",getMessage(),"status",status));
    }
}
