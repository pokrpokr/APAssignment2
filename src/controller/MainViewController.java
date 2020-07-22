package controller;

import Exceptions.ExistException;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import database.DB;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.event.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.MainGUI;
import models.*;
import models.Event;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MainViewController {
    @FXML private MenuItem exportData;
    @FXML private MenuItem importData;
    @FXML private ChoiceBox typeList;
    @FXML private ChoiceBox statusList;
    @FXML private ChoiceBox creatorList;
    @FXML private ListView<Post> postLists;
    @FXML private Label currentUserName;
    @FXML private MenuItem devInfo;
    @FXML private MenuItem exitSys;
    public static User currentUser = null;

    @FXML public void initialize(User currentUser,Post post){
        HashMap<String,String> options = new HashMap<>();
        typeList.setValue("All");
        statusList.setValue("All");
        creatorList.setValue("All");
        typeList.setItems(FXCollections.observableList((List) Post.getTypes()));
        statusList.setItems(FXCollections.observableList((List) Post.getStatusA()));
        ArrayList<String> creators = new ArrayList<>();
        creators.add("All");
        creators.add("MyPost");
        creatorList.setItems(FXCollections.observableList((List) creators));
        MainViewController.currentUser = currentUser;
        currentUserName.setText(currentUser.getUserName());
        // init filter
        typeList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, t1) -> {
            if (t1.equals("All")) {
                options.remove("type");
            } else {
                options.put("type", t1);
            }
            List<Post> posts = Post.getPosts(options);
            ObservableList<Post> list = FXCollections.observableList(posts);
            postLists.setItems(list);
            postLists.setCellFactory(postListView -> new PostListCell());
        });
        statusList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, t1) -> {
            if (t1.equals("All")) {
                options.remove("status");
            } else {
                options.put("status", t1);
            }
            List<Post> posts = Post.getPosts(options);
            ObservableList<Post> list = FXCollections.observableList(posts);
            postLists.setItems(list);
            postLists.setCellFactory(postListView -> new PostListCell());
        });
        creatorList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, t1) -> {
            if (t1.equals("All")) {
                options.remove("creatorId");
            } else {
                options.put("creatorId", String.valueOf(currentUser.getId()));
            }
            List<Post> posts = Post.getPosts(options);
            ObservableList<Post> list = FXCollections.observableList(posts);
            postLists.setItems(list);
            postLists.setCellFactory(postListView -> new PostListCell());
        });
        // init list view
        List<Post> posts = Post.getPosts(options);
        ObservableList<Post> list = FXCollections.observableList(posts);
        postLists.setItems(list);
        postLists.setCellFactory(postListView -> new PostListCell());

        //Show developer info
        devInfo.setOnAction((event)->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Developer Info");
            alert.setHeaderText("");
            alert.setContentText("Student Number: s3766925\nStudent Name: Xingyu Chai");
            alert.initOwner(MainGUI.stage);
            alert.show();
        });

        exitSys.setOnAction((event)->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit System");
            alert.setHeaderText("");
            alert.setContentText("Are you sure to exit System?");
            alert.initOwner(MainGUI.stage);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {System.exit(0);}
            }
        });
        //Export data to file
        exportData.setOnAction((event)->{
            HashMap<String,String> exportOptions = new HashMap<>();
            Gson gson = new Gson();
            ArrayList<Post> exportPosts = Post.getPosts(exportOptions);
            try {
                FileWriter postWriter = new FileWriter("./src/files/postOutput.txt");
                JsonObject jsonPost = new JsonObject();
                for (int i = 0; i < exportPosts.size(); i++){
                    Post ePost = exportPosts.get(i);
                    jsonPost.addProperty("id", ePost.getPostID());
                    jsonPost.addProperty("description", ePost.getDescription());
                    jsonPost.addProperty("title",ePost.getTitle());
                    jsonPost.addProperty("status",ePost.getStatus());
                    jsonPost.addProperty("idStr",ePost.getPostName());
                    jsonPost.addProperty("imageUrl",ePost.getImage());
                    jsonPost.addProperty("creatorId",ePost.getCreatorID());
                    jsonPost.addProperty("creatorName",ePost.getCreatorName());
                    jsonPost.addProperty("isDeleted", Event.classToDBTransDelValue(ePost.isDeleted()));

                    if (ePost instanceof Event) {
                        jsonPost.addProperty("type", Post.Type.Event.toString());
                        jsonPost.addProperty("venue", ((Event)ePost).getVenue());
                        jsonPost.addProperty("date",((Event)ePost).getDate());
                        jsonPost.addProperty("capacity", ((Event)ePost).getCapacity());
                        jsonPost.addProperty("attCount", ((Event)ePost).getAttCount());
                        jsonPost.add("proposedPrice", JsonNull.INSTANCE);
                        jsonPost.add("lowestOffer", JsonNull.INSTANCE);
                        jsonPost.add("askingPrice", JsonNull.INSTANCE);
                        jsonPost.add("highestOffer", JsonNull.INSTANCE);
                        jsonPost.add("minimumRaise", JsonNull.INSTANCE);
                    } else if(ePost instanceof Sale) {
                        jsonPost.addProperty("type", Post.Type.Sale.toString());
                        jsonPost.add("venue", JsonNull.INSTANCE);
                        jsonPost.add("date",JsonNull.INSTANCE);
                        jsonPost.add("capacity", JsonNull.INSTANCE);
                        jsonPost.add("attCount", JsonNull.INSTANCE);
                        jsonPost.add("proposedPrice", JsonNull.INSTANCE);
                        jsonPost.add("lowestOffer", JsonNull.INSTANCE);
                        jsonPost.addProperty("askingPrice", ((Sale) ePost).getAskingPrice());
                        jsonPost.addProperty("highestOffer", ((Sale) ePost).getHighestOffer());
                        jsonPost.addProperty("minimumRaise", ((Sale) ePost).getMinimumRaise());
                    } else if(ePost instanceof Job) {
                        jsonPost.addProperty("type", Post.Type.Job.toString());
                        jsonPost.add("venue", JsonNull.INSTANCE);
                        jsonPost.add("date",JsonNull.INSTANCE);
                        jsonPost.add("capacity", JsonNull.INSTANCE);
                        jsonPost.add("attCount", JsonNull.INSTANCE);
                        jsonPost.addProperty("proposedPrice", ((Job) ePost).getProposedPrice());
                        jsonPost.addProperty("lowestOffer", ((Job) ePost).getLowestOffer());
                        jsonPost.add("askingPrice", JsonNull.INSTANCE);
                        jsonPost.add("highestOffer", JsonNull.INSTANCE);
                        jsonPost.add("minimumRaise", JsonNull.INSTANCE);
                    }
                    String beWrite = gson.toJson(jsonPost);
                    postWriter.write(beWrite+"\n");
                }
                postWriter.close();// flushes the stream.

                FileWriter userWriter = new FileWriter("./src/files/userOutput.txt");
                JsonObject jsonUser = new JsonObject();
                DB db = new DB();
                String sql = "SELECT * FROM users";
                ResultSet rs = db.search(sql);
                while (rs.next()) {
                    jsonUser.addProperty("id", rs.getLong("id"));
                    jsonUser.addProperty("userName", rs.getString("userName"));
                    jsonUser.addProperty("password", rs.getString("password"));
                    String beWrite = gson.toJson(jsonUser);
                    userWriter.write(beWrite+"\n");
                }
                userWriter.close();

                DB dbR = new DB();
                FileWriter replyWriter = new FileWriter("./src/files/replyOutput.txt");
                JsonObject jsonReply = new JsonObject();
                String sqlR = "SELECT * FROM replies";
                ResultSet rsR = dbR.search(sqlR);
                while(rsR.next()) {
                    jsonReply.addProperty("id", rsR.getLong("id"));
                    jsonReply.addProperty("postId", rsR.getLong("postId"));
                    jsonReply.addProperty("creatorId", rsR.getLong("creatorId"));
                    jsonReply.addProperty("creatorName", rsR.getString("creatorName"));
                    jsonReply.addProperty("value", rsR.getDouble("value"));
                    String beWrite = gson.toJson(jsonReply);
                    replyWriter.write(beWrite+"\n");
                }
                replyWriter.close();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("");
                alert.setContentText("Export Successfully!");
                alert.initOwner(MainGUI.stage);
                alert.show();
            } catch (IOException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("");
                alert.setContentText(e.toString());
                alert.initOwner(MainGUI.stage);
                alert.show();
            }
        });

        //Import data from file
        importData.setOnAction((event)->{
            try {
                Gson gson = new Gson();
                BufferedReader  input = new BufferedReader(new FileReader("./src/files/postOutput.txt"));
                String next = input.readLine();
                while (next != null) {
                    HashMap<String, Object> postD = new HashMap<>();
                    postD = gson.fromJson(next, postD.getClass());
                    DB db = new DB();
                    String sql = "INSERT INTO posts(id, creatorId, idStr, creatorName, title, description, " +
                            "status, isDeleted, type, venue, date, capacity, attCount, proposedPrice, lowestOffer, askingPrice, highestOffer, minimumRaise, imageUrl) VALUES(" +
                            postD.get("id") + "," + postD.get("creatorId") + ",'" +
                            postD.get("idStr") + "','" + postD.get("creatorName") + "','" +
                            postD.get("title") + "','" + postD.get("description") + "','" +
                            postD.get("status") + "'," + postD.get("isDeleted") + ",'" + postD.get("type") +"','" +
                            postD.get("venue") + "','" + postD.get("date") + "'," + postD.get("capacity") + "," +
                            postD.get("attCount") + "," + postD.get("proposedPrice") + "," + postD.get("lowestOffer") + "," +
                            postD.get("askingPrice") + "," + postD.get("highestOffer") + "," + postD.get("minimumRaise") + ",'" +
                            postD.get("imageUrl") +"')";
                    db.insert(sql);
                    next = input.readLine();
                }
                input.close();

                BufferedReader rInput = new BufferedReader(new FileReader("./src/files/replyOutput.txt"));
                String rNext = rInput.readLine();
                while(rNext != null) {
                    HashMap<String, Object> postR = new HashMap<>();
                    postR = gson.fromJson(rNext, postR.getClass());
                    DB db = new DB();
                    String sql = "INSERT INTO replies(id, postId, creatorId, creatorName, value) VALUES("+ postR.get("id") + "," +
                            postR.get("postId") + "," + postR.get("creatorId") + ",'" + postR.get("creatorName") + "'," + postR.get("value") +")";
                    db.insert(sql);
                    rNext = rInput.readLine();
                }
                rInput.close();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("");
                alert.setContentText("Import Successfully!");
                alert.initOwner(MainGUI.stage);
                alert.show();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
                Parent root = loader.load();
                MainViewController controller = loader.getController();
                controller.initialize(currentUser,null);
                Scene scene = new Scene(root, 854, 600);
                MainGUI.stage.setScene(scene);

            } catch (IOException | SQLException | ExistException e) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Error!");
                alert.setHeaderText("");
                alert.setContentText(e.toString());
                alert.initOwner(MainGUI.stage);
                alert.show();
            }
        });
    }

    @FXML private void createNewEvent(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_event.fxml"));
            Parent root = loader.load();
            CreateEventController controller = loader.getController();
            controller.initialize(currentUser);
            Scene scene = new Scene(root, 486, 300);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void createNewSale(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_sale.fxml"));
            Parent root = loader.load();
            CreateSaleController controller = loader.getController();
            controller.initialize(currentUser);
            Scene scene = new Scene(root, 486, 300);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void createNewJob(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_job.fxml"));
            Parent root = loader.load();
            CreateJobController controller = loader.getController();
            controller.initialize(currentUser);
            Scene scene = new Scene(root, 486, 300);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void logoutMainView(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent root = loader.load();
            currentUser = null;
            LoginViewController controller = new LoginViewController();
            Scene scene = new Scene(root, 480, 200);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
