package dev.crowell.javatodo;

import dev.crowell.javatodo.datamodel.ToDoData;
import dev.crowell.javatodo.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class Controller
{
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
        todoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue != null)
            {
            ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
            longDescriptionText.setText(item.getLongDescription());
            DateTimeFormatter df = DateTimeFormatter.ofPattern("ddMMMyyyy");
            deadlineLabel.setText(df.format(item.getDeadline()).toUpperCase(Locale.ROOT));
            }
        });

        todoListView.setItems(ToDoData.getInstance().getToDoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>()
        {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView)
            {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>()
                {
                    @Override
                    protected void updateItem(ToDoItem toDoItem, boolean b)
                    {
                        super.updateItem(toDoItem, b);
                        if(b)
                        {
                            setText(null);
                        }
                        else
                        {
                            setText(toDoItem.getShortDescription());
                            if(toDoItem.getDeadline().equals(LocalDate.now()))
                            {
                                setTextFill(Color.RED);
                            }
                            else if(toDoItem.getDeadline().equals(LocalDate.now().plusDays(1)))
                            {
                                setTextFill(Color.ORANGE);
                            }
                            else if(toDoItem.getDeadline().isBefore(LocalDate.now()))
                            {
                                setTextFill(Color.DARKVIOLET);
                            }
                        }
                    }
                };

                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog()
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Create New List Item");
        dialog.setHeaderText("Use this dialog to create new items for your to-do list");
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
            Optional<ToDoItem> newItem = controller.processResults(result.get());

            newItem.ifPresent(toDoItem -> todoListView.getSelectionModel().select(toDoItem));
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
