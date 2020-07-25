package darkCrusader.toDoList;

import darkCrusader.toDoList.dataModel.TodoData;
import darkCrusader.toDoList.dataModel.TodoItem;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {

    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea itemDetails;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggle;

    private FilteredList<TodoItem> filteredList;

    private void initializeListContextMenu() {
        // Initialize new Context Menu
        listContextMenu = new ContextMenu();
        // Add Menu option to delete and add eventListener to it
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            deleteItem();
        });
        // Add delete option to context Menu
        listContextMenu.getItems().addAll(deleteMenuItem);
    }

    private void todoListInitializer() {
        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), TodoItem.ALL);
        todoListView.setItems(filteredList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Add event listener to handle selection change Event
        todoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleChangeListView();
            }
        });

        todoListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setContextMenu(listContextMenu);
                            if (item.getDeadline().isBefore(LocalDate.now())) {
                                setTextFill(Color.RED);
                                setText(item.getShortDescription() + "    OVERDUE");
                            } else if (item.getDeadline().isEqual(LocalDate.now())) {
                                setTextFill(Color.DARKORANGE);
                                setText(item.getShortDescription() + "    TODAY");
                            } else if (item.getDeadline().isEqual(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.DARKTURQUOISE);
                                setText(item.getShortDescription() + "    TOMORROW");
                            } else {
                                setText(item.getShortDescription());
                            }
                        }
                    }
                };
            }
        });
    }

    public void initialize() {
        // Initialize context menu for list items
        initializeListContextMenu();
        // Initialize todoList and add eventListeners
        todoListInitializer();
        // Select first element
        todoListView.getSelectionModel().selectFirst();
    }

    /**
     * Creates and displays a new Dialog for adding a new_todo
     * No args required
     * View defined by addItemDialog.fxml
     */
    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Todo Item");
        dialog.setHeaderText("Dialog to add a new Todo");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (Exception e) {
            System.out.println("Couldn't open the dialog box");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            filterToggle.setSelected(false);
            filteredList.setPredicate(TodoItem.ALL);
            DialogController controller = loader.getController();
            refreshList(controller.processResults());
        }
    }

    // Method to refresh items after adding a new Item
    private void refreshList(TodoItem newItem) {
        TodoData.getInstance().addTodoItem(newItem);
        todoListView.getSelectionModel().select(newItem);
    }

    // Method to make changes in info pane on changes in selections
    private void handleChangeListView() {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        itemDetails.setText(item.getDetails());
        deadlineLabel.setText(DateTimeFormatter.ofPattern("MMMM d, yyyy").format(item.getDeadline()));
    }

    /**
     * Handles keypress for delete button to delete an item
     * @param e Event of a keypress
     */
    @FXML
    public void handleKeyPress(KeyEvent e) {
        // If key pressed was delete
        if (e.getCode().equals(KeyCode.DELETE)) {
            deleteItem();
        }
    }

    @FXML
    public void deleteSelectedTodo() {
        deleteItem();
    }

    // Deletes selected item
    private void deleteItem() {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        // If an item is selected, show confirmation dialog
        if (selectedItem != null) {
            deleteItem(selectedItem);
        }
    }

    private void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Item to delete: " + item.getShortDescription());
        alert.setContentText("Press OK to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleFilter() {
        if (filterToggle.isSelected()) {
            filteredList.setPredicate(TodoItem.TODAY);
            todoListView.getSelectionModel().selectFirst();
        } else {
            filteredList.setPredicate(TodoItem.ALL);
            todoListView.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void exitProgram() {
        Platform.exit();
    }
}
