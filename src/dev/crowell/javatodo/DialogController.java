package dev.crowell.javatodo;

import dev.crowell.javatodo.datamodel.ToDoData;
import dev.crowell.javatodo.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Optional;

public class DialogController
{
    @FXML
    private TextField shortDescription;

    @FXML
    private TextArea longDescription;

    @FXML
    private DatePicker deadlinePicker;

    public Optional<ToDoItem> processResults(Optional<ButtonType> result)
    {
        if(result.get() == ButtonType.OK)
        {
            String shortDesc = shortDescription.getText();
            String longDesc = longDescription.getText();
            LocalDate deadline = deadlinePicker.getValue();
            Optional<ToDoItem> newItem = Optional.ofNullable(new ToDoItem(shortDesc, longDesc, deadline));
            ToDoData.getInstance().addToDoItem(newItem.get());
            return newItem;
        }
        else
        {
            System.out.println("CANCEL pressed");
        }
        return Optional.empty();
    }
}
