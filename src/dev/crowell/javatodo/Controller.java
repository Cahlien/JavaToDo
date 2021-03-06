package dev.crowell.javatodo;

import dev.crowell.javatodo.datamodel.ToDoData;
import dev.crowell.javatodo.datamodel.ToDoItem;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

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

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ToDoItem> filteredList;

    private Predicate<ToDoItem> allItems;
    private Predicate<ToDoItem> itemsDueToday;

    public void initialize()
    {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

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


        allItems = new Predicate<ToDoItem>()
        {
            @Override
            public boolean test(ToDoItem toDoItem)
            {
                return true;
            }
        };

        itemsDueToday = new Predicate<ToDoItem>()
        {
            @Override
            public boolean test(ToDoItem toDoItem)
            {
                return toDoItem.getDeadline().equals(LocalDate.now());
            }
        };


        filteredList = new FilteredList<ToDoItem>(ToDoData.getInstance().getToDoItems(), allItems);

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList,
                new Comparator<ToDoItem>()
                {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2)
                    {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

        todoListView.setItems(sortedList);
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

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) ->
                        {
                            if(isNowEmpty)
                            {
                                cell.setContextMenu(null);
                            }
                            else
                            {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );

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

    public void onKeyPressed(KeyEvent keyEvent)
    {
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null)
        {
            if(keyEvent.getCode().equals(KeyCode.DELETE))
            {
                deleteItem(selectedItem);
            }
        }
    }

    public void deleteItem(ToDoItem item)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete ToDo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure?  Press OK to confirm or cancel to abort.");
        Optional<ButtonType> result = alert.showAndWait();

        if((result.isPresent()) && (result.get() == ButtonType.OK))
        {
            ToDoData.getInstance().deleteToDoItem(item);
        }
    }

    public void onDeleteToolClicked()
    {
        deleteItem(todoListView.getSelectionModel().getSelectedItem());
    }

    public void handleFilterButton()
    {
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();

        if(filterToggleButton.isSelected())
        {
            filteredList.setPredicate(itemsDueToday);
            if(filteredList.isEmpty())
            {
                longDescriptionText.clear();
                deadlineLabel.setText("");
            }
            else if(filteredList.contains(selectedItem))
            {
                todoListView.getSelectionModel().select(selectedItem);
            }
            else
            {
                todoListView.getSelectionModel().selectFirst();
            }
        }
        else
        {
            filteredList.setPredicate(allItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }
}
