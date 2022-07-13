package gui;

import UserManagement.ResponseType;
import UserManagement.ServerResponse;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class Controller {
    //login menu items
    @FXML
    TextField login_username;
    @FXML
    TextField login_password;
    @FXML
    CheckBox stayLogedIn;



    @FXML
    Button signupButton;


    @FXML
    Pane SignInPane;
    @FXML
    Pane SignUpPane;
    @FXML
    Pane VerifyPane;


    @FXML
    Label SignUp;
    @FXML
    Label SignIn;

    @FXML
    void checkPasswordVerify(){
        signupButton.setDisable(!(!uPassword.getText().isEmpty() && uPassword.getText().equals(uPassword2.getText())));
    }
    @FXML
    void bringBackToSignUpMenu(){
        VerifyPane.setDisable(true);
        SignUp.setDisable(true);
        SignUpPane.setLayoutX(616+30);

        FadeTransition fadeInTransition = new FadeTransition();
        fadeInTransition.setNode(VerifyPane);
        fadeInTransition.setDuration(Duration.millis(200));
        fadeInTransition.setFromValue(100);
        fadeInTransition.setToValue(0);
        fadeInTransition.play();

        TranslateTransition translateTransition2 = new TranslateTransition();
        translateTransition2.setNode(VerifyPane);
        VerifyPane.setTranslateX(VerifyPane.getTranslateX());
        translateTransition2.setDuration(Duration.millis(300));
        translateTransition2.setByX(-30);
        translateTransition2.setInterpolator(Interpolator.LINEAR);
        translateTransition2.setOnFinished(event -> {VerifyPane.setVisible(false);VerifyPane.setLayoutX(616);});
        translateTransition2.play();


        FadeTransition fadeOutTransition = new FadeTransition();
        fadeOutTransition.setNode(SignUpPane);
        fadeOutTransition.setDuration(Duration.millis(800));
        fadeOutTransition.setFromValue(0);
        fadeOutTransition.setToValue(100);
        fadeOutTransition.play();

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(SignUpPane);
        //SignUpPane.setLayoutX(SignUpPane.getLayoutX()+30);
        translateTransition.setDuration(Duration.millis(300));
        translateTransition.setByX(-30);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        SignUpPane.setVisible(true);
        translateTransition.play();

        fadeOutTransition.setOnFinished(event1 -> {
            VerifyPane.setDisable(false);
            SignUp.setDisable(false);
        });

    }
    @FXML
    void bringSignInMenu(){
        SignIn.setDisable(true);
        SignUp.setDisable(true);
        FadeTransition fadeOutTransition = new FadeTransition();
        fadeOutTransition.setNode(SignUpPane);
        fadeOutTransition.setDuration(Duration.millis(200));
        fadeOutTransition.setFromValue(100);
        fadeOutTransition.setToValue(0);
        fadeOutTransition.play();

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(SignUpPane);
        translateTransition.setDuration(Duration.millis(300));
        translateTransition.setByX(30);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.setOnFinished(event -> {SignUpPane.setVisible(false); SignUpPane.setLayoutX(616);});
        translateTransition.play();

        TranslateTransition translateTransition2 = new TranslateTransition();
        translateTransition2.setNode(SignInPane);
        translateTransition2.setDuration(Duration.millis(300));
        translateTransition2.setByX(30);
        translateTransition2.setInterpolator(Interpolator.LINEAR);
        translateTransition2.play();
        translateTransition2.setOnFinished(event -> {SignInPane.setLayoutX(616);});

        FadeTransition fadeInTransition = new FadeTransition();
        fadeInTransition.setNode(SignInPane);
        fadeInTransition.setDuration(Duration.millis(800));
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(100);
        SignInPane.setVisible(true);
        fadeInTransition.play();
        fadeInTransition.setOnFinished(event -> {
            SignIn.setDisable(false);
            SignUp.setDisable(false);

        });
    }
    @FXML
    void verifyAccount() throws IOException {
        String ver = verificationCode.getText();
        ServerResponse response = Main.request.verifyCode(ver);
        if(response.isOk()){
            AlertSystem.AlertAndWait(Alert.AlertType.INFORMATION,"SignUp","Successful","Successfully created your account, signing in...");
            login_username.setText(uUsername.getText());
            login_password.setText(uPassword.getText());
            login();
        }
        else{
            AlertSystem.AlertAndWait(Alert.AlertType.ERROR,"Verification Code is not valid" , "Error","The Verification code you entered is not valid! enter again");
        }
    }
    @FXML TextField verificationCode;
    @FXML void bringVerifyMenu(){
        SignIn.setDisable(true);
        SignUp.setDisable(true);
        FadeTransition fadeOutTransition = new FadeTransition();
        fadeOutTransition.setNode(SignUpPane);
        fadeOutTransition.setDuration(Duration.millis(200));
        fadeOutTransition.setFromValue(100);
        fadeOutTransition.setToValue(0);
        fadeOutTransition.play();

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(SignUpPane);
        translateTransition.setDuration(Duration.millis(300));
        translateTransition.setByX(30);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.setOnFinished(event -> {SignUpPane.setVisible(false); SignUpPane.setLayoutX(616);});
        translateTransition.play();

        TranslateTransition translateTransition2 = new TranslateTransition();
        translateTransition2.setNode(VerifyPane);
        translateTransition2.setDuration(Duration.millis(300));
        translateTransition2.setByX(30);
        translateTransition2.setInterpolator(Interpolator.LINEAR);
        translateTransition2.play();
        translateTransition2.setOnFinished(event -> {VerifyPane.setLayoutX(616);});

        FadeTransition fadeInTransition = new FadeTransition();
        fadeInTransition.setNode(VerifyPane);
        fadeInTransition.setDuration(Duration.millis(800));
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(100);
        VerifyPane.setVisible(true);
        fadeInTransition.play();
        fadeInTransition.setOnFinished(event -> {
            SignIn.setDisable(false);
            SignUp.setDisable(false);

        });

    }
    @FXML
    void bringSignUpMenu(){
        SignIn.setDisable(true);
        SignUp.setDisable(true);
        SignUpPane.setLayoutX(616+30);

        FadeTransition fadeInTransition = new FadeTransition();
        fadeInTransition.setNode(SignInPane);
        fadeInTransition.setDuration(Duration.millis(200));
        fadeInTransition.setFromValue(100);
        fadeInTransition.setToValue(0);
        fadeInTransition.play();

        TranslateTransition translateTransition2 = new TranslateTransition();
        translateTransition2.setNode(SignInPane);
        SignInPane.setTranslateX(SignInPane.getTranslateX());
        translateTransition2.setDuration(Duration.millis(300));
        translateTransition2.setByX(-30);
        translateTransition2.setInterpolator(Interpolator.LINEAR);
        translateTransition2.setOnFinished(event -> {SignInPane.setVisible(false);SignInPane.setLayoutX(616);});
        translateTransition2.play();


        FadeTransition fadeOutTransition = new FadeTransition();
        fadeOutTransition.setNode(SignUpPane);
        fadeOutTransition.setDuration(Duration.millis(800));
        fadeOutTransition.setFromValue(0);
        fadeOutTransition.setToValue(100);
        fadeOutTransition.play();

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(SignUpPane);
        //SignUpPane.setLayoutX(SignUpPane.getLayoutX()+30);
        translateTransition.setDuration(Duration.millis(300));
        translateTransition.setByX(-30);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        SignUpPane.setVisible(true);
        translateTransition.play();

        fadeOutTransition.setOnFinished(event1 -> {
            SignIn.setDisable(false);
            SignUp.setDisable(false);
        });


    }
    @FXML
    TextField uUsername;
    @FXML
    TextField uEmail;
    @FXML
    TextField uPassword;
    @FXML
    TextField uPassword2;
    @FXML
    TextField uPhoneNumber;

    @FXML
    void signUp() throws Exception{
        String username = uUsername.getText();
        String email = uEmail.getText();
        String password = uPassword.getText();
        if(Main.request.signUp(username,password,email) == ResponseType.OK){
            //AlertSystem.AlertAndWait(Alert.AlertType.INFORMATION,"SignedUp" , "Success" , "");
            bringVerifyMenu();
        }


    }
    @FXML
    void login() throws IOException {
        String username = login_username.getText();
        String password = login_password.getText();
        System.out.println(username);
        System.out.println(password);
        //boolean stayLoged = stayLogedIn.isSelected();
        if(Main.request.signIn(username,password).isOk()){
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadingForm.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image("file:discord.png"));
            stage.setTitle("Loading");
            System.out.println("loading");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.initStyle(StageStyle.UNDECORATED);

            stage.show();
            Stage stage2 = (Stage) login_password.getScene().getWindow();

            double baseX = stage2.getX();
            Animation transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(200));
                    setInterpolator(Interpolator.EASE_BOTH);
                }
                @Override
                protected void interpolate(double frac) {
                    //stage.setX(700+60*frac);
                    stage2.setX(baseX+(frac*30));
                    stage2.setOpacity(1-frac);
                }
            };
            transition.play();
            transition.setOnFinished(event -> {stage2.close();});

            //stage.setX(0);
            //stage.setY(340);
            //stage.setWidth(0);
            //stage.setHeight(0);
            Animation transition2 = new Transition() {{
                    setCycleDuration(Duration.millis(250));
                    setInterpolator(Interpolator.EASE_IN);
                }
                @Override
                protected void interpolate(double frac) {
                    stage.setX(730+30*frac);
                    stage.setOpacity(frac);
                }
            };
            transition2.play();
        }
        else {
            AlertSystem.AlertAndWait(Alert.AlertType.ERROR, "Wrong Credentials", "Information mismatch", "Username/Password is incorrect");
        }
    }
    @FXML
    void Call() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PhoneCall.fxml"));
        Parent root = loader.load();
        ((PhoneCall)loader.getController()).setProfilePlace("prof.jpg");
        Scene scene = new Scene(root,350,600, Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(new Image("file:discord.png"));
        stage.setTitle("Loading");
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.show();
        stage.setY(1080-600-20);
        stage.getScene().setFill(Color.TRANSPARENT);

        Animation transition2 = new Transition() {{
            setCycleDuration(Duration.millis(250));
            setInterpolator(Interpolator.EASE_IN);
        }
            @Override
            protected void interpolate(double frac) {
                stage.setX(1920-330-40*frac);
                stage.setOpacity(frac);
            }
        };
        transition2.play();
    }
}
