package contevolve.etlVisualizer.error;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiError {
	private String message;
    private int status;
    private String error;
    private String timestamp;

    public ApiError(String message, HttpStatus httpStatus) {
        this.message   = message;
        this.status    = httpStatus.value();   
        this.error     = httpStatus.getReasonPhrase(); 
        this.timestamp = LocalDateTime.now().toString();
    }
}
