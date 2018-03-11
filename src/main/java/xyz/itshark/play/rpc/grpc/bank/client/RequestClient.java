package xyz.itshark.play.rpc.grpc.bank.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc.BankStub;
import xyz.itshark.play.rpc.grpc.bank.RequestMoney;
import xyz.itshark.play.rpc.grpc.bank.RequestStatus;

public class RequestClient {
	
	
	private static Queue<RequestMoney> queue = new ConcurrentLinkedQueue<RequestMoney>();
	
	public static void main(String[] args) throws InterruptedException {
		final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				                                            .usePlaintext(true)
				                                            .build();
		// Non blocking client
		BankStub client = BankGrpc.newStub(channel);

		// simulate request coming in queue
		queue.add(RequestMoney.newBuilder()
				              .setAmmount(10)	
				              .setFromAccountNumber(1)
				              .setToAccountNumber(2)
				              .setMessage("Can you please approve your electricty bill")
				              .setTransId(4)
				              .setMonthly(false)
				              .build());
		
		StreamObserver<RequestMoney> clientObserver = client.requestTransfer( new StreamObserver<RequestStatus>() {

			@Override
			public void onNext(RequestStatus value) {
				System.out.println("message recieved >>>>>>>>");
				System.out.println(value);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println(t.getMessage());
				try {
					channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onCompleted() {
				try {
					channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
		});
		
		// simulate sending multiple incoming requests
		while(!channel.isShutdown() ) {
			if(!queue.isEmpty()) {
				clientObserver.onNext(queue.poll());
			} else {
				Thread.sleep(1000);		
			}			
		}
	}

}
