package xyz.itshark.play.rpc.grpc.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.grpc.stub.StreamObserver;
import xyz.itshark.play.rpc.grpc.bank.Account;
import xyz.itshark.play.rpc.grpc.bank.AccountNotification;
import xyz.itshark.play.rpc.grpc.bank.BankGrpc;
import xyz.itshark.play.rpc.grpc.bank.NotificationType;
import xyz.itshark.play.rpc.grpc.bank.RequestMoney;
import xyz.itshark.play.rpc.grpc.bank.RequestStatus;
import xyz.itshark.play.rpc.grpc.bank.TransactionStatus;
import xyz.itshark.play.rpc.grpc.bank.TransferConfirmation;
import xyz.itshark.play.rpc.grpc.bank.TransferMoney;

// our service implementation extends auto-generated xyz.itshark.play.rpc.grpc.bank.BankGrpc.BankImplBase 
public class BankServiceGrpc extends BankGrpc.BankImplBase {

	// add default amounts to accounts
	private double[] accounts = { 0, 100, 40 };
	
	// Queue to be used for Notifications that need to be sent to corresponding notification client
	Map<Integer, Queue<AccountNotification>> map = new HashMap<Integer, Queue<AccountNotification>>();

	// default constructor just to initialize some values
	public BankServiceGrpc() {
		super();
		map.put(0, new ConcurrentLinkedQueue<AccountNotification>());
		map.put(1, new ConcurrentLinkedQueue<AccountNotification>());
		map.put(2, new ConcurrentLinkedQueue<AccountNotification>());
	}

	// This service is used to Transfer money from one account to other
	// for example paying a bill
	@Override
	public void transfer(TransferMoney request, StreamObserver<TransferConfirmation> responseObserver) {
		TransferConfirmation tc;

		// simulate "Bank process"
		if (transferMoney(request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmmount())) {
			tc = TransferConfirmation.newBuilder()
					.setMessage("money trasnfered")
					.setStatus(TransactionStatus.SUCCESS)
					.build();
		} else {
			tc = TransferConfirmation.newBuilder()
					.setMessage("not enough founds")
					.setStatus(TransactionStatus.FAILED)
					.build();
		}

		responseObserver.onNext(tc);
		responseObserver.onCompleted();
	}

	
	private AccountNotification accountNotify(int acc, double ammount, NotificationType type) {
		return AccountNotification.newBuilder()
				.setAccountNumber(acc)
				.setAmmount(ammount)
				.setNtype(type)
				.setBalance(accounts[acc])
				.build();	
	}

	private boolean transferMoney(int fromAccount, int toAccount, double ammount) {
		if (accounts[fromAccount] < ammount) {
			map.get(fromAccount).add(accountNotify(fromAccount,ammount,NotificationType.TRANSFER_REJECTED_NO_FUND));
			return false;
		} else {
			accounts[fromAccount] -= ammount;
			accounts[toAccount] += ammount;

			map.get(fromAccount).add(accountNotify(fromAccount,ammount,NotificationType.TRANSFER_COMPLETED));
			map.get(toAccount).add(accountNotify(toAccount,ammount,NotificationType.TRANSFER_RECIEVED));

			return true;
		}
	}

}
