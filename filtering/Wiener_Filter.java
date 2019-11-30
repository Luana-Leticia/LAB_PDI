package filtering;

import java.awt.Point;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.process.ImageProcessor;

/**
 *  This class implements an Wiener filter for 8-bits gray scale images.
 *   
 *   @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class Wiener_Filter {

	private int h;
	private int w;

	public Wiener_Filter() {
		// TODO Auto-generated constructor stub
	}
	
	// Method filter given image with a MxN window dimension and return the filtered
	public ImagePlus filtering(ImagePlus imp, int m, int n) {
		
		ImageProcessor ip = imp.getProcessor();
		this.h = imp.getHeight();
		this.w = imp.getWidth();

		// Create a empty vector that will receive the new pixels
		byte[] newPixels = new byte[w * h];

		// Conversion so that the iterations be in MxN windows
		m = (int) (m - 1) / 2;
		n = (int) (n - 1) / 2;		
		
		// Get the coefficient of variation  qH in ROI
		Roi roi = drawROI(imp); // Get ROI of imp
		double qH = calculateROIqH (roi, ip); // Calculate coefficient of variation  qH of ROI

		// Iterate in points of the image to get windows
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
		
				int tempPix = 0; 
				int sum = 0; 
				int sqSum = 0;
				int count = 0; 
				
				// Iterate in window to get sum, squared sum of pixels and numbers of pixels in the window
				for (int l = -n; l <= n; l++) {
					for (int k = -m; k <= m; k++) {						
						int u = x + k;
						int v = y + l;
						if (!isOutside(u, v)) {
							tempPix = ip.get(u, v);
							sum += tempPix;
							sqSum += tempPix * tempPix;
							count++;
						}	
					}
				}
				
				// Get mean, standard deviation, coefficient of variation and alpha with values from the window
				double mean = sum/count;
				double stdDev = Math.sqrt((sqSum - count * mean * mean) / (count-1));
				double q = stdDev/mean;
				double alpha = (double) (1 - Math.pow(qH/q, 2));
				
				// Get current pixel
				int pixel = ip.get(x,y);
				
				// Guarantee that alpha is in the interval [0,1]
				if (alpha > 0 && alpha < 1) {
					// Use the Lee's relation to calculate the new pixel
					newPixels[y*w+x] = (byte) (alpha * pixel + (1 - alpha) * mean);					
				} 
				else {
					// Don't change the pixel
					newPixels[y*w+x] = (byte) pixel;
				}
			}
		}
		
		// Set new pixels in the original image
		ip.setPixels(newPixels);
		return imp; // return filtered image
	}

	// Method returns if the coordinate is out of the image or not
	private boolean isOutside(int x, int y) {
		return ((x < 0) || (x >= w) || (y < 0) || (y >= h));
	}
	
	// Method shows image, ask to draw a ROI in image, get ROI and returns ROI
	private Roi drawROI(ImagePlus imp) {
		imp.show("Select ROI");
		WaitForUserDialog wait = new WaitForUserDialog("Select ROI of homogeneous region noise \n to help the Wiener Filter: ");
		wait.show();
		Roi roi = imp.getRoi();
		return roi;
	}
	
	// Method calculates the coefficient of variation qH of given ROI
	private double calculateROIqH(Roi roi, ImageProcessor ip) {
		// TODO Auto-generated method stub
		int pixel = 0; 
		int sum = 0; 
		int sqSum = 0;
		int count = 0; 
		// Iterate to get sum, squared sum and count to calculate mean and standard deviation
		for (Point p : roi) {
			pixel = ip.get(p.x, p.y);
			sum += pixel;
			sqSum += pixel*pixel;
			count++;
		}
		// Calculus to get qH
		double mean = sum/count;
		double stdDev = Math.sqrt((sqSum - count * mean * mean) / (count-1));
		double qH = stdDev/mean;
		return qH;
	}

}
