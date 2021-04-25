package dev.crowell.javatodo;

import dev.crowell.javatodo.datamodel.ToDoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainwindow.fxml")));
        primaryStage.setTitle("ToDo List");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init()
    {
        try
        {
            ToDoData.getInstance().loadToDoItems();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop()
    {
        try
        {
            ToDoData.getInstance().storeToDoItems();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
