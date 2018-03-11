package xyz.itshark.play.rpc.grpc.bank;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class BankServer {

	public static void main(String args[]) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(9090)
				.addService(new BankServiceGrpc())
				.build();

		System.out.println("Bank will start soon!!!");
		server.start();

		System.out.println("Bank is up and running!!!");
		server.awaitTermination();
	}

}
