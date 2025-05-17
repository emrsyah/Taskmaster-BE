package prasetyo.jpa.entity;

import java.util.Date;

public class Notification {
    private Long id;
    private String title;
    private String description;
    private RecurringTask recurringTask;
    private RegularTask regularTask;
    private Date date;
    private boolean isRead;
}
