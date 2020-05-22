package controller;

import Exceptions.ExistException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.event.*;
import javafx.scene.paint.Color;
import main.MainGUI;
import models.User;

import java.io.IOException;

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
}
