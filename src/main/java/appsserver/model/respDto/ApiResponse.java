package appsserver.model.respDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("data")
    private T data;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("totalCount")
    private Long totalCount;

    public ApiResponse(T data) {
        this.data = data;
        this.success = true;
        this.message = "";
    }

    public ApiResponse(String message, boolean status) {
        this.data = null;
        this.success = status;
        this.message = message;
    }

    public ApiResponse(T data, long size) {
        this.data = data;
        this.success = true;
        this.message = null;
        this.totalCount = size;
    }


    public static <T> ResponseEntity<ApiResponse<T>> response(T data) {
        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> response(T data, long size) {
        return ResponseEntity.ok(new ApiResponse<>(data, size));
    }

    public static <T> ResponseEntity<ApiResponse<T>> response(boolean status, String message, HttpStatus httpStatus) {
        if (status) {
            return ResponseEntity.ok(new ApiResponse<>(message, true));
        } else {
            return new ResponseEntity<>(new ApiResponse<>(message, false), httpStatus);
        }
    }
}
