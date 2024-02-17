package util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletErrorHandler {
    public static void handleErrorMessage(HttpServletResponse resp, String message, int responseStatus, ObjectMapper objectMapper)
            throws IOException {
        resp.setStatus(responseStatus);
        byte[] errorMessage = objectMapper.writeValueAsBytes(message);
        resp.getOutputStream().write(errorMessage);
    }
}
