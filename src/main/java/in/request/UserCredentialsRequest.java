package in.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredentialsRequest {
    String username;
    String password;


    public boolean isValid() {
        return username != null && username.length() <= 255 && username.length() >=6
                && password != null && password.length() <= 255 && password.length() >= 6;
    }
}
