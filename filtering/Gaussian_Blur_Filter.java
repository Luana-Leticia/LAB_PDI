package filtering;

import ij.ImagePlus;
import ij.plugin.filter.Convolver;
import ij.process.ImageProcessor;

/**
 *  This class implements a Gaussian Blur filter for 8-bits gray scale images.
 *   
 *   @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class Gaussian_Blur_Filter {

	public Gaussian_Blur_Filter () {
		// TODO Auto-generated constructor stub
	}
	
	// Method filter given image with a MxN window dimension and return the filtered
	public ImagePlus filtering(ImagePlus imp, int m, int n) {

		ImageProcessor ip = imp.getProcessor();
		
		// Generate gaussian kernels horizontal and vertical
		float[] xBlurFilter = makeGaussKernel1d(m);
		float[] yBlurFilter = makeGaussKernel1d(n);	

		// Initialize convolver
		Convolver conv = new Convolver();
		conv.setNormalize(true); 

		// Gaussian convolution
		conv.convolve(ip, xBlurFilter, 1, m); // convolve horizontally with blur kernel
		conv.convolve(ip, yBlurFilter, n, 1); // convolve vertically with blur kernel

		return imp; // return filtered image
	}
	
	// Method creates a 1D gaussian filter kernel of given size
	private float[] makeGaussKernel1d(int size){
		
		// Calculate sigma small enough to avoid truncation effects
		double sigma = (size - 1)/7;
		double sqSigma = sigma * sigma;	
		
		// Create a vector to store the kernel values
		float[] kernel = new float[size]; // odd size
		// Calculate the kernel radius to get a mirrored set
		int rad = (size - 1)/2;
		// Iterate to get the kernel of [- radius; radius]
		for (int i = 0; i < size; i++) {
			double x = rad - i;
			kernel[i] =  (float) Math.exp(- (x * x) / (2 * sqSigma)); // gaussian formula
		}
		return kernel;
	}
}
