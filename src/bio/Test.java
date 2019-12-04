package bio;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Test {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			// Simulate a long-running Job   
			try {
			TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
			throw new IllegalStateException(e);
			}
			System.out.println("I'll run in a separate thread than the main thread.");
			return "anwser";
			});
			System.out.println(future.get());
			

	}

}
