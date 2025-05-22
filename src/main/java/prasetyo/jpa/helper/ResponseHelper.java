package prasetyo.jpa.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.Builder;

@Component
@Builder
public class ResponseHelper {

    // ======= SUCCESS METHODS =======

    // 1. Full parameter
    public <T> ResponseEntity<Map<String, Object>> success(String message, T data, boolean status, int statusCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("statusCode", statusCode);
        body.put("message", message);
        body.put("data", data);
        body.put("errors", null);
        return ResponseEntity.status(statusCode).body(body);
    }

    // 2. message + data (status=true, statusCode=201 for create, 200 for others)
    public <T> ResponseEntity<Map<String, Object>> success(String message, T data) {
        if (message.toLowerCase().contains("created")) {
            return success(message, data, true, HttpStatus.CREATED.value());
        }
        return success(message, data, true, HttpStatus.OK.value());
    }

    // 3. message only (data=null, status=true, statusCode=200)
    public ResponseEntity<Map<String, Object>> success(String message) {
        return success(message, null, true, HttpStatus.OK.value());
    }

    // ======= ERROR METHODS =======

    // 1. Full parameter
    public <E> ResponseEntity<Map<String, Object>> error(String message, E errors, boolean status, int statusCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", false);
        body.put("statusCode", statusCode);
        body.put("message", message);
        body.put("data", null);
        body.put("errors", errors);
        return ResponseEntity.status(statusCode).body(body);
    }

    // 2. message + HttpStatus
    public ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        return error(message, status.getReasonPhrase(), false, status.value());
    }

    // 3. message only (errors=null, status=false, statusCode=400)
    public ResponseEntity<Map<String, Object>> error(String message) {
        return error(message, null, false, HttpStatus.BAD_REQUEST.value());
    }
}
