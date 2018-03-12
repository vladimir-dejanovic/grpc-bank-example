package xyz.itshark.play.rpc.grpc.bank.client;


import java.util.Iterator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import xyz.itshark.play.rpc.grpc.bank.Account;
import xyz.itshark.play.rpc.grpc.bank.AccountNotification;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc.BankStub;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import io.grpc.stub.StreamObserver;


public class NotificationsJavaFXClient extends Application {

	private TextField message = new TextField();
	private Button btn = new Button();
	private ObservableList<String> messages = FXCollections.observableArrayList();
	private ListView<String> messagesView = new ListView<>();

	public static void main(String args[]) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        		
		primaryStage.setTitle("Notifications for account");
        		
	    BorderPane pane = new BorderPane();
	    pane.setLeft(new Label("Account #"));
	    pane.setCenter(message);
	    pane.setRight(btn);
		
	    BorderPane root = new BorderPane();
	    root.setTop(pane);
	    root.setCenter(messagesView);
		
	    messagesView.setItems(messages);
	    
		
		btn.setText("Show notifications");
	    primaryStage.setScene(new Scene(root, 400, 300));

		// create channel to connect to gRPC service
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				                                      .usePlaintext(true)
				                                      .build();

		// put hook when window is closed to stop connection to server
		primaryStage.setOnCloseRequest(e -> { channel.shutdown(); });

	    primaryStage.show();

		BankStub client = BankGrpc.newStub(channel);
	    
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
            	            	
            	message.setDisable(true);
            	btn.setDisable(true);
            	primaryStage.setTitle("Notifications for account " + message.getText());
            	
        		
        		// take input argument to decide for which account I want to listen to notifications
        		Integer accountNumber = Integer.valueOf(message.getText()).intValue();
        		
		
        		client.listenToNotifications(Account.newBuilder()
                                                    .setAccountNumber(accountNumber)
                                                    .build(), 
                                  new StreamObserver<AccountNotification>() {

							          @Override
							          public void onNext(AccountNotification value) {
							        	  Platform.runLater(() -> {
							        		  messages.add("Notification:\n " + value.toString());
							        		  messagesView.scrollTo(messages.size());
							        	  });

							          }

							          @Override
							          public void onError(Throwable t) {
							        	  // TODO Auto-generated method stub
							          }

							          @Override
							          public void onCompleted() {
							        	  // TODO Auto-generated method stub

							          }
								});
        		
            }
        });   
	}

}
