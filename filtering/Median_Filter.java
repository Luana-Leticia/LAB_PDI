package filtering;

import java.util.ArrayList;
import java.util.Collections;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 *  This class implements an Median filter for 8-bits gray scale images.
 *   
 *   @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class Median_Filter {

	private int h;
	private int w;

	public Median_Filter() {
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
		m = (int) (m-1)/2;
		n = (int) (n-1)/2;
				
		// Iterate in points of the image to get windows
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int temp = 0;
				// Create dummy list to store the window pixels values
				ArrayList<Integer> dummy = new ArrayList<Integer>();
				
				// Iterate in window to store values of the window 
				for (int l = -n; l <= n; l++) {
					for (int k = -m; k <= m; k++) {
						int u = x + k;
						int v = y + l;
						if (!isOutside(u, v)) {
							temp = ip.get(u, v);
							dummy.add(temp);
						}
					}
				}
				
				// Sort list in ascending form
				Collections.sort(dummy);
				
				// Get the median of window values set
				newPixels[y * w + x] = calculateMedian(dummy);				
			}
		}
		
		// Change the original image with the new pixels
		ip.setPixels(newPixels);
		return imp; // return filtered image
	}
	
	// Method returns if the coordinate is out of the image or not
	public boolean isOutside(int x, int y) {
		return ((x < 0)||(x >= w)||(y < 0)||(y >= h));
	}
	
	// Method calculate median of a given set
	private byte calculateMedian (ArrayList<Integer> set) {
		// Get the numbers of elements in set
		int count = set.size(); 
		byte median = 0;		
		if (count % 2 != 0) { // if count is odd
			median = set.get((count-1)/2).byteValue();
		} else { // if count is even
			median = (byte) (Math.round((set.get((count-2)/2) + set.get(count/2))/2));
		}
		return median;	
	}
}
