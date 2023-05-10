package com.example.chat.server;

import java.io.IOException;
import java.util.ArrayList;

import com.example.chat.client.Client;
import com.example.chat.client.ClientApplication;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ServerApplication extends Application {
	public static ArrayList<Thread> threads;
	public static void main(String[] args){
		launch();
	}

	//추가
	public String n;
	public int idx;
	public String muteNN;
	public ObservableList<String> obList = FXCollections.observableArrayList(); // mute리스트

	public int portNumber;
	public ObservableList<String> clientList = FXCollections.observableArrayList();
	//

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		threads = new ArrayList<Thread>();
		primaryStage.setTitle("JavaFX Chat Server");
		primaryStage.setScene(makePortUI(primaryStage));
		primaryStage.show();

		//추가
		obList.add("시작");
		obList.addListener(new Server.MyListener());
		//
	}

	
	public Scene makePortUI(Stage primaryStage) {
		/* Make the root and set properties */
		GridPane rootPane = new GridPane();
		rootPane.setPadding(new Insets(20));
		rootPane.setVgap(10);
		rootPane.setHgap(0);
		rootPane.setAlignment(Pos.CENTER);

		/* Text label and field for port Number */
		Text portText = new Text("Port Number");
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		TextField portTextField = new TextField();
		portText.setFont(Font.font("Tahoma"));
		/*
		 * "Done" button and its click handler When clicked, another method is
		 * called
		 */
		Button portApprovalButton = new Button("Done");
		portApprovalButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				/* Make the server and it's thread, and run it */
				try {
					portNumber = Integer.parseInt(portTextField.getText());
					Server server = new Server(Integer.parseInt(portTextField
							.getText()));
					Thread serverThread = (new Thread(server));
					serverThread.setName("Server Thread");
					serverThread.setDaemon(true);
					serverThread.start();
					threads.add(serverThread);
					/* Change the view of the primary stage */
					primaryStage.hide();
					primaryStage.setScene(makeServerUI(server));
					primaryStage.show();
				}catch(IllegalArgumentException e){
					errorLabel.setText("Invalid port number");
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
				
			}
		});
		
		/* Add the views to the pane */
		rootPane.add(portText, 0, 0);
		rootPane.add(portTextField, 0, 1);
		rootPane.add(portApprovalButton, 0, 2);
		rootPane.add(errorLabel, 0, 3);
		/*
		 * Make the Scene and return it Scene has constructor (Parent, Width,
		 * Height)
		 */
		return new Scene(rootPane, 400, 300);
	}
	public Scene makeServerUI(Server server){
		/* Make the root pane and set properties */
		GridPane rootPane = new GridPane();
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setPadding(new Insets(20));
		rootPane.setHgap(10);
		rootPane.setVgap(10);
		
		/* Make the server log ListView */
		Label logLabel = new Label("Server Log");
		ListView<String> logView = new ListView<String>();
		ObservableList<String> logList = server.serverLog;
		logView.setItems(logList);
		
		/* Make the client list ListView */
		Label clientLabel = new Label("Clients Connected");
		ListView<String> clientView = new ListView<String>();
		clientList = server.clientNames;

//		ObservableList<String> clientList = server.clientNames;
		clientView.setItems(clientList);


		// 추가
		class ListViewHandler implements EventHandler<MouseEvent> {
			@Override
			public void handle(MouseEvent event) {}
		}

		clientView.setOnMouseClicked(new ListViewHandler(){
			@Override
			public void handle(javafx.scene.input.MouseEvent event) {
				n = clientView.getSelectionModel().getSelectedItem().split(" ")[0];
				idx = clientView.getSelectionModel().getSelectedIndex();
			}
		});

		Button kick = new Button("kick");
		kick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) { // 버튼 클릭 이벤트
				System.out.println("kick " + idx);
				server.clientDisconnected(server.clientThreads.get(idx), false);
				obList.add(n);
			}
		});

		Button mute = new Button("mute");
		mute.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("mute: " + n);
				obList.add(n);
				obList.addListener(new Server.MyListener());
				// 뮤트 버튼을 클릭했을 때 해당 닉네임가진 사람 채팅 X
			}
		});

		//여기까지 추가


		/* Add the view to the pane */
		rootPane.add(logLabel, 0, 0);
		rootPane.add(logView, 0, 1);
		rootPane.add(clientLabel, 0, 2);
		rootPane.add(clientView, 0, 3);

		//추가
		rootPane.add(kick, 0, 4);
		rootPane.add(mute, 1, 4);
		//

		/* Make scene and return it,
		 * Scene has constructor (Parent, Width, Height)
		 *  */
		return new Scene(rootPane, 400, 600);
	}
}
