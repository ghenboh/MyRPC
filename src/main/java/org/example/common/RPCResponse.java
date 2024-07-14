package org.example.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCResponse implements Serializable {
    private int statusCode;
    private String message;
    private Object data;

    public static RPCResponse getSuccessResponse(Object data) {
        return RPCResponse.builder().statusCode(200).data(data).message("Success getting the information!").build();
    }

    public static RPCResponse getFailureResponse() {
        return RPCResponse.builder().statusCode(500).message("Fail to get information from server!").build();
    }
}
