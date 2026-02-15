package com.lucas.api_restaurante.responseutils;


import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class ResponseUtil {
    public static <T> ApiResponse<T> sucess(T data, String message, String path) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data, null, 0, System.currentTimeMillis(), path);
        return response;
    }

    public static <T> ApiResponse<Void> sucess(String message, String path) {
        ApiResponse<Void> response = new ApiResponse<>(true, message, null, null, 0, System.currentTimeMillis(), path);
        return response;
    }

    public static <T> ApiResponse<T> error(List<String> errors, String message, int errorCode, String path) {

        ApiResponse<T> response = new ApiResponse<>(false, message, null, errors, errorCode, System.currentTimeMillis(), path);

        return response;
    }

    public static <T> ApiResponse<T> error(String error, String message, int errorCode, String path) {
        return error(Arrays.asList(error), message, errorCode, path);
    }
}
