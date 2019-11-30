package filtering;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 *  This class implements an Moving Average filter for 8-bits gray scale images.
 *   
 *   @authors: Luana Let�cia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class Moving_Average_Filter {

	private int h;
	private int w;

	public Moving_Average_Filter() {
		// TODO Auto-generated constructor stub
	}
	
	// Method filter given image with a MxN window dimension and return the filtered
	public ImagePlus filtering(ImagePlus imp, int m, int n) {
		
		ImageProcessor ip = imp.getProcessor();
		this.h = imp.getHeight();
		this.w = imp.getWidth();

		// Create a empty vector that will receive the new pixels
		byte[] newPixels = new byte[w * h];

		// Conversion for the iteration result in a MxN window
		m = (int) (m - 1) / 2;
		n = (int) (n - 1) / 2;

		// Iterate in points of the image to get windows
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int temp = 0;
				int sum = 0;
				int cont = 0;
				// Iterate in window to get values of the window, sum and count pixels
				for (int l = -n; l <= n; l++) {
					for (int k = -m; k <= m; k++) {
						int u = x + k;
						int v = y + l;
						if (!isOutside(u, v)) {
							temp = ip.get(u, v);
							sum += temp;
							cont++;
						}
					}
				}
				
				// Summed and counted pixels of the window, calculate mean and add in pixel (x,y)
				newPixels[y * w + x] = (byte) ((int) sum / cont);
			}
		}
		
		// Set new pixels in the original image
		ip.setPixels(newPixels);
		return imp; // return filtered image
	}
	
	// Method returns if the coordinate is out of the image or not
	public boolean isOutside(int x, int y) {
		return ((x < 0) || (x >= w) || (y < 0) || (y >= h));
	}

}