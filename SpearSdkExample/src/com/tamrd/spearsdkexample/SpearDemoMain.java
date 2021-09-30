package com.tamrd.spearsdkexample;

import javafx.fxml.FXMLLoader;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.tamrd.spearsdkexample.view.WakeUpController;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

//import com.tamrd.spearsdkexample.view.WakeUpController;

public class SpearDemoMain extends Application {

	private static final Logger LOGGER = Logger.getLogger(SpearDemoMain.class.getSimpleName());

	//private WakeUpController WakeUpController;

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("SpearSdkExample");
		initRootLayout();
		showWakeUpFrontend();
	}

	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SpearDemoMain.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 1200, 700);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the wake-up front end inside the root layout.
     */
    public void showWakeUpFrontend() {
        try {
            // Load wakeUpFrontend.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SpearDemoMain.class.getResource("view/WakeUp.fxml"));
            AnchorPane wakeUpFrontend = (AnchorPane) loader.load();

            // Set wakeUpFrontend into the center of root layout.
            rootLayout.setCenter(wakeUpFrontend);

         // Give the controller access to the main app.
            WakeUpController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void stop() {
        LOGGER.log(Level.INFO, "Application shutting down...");
        //WakeUpController.shutdown();
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        	LOGGER.log(Level.INFO, "JVM shutting down...");
        }));

		launch(args);

		// Force JVM shutdown
		System.exit(0);
	}
}
