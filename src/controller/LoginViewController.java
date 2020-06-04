package controller;

import Exceptions.ExistException;
import com.google.gson.Gson;
import database.DB;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.event.*;
import javafx.scene.paint.Color;
import main.MainGUI;
import models.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class LoginViewController {
    @FXML private Label loginErrorMessage;
    @FXML private TextField userNameTextField;

    @FXML public void initialize(){
        loginErrorMessage.setText("");
        loginErrorMessage.setTextFill(Color.RED);
    }

    @FXML public void SignupUser(ActionEvent actionEvent) {
        String userName = userNameTextField.getText();
        if(userName.isEmpty()){
            loginErrorMessage.setText("please enter username!");
        }else{
            try{
                User currentUser = User.insertUser(userName);
                if (currentUser == null){
                    loginErrorMessage.setText("DB Error");
                } else {
                    logInMainView(currentUser);
                }
            } catch (ExistException ee) {
                loginErrorMessage.setText(ee.getMessage());
            }
        }
    }

    @FXML private void loginMainView(ActionEvent actionEvent) {
        String userName = userNameTextField.getText();
        if(userName.isEmpty()){
            loginErrorMessage.setText("please enter username!");
        }else {
            User currentUser = User.findUser(userName);
            if (currentUser == null) {
                loginErrorMessage.setText("user not found");
            } else {
                logInMainView(currentUser);
            }
        }
    }

    @FXML private void exitSystem(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML private void logInMainView(User currentUser){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
            Parent root = loader.load();
            MainViewController controller = loader.getController();
            controller.initialize(currentUser,null);
            Scene scene = new Scene(root, 854, 600);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void importUsers(ActionEvent actionEvent) {
        try{
            BufferedReader uInput = new BufferedReader(new FileReader("./src/files/userOutput.txt"));
            String uNext = uInput.readLine();
            while(uNext != null) {
                HashMap<String, Object> postU = new HashMap<>();
                Gson gson = new Gson();
                postU = gson.fromJson(uNext, postU.getClass());
                DB db = new DB();
                String sql = "INSERT INTO users(id, userName, password) VALUES(" + postU.get("id") + ",'" +
                        postU.get("userName") + "','" + postU.get("password") + "'" + ")";
                db.insert(sql);
                uNext = uInput.readLine();
            }
            uInput.close();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText("");
            alert.setContentText("Import Successfully!");
            alert.initOwner(MainGUI.stage);
            alert.show();
        } catch (IOException | SQLException | ExistException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("");
            alert.setContentText(e.toString());
            alert.initOwner(MainGUI.stage);
            alert.show();
        }
    }
}
