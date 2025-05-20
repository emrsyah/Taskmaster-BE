package prasetyo.jpa.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategoryRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    private String color;

    private boolean isArchived;
} 