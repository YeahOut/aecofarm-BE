package dgu.aecofarm.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static dgu.aecofarm.entity.ResponseType.FAILURE;
import static dgu.aecofarm.entity.ResponseType.SUCCESS;

@Getter
@NoArgsConstructor
public class Response<T> {
    private int code;
    private String message;
    private T data;

    @Builder
    public Response(ResponseType responseType, T data, String message){
        this.code = responseType.getCode();
        this.message = message != null ? message : responseType.getMessage();
        this.data = data;
    }

    public static Response success(){
        return Response.builder()
                .responseType(SUCCESS)
                .build();
    }

    public static <T> Response<T> success(T data){
        return new Response<>(SUCCESS, data, null);
    }

    public static Response failure(String message) {
        return Response.builder()
                .responseType(FAILURE)
                .message(message)
                .build();
    }

    public static Response failure(Exception e) {
        return Response.builder()
                .responseType(FAILURE)
                .message(e.getMessage())
                .build();
    }
}
