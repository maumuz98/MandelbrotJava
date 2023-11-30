package lab1;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class test {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		LinkedList<Future<double[]>> lista = new LinkedList<>();
		ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		double[] aa = new double[2];
		aa[0] = 1.0;
		aa[1] = 2.1;
		
		lista.add(ex.submit(() -> aa));
		for(var a : lista) {
			try {
				System.out.println(a.get()[0]);
			} catch (InterruptedException e) {} catch (ExecutionException e) {}
		}
		ex.shutdown();
		try {
			ex.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {}
	}

}
