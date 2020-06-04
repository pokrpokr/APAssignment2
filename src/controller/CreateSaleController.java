package controller;

import Exceptions.ExistException;
import Exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import main.MainGUI;
import models.Event;
import models.Post;
import models.Sale;
import models.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CreateSaleController {
    @FXML private TextField askingPriceField;
    @FXML private TextField minimumRaiseField;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private javafx.scene.control.MenuBar MenuBar;
    @FXML private Label ValidationMessage;
    @FXML private ImageView imageView;
    @FXML private String imageUrl = "";
    private User currentUser;

    @FXML public void initialize(User currentUser){
        this.currentUser = currentUser;
        ValidationMessage.setText("");
        ValidationMessage.setTextFill(Color.RED);
        Label menuLabel = new Label("BackToMainView");
        menuLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    backToMainView(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Menu backButton = new Menu();
        backButton.setGraphic(menuLabel);
        MenuBar.getMenus().add(backButton);
    }

    public void UploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./images/"));
        File file = fileChooser.showOpenDialog(MainGUI.stage);
        if (file == null){
            ValidationMessage.setText("please upload image");
        } else {
            this.imageUrl = file.toURI().toString();
            Image newImage = new Image(this.imageUrl);
            imageView.setImage(newImage);
        }
    }

    public void CreateSale(ActionEvent actionEvent) {
        try {
            String idStr       = Sale.generateId();
            if (idStr.isBlank()) { throw new ValidationException("ID did not generate"); }
            String title        = titleField.getText();
            String description  = descriptionField.getText();
            String askingPrice  = askingPriceField.getText();
            String minimumRaise = minimumRaiseField.getText();
            String imageUrl     = this.imageUrl;
            validation(new String[]{title, description, askingPrice, minimumRaise, imageUrl});
            if (currentUser == null) { throw new ValidationException("user logged out!"); }
            long creatorID     = currentUser.getId();
            String creatorName = currentUser.getUserName();
            Sale sale = new Sale(creatorID, creatorName, idStr, title, description, imageUrl, Double.valueOf(askingPrice), Double.valueOf(minimumRaise));
            sale = sale.createSale(currentUser);
            backToMainView(sale);
        } catch (ValidationException ve){
            ValidationMessage.setWrapText(true);
            ValidationMessage.setText(ve.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExistException e) {
            e.printStackTrace();
        }
    }

    @FXML private void backToMainView(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
        Parent root = loader.load();
        MainViewController controller = loader.getController();
        controller.initialize(currentUser, post);
        Scene scene = new Scene(root, 854, 600);
        MainGUI.stage.setScene(scene);
    }

    @FXML public void validation(String[] args) throws ValidationException {
//For checking date format
        String message = "";
        // 0: title
        if (args[0].isBlank()) { message += "title, "; }
        if (args[1].isBlank()) { message += "description, "; }
        if (args[2].isBlank()) { message += "askingPrice, "; }
        if (args[3].isBlank()) { message += "minimumRaise, "; }
        if (args[4].isBlank()) { message += "image "; }
        if (!message.isEmpty()) { message += "can not be blank! "; }
        try{
            Double aPrice = Double.valueOf(args[2]);
            if (aPrice < 0) {
                throw new ValidationException("asking price can not be less than 0!");
            }
            Double miniPrice = Double.valueOf(args[3]);
            if (miniPrice < 0) {
                throw new ValidationException("minimum raise can not be less than 0!");
            }
        } catch (NumberFormatException e) {
            message += "askingPrice should be a number!";
            throw new ValidationException(message);
        }
        if (!message.isBlank()){
            throw new ValidationException(message);
        }
    }
}
