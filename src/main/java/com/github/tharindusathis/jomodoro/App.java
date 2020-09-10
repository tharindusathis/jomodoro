package com.github.tharindusathis.jomodoro;

import com.github.tharindusathis.jomodoro.controller.ControllerManager;
import com.github.tharindusathis.jomodoro.controller.FullScreenController;
import com.github.tharindusathis.jomodoro.controller.MainController;
import com.github.tharindusathis.jomodoro.controller.NotifyFlashScreenController;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @author tharindusathis
 */
public class App extends Application
{
    private Stage mainViewStage;
    private Stage fullscreenViewStage;
    private static final double INIT_UI_SCALE_FACTOR = .4;

    public static void main( String[] args )
    {
        launch();
    }

    private FullScreenController createFullscreenView() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource( "/com/github/tharindusathis/jomodoro/view/full-screen-view.fxml" ) );
        Region contentRootRegion = loader.load();

        double origW = 1920;
        double origH = 1080;

        contentRootRegion.setPrefWidth( origW );
        contentRootRegion.setPrefHeight( origH );

        Group group = new Group( contentRootRegion );
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add( group );
        rootPane.setStyle( "-fx-background-color: rgba(0,0,0,0.8)" );

        Scene scene = new Scene( rootPane, origW, origH );


        DoubleBinding width = rootPane.maxWidthProperty().divide( origW );
        group.scaleXProperty().bind( width );
        group.scaleYProperty().bind( width );
        rootPane.setMaxWidth( origW );



        fullscreenViewStage = new Stage();
        fullscreenViewStage.initStyle( StageStyle.TRANSPARENT );
        scene.setFill( Color.TRANSPARENT );
        fullscreenViewStage.setScene( scene );
        fullscreenViewStage.setAlwaysOnTop( true );

        rootPane.setMaxWidth( origW * INIT_UI_SCALE_FACTOR * 2 );
        return loader.getController();
    }

    private MainController createMainView() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource( "/com/github/tharindusathis/jomodoro/view/main-view.fxml" ) );
        Region contentRootRegion = loader.load();

        double origW = 720 + 720;
        double origH = 360 + 720;

        contentRootRegion.setPrefWidth( origW );
        contentRootRegion.setPrefHeight( origH );

        Group group = new Group( contentRootRegion ); // non-resizable container (Group)
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add( group );
        rootPane.setStyle( "-fx-background-color: #00000000" );
        Scene scene = new Scene( rootPane, origW, origH );

        // bind the scene's width and height to the scaling parameters on the group
        DoubleBinding width = scene.widthProperty().divide( origW );    
        group.scaleXProperty().bind( width );
        group.scaleYProperty().bind( width );

        mainViewStage = new Stage();
        mainViewStage.initStyle( StageStyle.TRANSPARENT );
        scene.setFill( Color.TRANSPARENT );
        mainViewStage.setScene( scene );
        mainViewStage.setAlwaysOnTop( true );

        mainViewStage.setWidth( origW * INIT_UI_SCALE_FACTOR );
        return loader.getController();
    }

    private Stage notifyFlashScreenStage;
    private NotifyFlashScreenController createNotifyFlashScreenView() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource( "/com/github/tharindusathis/jomodoro/view/notify-flash-screen.fxml" ) );
        Region contentRootRegion = loader.load();

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        contentRootRegion.setPrefWidth( screenBounds.getWidth() );
        contentRootRegion.setPrefHeight( screenBounds.getHeight() );
        contentRootRegion.setMouseTransparent( true );
        contentRootRegion.setPickOnBounds( false );

        notifyFlashScreenStage = new Stage();

        notifyFlashScreenStage.initStyle( StageStyle.TRANSPARENT );
        Scene scene = new Scene( contentRootRegion, screenBounds.getWidth(), screenBounds.getHeight() );
        contentRootRegion.setStyle( "-fx-background-color: rgba(0,0,0,0.0)" );
        scene.setFill( Color.TRANSPARENT );
        notifyFlashScreenStage.setScene( scene );
        notifyFlashScreenStage.setAlwaysOnTop( true );
        return loader.getController();
    }

    @Override
    public void start( Stage stage )
    {
        MainController mainController = null;
        FullScreenController fullscreenController = null;
        NotifyFlashScreenController notifyFlashScreenController = null;
        try
        {
            mainController = createMainView();
            fullscreenController = createFullscreenView();
            notifyFlashScreenController = createNotifyFlashScreenView();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        final ControllerManager controllerManager = new ControllerManager();
        controllerManager.registerController(
                ControllerManager.View.MAIN, mainController, mainViewStage );
        controllerManager.registerController(
                ControllerManager.View.FULLSCREEN, fullscreenController, fullscreenViewStage );
        controllerManager.registerController(
                ControllerManager.View.NOTIFY_FLASH, notifyFlashScreenController, notifyFlashScreenStage );

        mainViewStage.show();

    }
}