package prasetyo.jpa.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    private int priority;

    private Long categoryId;

    @NotNull
    private TaskType taskType;

    // Fields specific to RegularTask
    private Date deadline;

    // Fields specific to RecurringTask
    private List<String> recurrenceDays;

    public enum TaskType {
        REGULAR,
        RECURRING
    }
}
