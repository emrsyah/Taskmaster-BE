package prasetyo.jpa.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import prasetyo.jpa.annotation.request.ExistsInDatabase;
import prasetyo.jpa.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

  @NotBlank
  @Size(max = 255)
  @ExistsInDatabase(entity=User.class, field="email", message="Email or password is wrong")
  private String email;

  @NotBlank
  @Size(max=100)
  private String password;
}
