package sample;

/**
 * To-Do:
 *    - Automatically refresh messages
 *    - Have UI notification for group creation and join
 *    - Add confirmation messages
 *    - Button to logout (so you don't have to restart app to login as different user)
 */


import com.mkyong.http.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Controller {
    @FXML
    private Label login_label;
    @FXML
    private Text actiontarget;
    @FXML
    private TextField usr;
    @FXML
    private TextField pwd;
    @FXML
    private TextField createUser_username;
    @FXML
    private PasswordField createUser_password;
    @FXML
    private PasswordField createUser_repassword;
    @FXML
    private Label label2;
    @FXML
    private TextField msg;
    @FXML
    private TextArea display;
    @FXML
    public ListView userList;
    @FXML
    private TextField group_name;
    @FXML
    private PasswordField group_password;
    @FXML
    private Label createUser_username_label;

    // I'm using these variables as a way to make testing this GUI
    // easier for now. However, does storing the username and password
    // here make this app less secure?
    public OkHttpExample obj = new OkHttpExample();
    public static String authToken;
    public static String username;
    public static String password;
    public static String groupName;
    public static int groupNum;
    public static RSAUtil keygen = new RSAUtil();

    // The setText() function for a text field replaces the text that is
    // already there. However, we want any new text to be appended in our
    // chat box, so I'm declaring this string here as a way to keep track
    // of it. I would like this to be in the function that deals with
    // sending/receiving the text, but I'm not sure if the text will remain.
    //public static String text;


    /**
     * This function will take the user to the "messages" GUI after clicking submit.
     *
     * The user will enter their username and password. Their input will be gathered here
     * and verified. If successful, they will continue. If not successful, they will be
     * notified and cannot proceed until a successful login.
     */
    @FXML protected void handleLoginButtonAction(ActionEvent event) throws Exception {
        //String authToken;
        username = usr.getText();
        password = pwd.getText();


        authToken = obj.login(username, password);
        //System.out.println("authtokenSubmit " + authToken);
        if (authToken == null) {
            login_label.setText("Incorrect password");
        } else {
            //obj.setUser(username);
            //System.out.println(obj.getUser());
            Parent chatViewParent = FXMLLoader.load(getClass().getResource("Chat.fxml"));
            Scene chatViewScene = new Scene(chatViewParent);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            //window.setMaximized(true);
            window.setScene(chatViewScene);
            window.show();
        }

    }

    /**
     * Handles what the happens if the user clicks "CreateUser"
     */
    @FXML protected void handleCreateUserButtonAction(ActionEvent event) throws IOException {
        Parent chatViewParent = FXMLLoader.load(getClass().getResource("CreateUser.fxml"));
        Scene chatViewScene = new Scene(chatViewParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(chatViewScene);
        window.show();
    }

    /**
     * Returns the user to the chat screen from the groups screen
     */
    @FXML protected void handleReturnChatButtonAction(ActionEvent event) throws IOException {
        Parent chatViewParent = FXMLLoader.load(getClass().getResource("Chat.fxml"));
        Scene chatViewScene = new Scene(chatViewParent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(chatViewScene);
        window.show();
    }

    /**
     * This will handle checking the user's password to see if it meets the necessary requirements
     */
    @FXML public void handleKeyReleased(KeyEvent e){
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(createUser_password.getText());
        int hasUppercase = createUser_password.getText().compareTo(createUser_password.getText().toLowerCase());

        if (createUser_password.getLength() < 8 && createUser_repassword.getLength() < 8) {
            label2.setText("Length of password should be 8 or more");

        } else if(createUser_password.getText() != null && createUser_repassword.getText() != null &&
                 !createUser_password.getText().equals(createUser_repassword.getText())) {
            label2.setText("The passwords do not match! Please try again.");
        } else if(matcher.matches()) {
             label2.setText("Password must contain a symbol (@, $, etc...)");
        } else if(hasUppercase == 0) {
             label2.setText("Password must contain at least one upper case letter");
        } else {
            label2.setText("Username and password are valid");
        }
    }



    /**
     * Displays the user's entered and sent message in chat. It will
     * also send the user's message as a POST to the server and other
     * users.
     */
    @FXML protected void handleSendChat(ActionEvent event) {
        keygen.keyCheck();

        try {
            //obj.sendMessage(msg.getText(), keygen.getPublicKey(), authToken, username, 1);
            obj.sendMessageToAll(msg.getText(), authToken, groupNum, obj, username);
            //Thread.sleep(2000);
            //obj.sendMessage(msg.getText(), "SuperSecretKey", authToken, username);
            //display.setText(msg.getText());
            //Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML protected void onEnter(ActionEvent event){
        display.setText(msg.getText());
    }

    /**
    * Handles the actions the user will take to register an account.
    *
    * Upon clicking "Create User", the user will be taken to a registration
    * screen where they can enter a username and password. After they have
    * done so, they can then click on "Register", which will trigger this
    * function.
    */
    public void handleRegisterButtonAction(ActionEvent actionEvent) throws Exception {
        // Still trying to figure out how to set label correctly depending on the errors
        /*
        if ((createUser_username.getText().isEmpty()) || createUser_password.getText().isEmpty() || createUser_repassword.getText().isEmpty()) {
            //createUser_username_label.setContentDisplay(ContentDisplay.TOP);
            createUser_username_label.setAlignment(Pos.TOP_LEFT);
            createUser_username_label.setText("Fields cannot be empty");
        } else if (createUser_password.getText() != createUser_repassword.getText()) {
            createUser_username_label.setText("Passwords do not match");
        } else {
            obj.registerUser(createUser_username.getText(), createUser_password.getText(), createUser_repassword.getText());

            Parent chatViewParent = FXMLLoader.load(getClass().getResource("Ephemeral.fxml"));
            Scene chatViewScene = new Scene(chatViewParent);

            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            //window.setMaximized(true);
            window.setScene(chatViewScene);
            window.show();
        }

         */
        //System.out.print("New user: " + createUser_username.getText());
        obj.registerUser(createUser_username.getText(), createUser_password.getText(), createUser_repassword.getText());
        //obj.setUser(createUser_username.getText());

        Parent chatViewParent = FXMLLoader.load(getClass().getResource("Ephemeral.fxml"));
        Scene chatViewScene = new Scene(chatViewParent);

        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //window.setMaximized(true);
        window.setScene(chatViewScene);
        window.show();
    }

    /**
     * Handles creating a group after the user has clicked "Create Group" from the
     * Group GUI
     */
    public void handleCreateGroupAction(ActionEvent actionEvent) throws Exception {
        //System.out.println("authtokenGroup: " + authToken);
        //System.out.println(group_name.getText());
        //System.out.print(group_password.getText());
        obj.createGroup(group_name.getText(), group_password.getText(), authToken);
    }

    /**
     * Handles joining a group after the user has clicked "Join Group" from the
     * Group GUI
     */
    public void handleJoinGroupAction(ActionEvent actionEvent) throws Exception {
        // If successful upon joining a group, display user's of group
        keygen.keyCheck();
        groupName = group_name.getText();
        // Should error check this.
        groupNum = obj.findGroup(authToken, groupName);
        obj.joinGroup(groupNum, group_password.getText(), authToken, keygen.getPublicKey(), username);

    }

    /**
     * Returns the user to the login GUI from the registration GUI
     */
    public void handleReturnLoginButtonAction(ActionEvent actionEvent) throws IOException {
        Parent chatViewParent = FXMLLoader.load(getClass().getResource("Ephemeral.fxml"));
        Scene chatViewScene = new Scene(chatViewParent);

        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(chatViewScene);
        window.show();
    }

    /**
     * Refreshes the message and user list in the chat GUI
     */
    public void handleRefreshButtonAction(ActionEvent actionEvent) throws Exception {
        StringBuilder messageView = new StringBuilder((""));

        // This will grab and display the user list (i.e, refresh user list)
        //List<String> values = Arrays.asList("one", "two", "three");

        // Should error check this too
        // Also, the way this is setup right now, the user should NOT
        // click "refresh" before joining a group. This is because
        // groupName is not set until a user joins a group.
        List<String> values = obj.listUsers(groupNum, authToken);
        userList.setItems(FXCollections.observableList(values));

        keygen.keyCheck();
        // Refresh messages
        List<String> messageList = obj.getMessages(keygen.getPrivateKey(), authToken, groupNum, username);

        for (String s : messageList) {
            //System.out.print(s);
            messageView.append(s + "\n");
            display.setText(messageView.toString());
        }

    }

    /**
     * Takes the user to the group creation/join GUI
     */
    public void handleGroupButtonAction(ActionEvent actionEvent) throws IOException {
        Parent chatViewParent = FXMLLoader.load(getClass().getResource("Groups.fxml"));
        Scene chatViewScene = new Scene(chatViewParent);

        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(chatViewScene);
        window.show();
    }
}
