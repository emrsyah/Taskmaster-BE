package prasetyo.jpa.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import prasetyo.jpa.annotation.request.UniqueValue;
import prasetyo.jpa.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

  @NotBlank
  @Size(max = 255)
  @UniqueValue(entity=User.class, field="email", message="Email already taken")
  private String email;

  @NotBlank
  @Size(max=255)
  private String name;

  @NotBlank
  @Size(max=100)
  private String password;
}
