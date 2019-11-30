package filtering;

import ij.ImagePlus;

/**
 *  This class implements an IBSF filter for 8-bits gray scale images.
 *   
 *   @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class IBSF_Filter {

	public IBSF_Filter() {
		// TODO Auto-generated constructor stub
	}
	
	// Method filter given image with a MxN window dimension and return the filtered
	public ImagePlus filtering (ImagePlus imp, int m, int n) {
		byte[] pixels = (byte[]) imp.getProcessor().getPixels();
		int[] intPixels = convertByteToInt(pixels);
		
		// Median Filtering
		Median_Filter filter = new Median_Filter();
		ImagePlus filtered = filter.filtering(imp, m, n);
		byte [] fPixels = (byte[]) filtered.getProcessor().getPixels();
		int[] intFPixels = convertByteToInt(fPixels);
		
		// Destructive interference suppression
		byte[] filteredPixels = max (intPixels, intFPixels);
		
		// Constructive  interference suppression
		filtered.getProcessor().setPixels(filteredPixels);
		filtered = filter.filtering(filtered, 5, 5);
		
		return filtered; // return filtered image
	}
	
	// Method get the max between two images
	public byte[] max (int[] aPixels, int[] bPixels) {
		int N = aPixels.length;
		byte[] newPixels = new byte[N];
		
		for (int index = 0; index < N; index++) {
			if (aPixels[index] > bPixels[index]) {
				newPixels[index] = (byte) aPixels[index];
			}
			
			if (bPixels[index] > aPixels[index]) {
				newPixels[index] = (byte) bPixels[index];
			}
			
			if (aPixels[index] == bPixels[index]) {
				newPixels[index] = (byte) aPixels[index];
			}
		}
		
		return newPixels;
	}
	
	// Method convert vector of bytes in an vector of integers
	private int[] convertByteToInt (byte[] pixels) {
		int N = pixels.length;
		int[] newPixels = new int[N];
		// Iterate in vector to get integers values
		for (int index = 0; index < N; index++) {
			int temp = 0xff & pixels[index];
			newPixels[index] = temp;
		}
		return newPixels;
	}
}