package darkCrusader.toDoList.dataModel;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.Predicate;

public class TodoItem {
    private String shortDescription;
    private String details;
    private LocalDate deadline;

    static final Comparator<TodoItem> BY_DATE;
    public static final Predicate<TodoItem> TODAY;
    public static final Predicate<TodoItem> ALL;

    static {
        BY_DATE = new SortByDate();
        TODAY = todoItem -> todoItem.deadline.isEqual(LocalDate.now());
        ALL = todoItem -> true;
    }

    private static class SortByDate implements Comparator<TodoItem> {
        @Override
        public int compare(TodoItem todoItem1, TodoItem todoItem2) {
            return todoItem1.deadline.compareTo(todoItem2.deadline);
        }
    }

    public TodoItem(String shortDescription, String details, LocalDate deadline) {
        this.shortDescription = shortDescription;
        this.details = details;
        this.deadline = deadline;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
