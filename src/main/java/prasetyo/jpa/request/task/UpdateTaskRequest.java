package prasetyo.jpa.request.task;

import jakarta.validation.constraints.NotBlank;
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

    // Fields specific to RegularTask
    private Date deadline;

    // Fields specific to RecurringTask
    private List<String> recurrenceDays;
    private List<Date> doneDates;
} 