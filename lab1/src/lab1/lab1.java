//Maurycy Muzyka 285730
//WdPRiR 2023Z

//Mandelbrot nierownolegly:
//zb Mandelbrota <=> c dla ktorych
//z_n+1 = z_n ^ 2 + c
//z_0 = 0
//jest zbiezne |z| < 2 po 200 krokach

//ustalenie fragmentu R^2 jaki chcemy przeliczyć
//ustalenie wymiarów siatki

//rozna szybkosc rozbieznosci - kolory

//czas generacji (pixeli) = f(t)

package lab1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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


	public static void main(String[] args) {
		
		double x_start = -2.;
		double x_end = -x_start;
		double y_start = x_start;
		double y_end = x_end;

		int px[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000};
		//int px[] = {20};
		int zapis = 0; //zapisz csv
		int printuj = 1; //jesli zapisujesz to wyswietl dodatkowo "obraz" w terminalu
		
		int n_usr = 10;
		for(int k = 0; k < px.length; k++) {
			double t_sr = 0;
			int x_n = px[k];
			int y_n = x_n;
			double dx = (x_end-x_start)/x_n;
			double dy = (y_end-y_start)/y_n;
			//System.out.println(dx+" "+dy);
			double[][] wyniki = new double[x_n][y_n];
			for(int l = 0; l < n_usr; l++) {				
				long start = System.nanoTime();
				
				for(int j = 0; j < y_n; j++) {
					for(int i = 0; i < x_n; i++) {
						Complex z = new Complex(x_start + dx*(i + 0.5), y_end - dy*(j + 0.5));
						wyniki[i][j] = liczZbieznosc(z, 100);
					}
				}
				
				long stop = System.nanoTime();
				t_sr += (double)(stop - start)/1e9;

				if(zapis == 1) {
					String format = "%.2f";
					OutputStream os = null;
			        try {
			            os = new FileOutputStream(new File("daneMB3.csv"));
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
			        } catch (IOException e) {
			            e.printStackTrace();
			        }finally{
			            try {
			                os.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
				}
			}
			System.out.println("Pixeli: " + x_n*y_n);
			System.out.println("Time: " + String.format("%.10f", t_sr/n_usr));
		}
		
	}
}