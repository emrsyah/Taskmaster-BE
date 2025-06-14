package prasetyo.jpa.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private Integer priority;

    private Long categoryId;

    @NotNull(message = "Task type is required")
    private TaskType taskType;

    // Fields specific to RegularTask
    private Date deadline;  // Can be null for regular tasks

    // Fields specific to RecurringTask
    private Set<String> recurrenceDays;  // Only required for recurring tasks

    public enum TaskType {
        REGULAR,
        RECURRING
    }

    // Custom validation to ensure recurrenceDays is provided for RECURRING tasks
    public boolean isValid() {
        if (taskType == TaskType.RECURRING && (recurrenceDays == null || recurrenceDays.isEmpty())) {
            return false;
        }
        return true;
    }
}
