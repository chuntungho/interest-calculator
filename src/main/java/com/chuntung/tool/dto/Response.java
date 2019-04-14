package com.chuntung.tool.dto;

import lombok.Data;

@Data
public class Response<T> {
    private String status;
    private String message;
    private T data;

    public static <T> Response<T> success(T data){
        Response<T> res = new Response<>();
        res.setStatus("success");
        res.setData(data);
        return  res;
    }
}
