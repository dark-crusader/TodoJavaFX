package darkCrusader.toDoList;

import darkCrusader.toDoList.dataModel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea details;
    @FXML
    private DatePicker deadlinePicker;

    TodoItem processResults() {
        String shortDescriptionText = shortDescription.getText().trim();
        String detailsText = details.getText().trim();
        LocalDate deadline = deadlinePicker.getValue();
        return new TodoItem(shortDescriptionText, detailsText, deadline);
    }
}
