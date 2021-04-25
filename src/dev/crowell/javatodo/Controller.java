package dev.crowell.javatodo;

import dev.crowell.javatodo.datamodel.ToDoData;
import dev.crowell.javatodo.datamodel.ToDoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Controller
{
    private List<ToDoItem> todoItems;

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ListView<ToDoItem> todoListView;

    @FXML
    private TextArea longDescriptionText;

    @FXML
    private Label deadlineLabel;

    public void initialize()
    {
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>()
        {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue,
                                ToDoItem newValue){
                if(newValue != null)
                {
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                    longDescriptionText.setText(item.getLongDescription());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("ddMMMyyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()).toUpperCase(Locale.ROOT));
                }
            }
        });
        todoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        todoListView.getSelectionModel().selectFirst();
    }

    @FXML
    public void showNewItemDialog()
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try
        {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch(IOException e)
        {
            System.out.println("Failed to load the dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent())
        {
            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResults(result).get();
            todoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
            todoListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void OnListViewClicked()
    {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        longDescriptionText.setText(item.getLongDescription());
        deadlineLabel.setText(item.getDeadline().toString());
    }
}
