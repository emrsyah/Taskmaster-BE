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
public class UpdateTaskRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    private boolean isCompleted;
    private int priority;
    private Long categoryId;
    private boolean isArchived;

    @NotNull
    private CreateTaskRequest.TaskType taskType;

    // Fields specific to RegularTask
    private Date deadline;

    // Fields specific to RecurringTask
    private Set<String> recurrenceDays;
    private Set<Date> doneDates;
} 