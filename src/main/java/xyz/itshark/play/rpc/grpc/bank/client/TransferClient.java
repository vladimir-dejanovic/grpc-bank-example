package xyz.itshark.play.rpc.grpc.bank.client;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc;
import xyz.itshark.play.rpc.grpc.bank.TransferMoney;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc.BankBlockingStub;
import xyz.itshark.play.rpc.grpc.bank.TransferConfirmation;

public class TransferClient {
	
	public static void main(String args[]) throws InterruptedException {
		
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext(true)
				.build();
		
		// Blocking client
		BankBlockingStub client = BankGrpc.newBlockingStub(channel);
		
		System.out.println(">>>>>>>>> Send Transfer Request 1");
		TransferConfirmation response = client.transfer(TransferMoney.newBuilder()
				.setTransId(1)
				.setFromAccountNumber(1)
				.setToAccountNumber(2)
				.setAmmount(60)
				.build());
		
		System.out.println(response);

	
		System.out.println(">>>>>>>>> Send Transfer Request 2");
		response = client.transfer(TransferMoney.newBuilder()
				.setTransId(1)
				.setFromAccountNumber(1)
				.setToAccountNumber(2)
				.setAmmount(60)
				.build());
		
		System.out.println(response);
		
	    channel.shutdown()
	    	   .awaitTermination(5, TimeUnit.SECONDS);
	}

}
