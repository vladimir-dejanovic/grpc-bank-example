package xyz.itshark.play.rpc.grpc.bank.client;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import xyz.itshark.play.rpc.grpc.bank.Account;
import xyz.itshark.play.rpc.grpc.bank.AccountNotification;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc.BankBlockingStub;

public class NotificationsConsoleClient {

	public static void main(String[] args) throws InterruptedException {
		int accountNumber = 0;
		
		// create channel to connect to gRPC service
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				                                      .usePlaintext(true)
				                                      .build();
		BankBlockingStub client = BankGrpc.newBlockingStub(channel);
		
		// take input argument to decide for which account I want to listen to notifications
		if(args.length > 0) {
			accountNumber = Integer.valueOf(args[0]).intValue();
		}
		
		// get iterator for notification messages
		Iterator<AccountNotification> iter = client.listenToNotifications(Account.newBuilder()
				                                                                 .setAccountNumber(accountNumber)
				                                                                 .build());
		
		// add try catch block here so client don't crash in case server goes down
		try {
			while (iter.hasNext()) {
				AccountNotification notif = iter.next();
				System.out.println(notif.toString());
			}
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		}

	}

}
