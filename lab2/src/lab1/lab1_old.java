//Maurycy Muzyka 285730
//WdPRiR 2023Z

//Mandelbrot rownolegly
//analiza = 1 <=> rozne rozmiary bloku;
//analiza = 2 <=>  rozne rozdzielczosci px

package lab1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class lab1 {
	public static double liczZbieznosc(Complex z0, int max) {
	    Complex z = z0;
	    for (int t = 0; t < max; t++) {
	        double fAbs = z.abs();
	        if (fAbs > 2.0)
	        {
	             // based on the final value, add a fractional amount based on
	             // how much it escaped by (fAbs will be in the range of 2 to around 4):
	             return (float) (t + (2.0 - (Math.log(fAbs) / Math.log(2.0))));
	        }
	        z = z.times(z).plus(z0);
	    }
	    return max;
	}
	
	public static double[][] liczBlok(double x_start3, int x_n3, int y_n3, double y_end3, double dx3, double dy3) {
		double[][] wyn = new double[x_n3][y_n3];
		for(int j = 0; j < y_n3; j++) {
			for(int i = 0; i < x_n3; i++) {
				Complex z = new Complex(x_start3 + dx3*(i + 0.5), y_end3 - dy3*(j + 0.5));
				wyn[i][j] = liczZbieznosc(z, 100);
				//wyn[i][j] = z.re(); //debug czy dobrze wycinkuje obszar
			}
		}
		return wyn;
	}
	
//	public static String liczBlok2(double x_start3, int x_n3, int y_n3, double y_end3, double dx3, double dy3, int i3, double[][] wyn) {
//		for(int j = 0; j < y_n3; j++) {
//			for(int i = 0; i < x_n3; i++) {
//				Complex z = new Complex(x_start3 + dx3*(i + 0.5), y_end3 - dy3*(j + 0.5));
//				wyn[i + i3][j] = liczZbieznosc(z, 100);
//			}
//		}
//		return "";
//	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		double x_start = -2.;
		double x_end = -x_start;
		double y_start = x_start;
		double y_end = x_end;

		int analiza = 2; //1 - rozne rozmiary bloku; 2 - rozne px
		
		int px[] = {1000}; 
		int rozmBloku[] = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000}; //rozmiary blokow
		if(analiza == 2) {
			int ppx[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000}; //rozdzielczosci
			px = ppx;
			int rrozmBloku[] = {1};
			rozmBloku = rrozmBloku;
		}
		int[] nBlokow = new int[Math.max(rozmBloku.length, px.length)]; //liczba blokow
		for(int i = 0; i < nBlokow.length; i++) {
			if(analiza == 1)
				nBlokow[i] = (int)(px[0] / rozmBloku[i]);
			else
				nBlokow[i] = (int)(px[i] / rozmBloku[0]);
		}	

		
		int zapis = 0; //1 - zapisz csv; UWAGA! Wczesniej ustaw 1-elementowy rozmiar bloku i 1-elementowa rozdzielczosc px
		int printuj = 1; //1 - jesli zapisujesz to wyswietl dodatkowo "obraz" w terminalu
		
		int n_usr = 10; 
		for(int k = 0; k < px.length; k++) { //rozna rozdzielczosc obrazka
			double t_sr = 0;
			int x_n = px[k];
			int y_n = x_n;
			double dx = (x_end-x_start)/x_n;
			double dy = (y_end-y_start)/y_n;
			double[][] wyniki = new double[x_n][y_n];
			
			for(int m = 0; m < rozmBloku.length; m++) { //rozne rozmiary blokow
				int x_n2 = (int)(x_n / nBlokow[m]);
				int y_n2 = y_n; 
				
				for(int l = 0; l < n_usr; l++) { //usrednianie realizacji calego procesu
					LinkedList<Future<double[][]>> results = new LinkedList<>();
					ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
					long start = System.nanoTime();
					
					for(int o = 0; o < nBlokow[m]; o++) { //kolejne bloki jako joby
						int p = o;
						double x_start2 = (double)(x_start + p * dx * rozmBloku[m]);
						double y_end2 = y_end;
						results.add(ex.submit(() -> liczBlok(x_start2, x_n2, y_n2, y_end2, dx, dy))); //liczBlok()
					}
					
					for(int o = 0; o < nBlokow[m]; o++) { //kolejne odczyty jobow
						int p = o;
						int i2 = (int)(p * rozmBloku[m]); //przesuniecie x-owe do tablicy wynikow[][]
						try {
							for(int j = 0; j < y_n2; j++) {
								for(int i = 0; i < x_n2; i++) {
									wyniki[i + i2][j] = (double)(results.get(p).get()[i][j]);  //czeka aż wynik sie pojawi
								}
							}
						} catch (InterruptedException e) {} catch (ExecutionException e) {}
					}
					ex.shutdown();
					ex.awaitTermination(1, TimeUnit.DAYS);
					
					long stop = System.nanoTime();
					t_sr += (double)(stop - start)/1e9;
				}
				if(analiza == 1) {
					System.out.println("Rozmiar bloku: " + rozmBloku[m]);
					System.out.println("Time: " + String.format("%.10f", t_sr/n_usr));
				}
				
				if(zapis == 1) {
					String format = "%.2f";
					OutputStream os = null;
			        try {
			            os = new FileOutputStream(new File("daneMB4csv"));
			            //String str = String.format(format, dx) + " " + String.format("%.2f", dy) + "\n";
			            //os.write(str.getBytes(), 0, str.length());
			            for(int j = 0; j < y_n; j++) {
							for(int i = 0; i < x_n; i++) {
								String str = String.format(format, wyniki[i][j]) + " ";
								if(printuj == 1) {
									System.out.print(str);
								}
								os.write(str.getBytes(), 0, str.length());
							}
							if(printuj == 1) {
								System.out.println("");
							}
							String str = "\n";
							os.write(str.getBytes(), 0, str.length());
						}
			        } catch (IOException e) {}
			        finally{
			            try {
			                os.close();
			            } catch (IOException e) {}
			        }
				}
			}
			if(analiza == 2) {
				System.out.println("Pixeli: " + x_n*y_n);
				System.out.println("Time: " + String.format("%.10f", t_sr/n_usr));
			}
		}
		
	}
}
