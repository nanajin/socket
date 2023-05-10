package com.example.chat.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import com.example.chat.server.Server;
import com.example.chat.server.ServerApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class ClientApplication extends Application {
	private ArrayList<Thread> threads;
	public static void main(String[] args){
		launch();
	}

	public String hostname;
	public ObservableList<String> user;
	
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		for (Thread thread: threads){
			thread.interrupt();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		threads = new ArrayList<Thread>();
		primaryStage.setTitle("Client Login");
		primaryStage.setScene(makeInitScene(primaryStage));
		primaryStage.show();
	}

	public Scene makeInitScene(Stage primaryStage) {
		/* Make the root pane and set properties */
		GridPane rootPane = new GridPane();
		rootPane.setPadding(new Insets(20));
		rootPane.setVgap(20); // 10
		rootPane.setHgap(20); // 10
		rootPane.setAlignment(Pos.CENTER);
		/* Make the text fields and set properties */
		TextField nameField = new TextField();
		TextField hostNameField = new TextField();
		TextField portNumberField = new TextField();

		/* Make the labels and set properties */
		Label nameLabel = new Label("닉네임 ");
		Label hostNameLabel = new Label("호스트");
		Label portNumberLabel = new Label("Port");
		Label errorLabel = new Label();
		/* Make the button and its handler */
		Button submitClientInfoButton = new Button("접속하기");
		submitClientInfoButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent Event) {
				// TODO Auto-generated method stub
				/* Instantiate the client class and start it's thread */
				Client client;
				try {
					client = new Client(hostNameField.getText(), Integer
							.parseInt(portNumberField.getText()), nameField
							.getText());
					Thread clientThread = new Thread(client);
					clientThread.setDaemon(true);
					clientThread.start();
					threads.add(clientThread);
					
					/* Change the scene of the primaryStage */
					primaryStage.close();
					primaryStage.setTitle("Client Chat");
					primaryStage.setScene(makeChatUI(client));
					primaryStage.show();
//					userPage();

				}
				catch(ConnectException e){
					errorLabel.setTextFill(Color.RED);
					errorLabel.setText("호스트를 다시 입력해주세요");
				}
				catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					errorLabel.setTextFill(Color.RED);
					errorLabel.setText("포트번호를 다시 확인해주세요");
				}
				
			}
		});

		/*
		 * Add the components to the root pane arguments are (Node, Column
		 * Number, Row Number)
		 */
		rootPane.add(nameLabel, 0, 0);
		rootPane.add(nameField, 1, 0);

		rootPane.add(hostNameLabel, 0, 1);
		rootPane.add(hostNameField, 1, 1);

		rootPane.add(portNumberLabel, 0, 2);
		rootPane.add(portNumberField, 1, 2);

		rootPane.add(submitClientInfoButton, 0,3, 3, 1);
		rootPane.add(errorLabel, 0, 4);
		/* Make the Scene and return it */
		return new Scene(rootPane, 400, 400);
	}
	public void userPage(){
		Stage newStage = new Stage();
		try{
//			start(newStage);
			newStage.setTitle("Joined UserList");
			newStage.setScene(userList());
			newStage.show();
		}catch (Exception e){e.printStackTrace();}
	}

	public Scene makeChatUI(Client client) {
		/* Make the root pane and set properties */
		GridPane rootPane = new GridPane();
		rootPane.setPadding(new Insets(10));
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setHgap(10);
		rootPane.setVgap(10);

		/*
		 * Make the Chat's listView and set it's source to the Client's chatLog
		 * ArrayList
		 */
		ListView<String> chatListView = new ListView<String>();
		chatListView.setItems(client.chatLog);

		/*
		 * Make the chat text box and set it's action to send a message to the
		 * server
		 */
		TextField chatTextField = new TextField();

		chatTextField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				client.writeToServer(chatTextField.getText());
				chatTextField.clear();
			}
		});
		Button sendBtn = new Button("Send");
		sendBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				client.writeToServer(chatTextField.getText());
				chatTextField.clear();
			}
		});

		/* Add the components to the root pane */
		rootPane.add(chatListView, 0, 0,2,1);
		rootPane.add(chatTextField, 0, 1);
		rootPane.add(sendBtn, 1, 1);

		/* Make and return the scene */
		return new Scene(rootPane, 400, 500);

	}
	public Scene userList() {
		GridPane rootPane = new GridPane();
		rootPane.setPadding(new Insets(10));
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setHgap(10);
		rootPane.setVgap(10);

		ListView<String> clientView = new ListView<String>();
//		ObservableList<String> clientList = new ServerApplication().clientList;
		clientView.setItems(new ServerApplication().clientList);

		ListView<String> userListView = new ListView<String>();
//		userListView.setItems();
//		Button btn1 = new Button("1");
//		Button btn2 = new Button("2");
//
//		VBox layout = new VBox(20);
//		layout.getChildren().addAll(btn1, btn2);


		rootPane.add(userListView, 0, 0,2,1);

		/* Make and return the scene */
		return new Scene(rootPane, 400, 500);

	}
}
