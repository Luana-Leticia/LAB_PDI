package metrics;

import java.util.ArrayList;
import java.util.Collections;

import ij.plugin.filter.Convolver;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import metrics.Corner;

/**
 *  This class implements an Harris Corner Detector for 8-bits gray scale images.
 *  
 *  Class edited from: Digital Images Processing - Wilhelm Burger.
 *   
 *   By: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *    
 */

public class Corners_Counter {

	private int h;
	private int w;
	float alpha = 0.05f; 
	int threshold = 400; 
	double dmin = 35; 
	final int border = 20; 

	// filters 1D separable
	final float[] pfilt = { 0.223755f, 0.552490f, 0.223755f }; // pre filter
	final float[] dfilt = {0.453014f,0.0f,-0.453014f}; // derivative filter
	final float[] bfilt = { 0.01563f, 0.09375f, 0.234375f, 0.3125f, 0.234375f, 0.09375f, 0.01563f }; // blur filter
	// = [1, 6, 15, 20, 15, 6, 1]/64
	ImageProcessor ipOrig;
	FloatProcessor A;
	FloatProcessor B;
	FloatProcessor C;
	FloatProcessor Q;
	ArrayList<Corner> corners = new ArrayList<Corner>();

	public Corners_Counter(ImageProcessor ip) {
		// TODO Auto-generated constructor stub
		this.ipOrig = ip;
		this.h = ipOrig.getHeight(); 
		this.w = ipOrig.getWidth();
	}

	public Corners_Counter(ImageProcessor ip, float alpha, int threshold) {
		// TODO Auto-generated constructor stub
		this.ipOrig = ip;
		this.alpha = alpha;
		this.threshold = threshold;
		this.h = ipOrig.getHeight();
		this.w = ipOrig.getWidth();
	}
	
	// Method returns a number of corners in a given image
	public int countering() {
		
		// Get copies of original image
		FloatProcessor xIp = ipOrig.duplicate().convertToFloatProcessor();
		FloatProcessor yIp = ipOrig.duplicate().convertToFloatProcessor();
		
		// Initialize convolver, pre filter and get derivatives in x and in y of the image
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		
		conv.convolve(xIp, pfilt, 1, pfilt.length); // convolve horizontally ix with the pre filter
		conv.convolve(xIp, dfilt, 1, dfilt.length); // convolve horizontally ix with the difference filter
		
		conv.convolve(yIp, pfilt, pfilt.length, 1); // convolve vertically iy with the pre filter
		conv.convolve(yIp, dfilt, dfilt.length, 1); // convolve vertically iy  with the difference filter
		
		// Get pixels of derivatives images
		float[] ix = (float[]) xIp.getPixels();
		float[] iy = (float[]) yIp.getPixels();

		// Get structural matrix
		float[] multIxIy = mult(ix, iy);
		float[] sqIx = mult(ix, ix);
		float[] sqIy = mult(iy, iy);
		
		FloatProcessor A = ipOrig.duplicate().convertToFloatProcessor();
		FloatProcessor B = ipOrig.duplicate().convertToFloatProcessor();
		FloatProcessor C = ipOrig.duplicate().convertToFloatProcessor();
		
		A.setPixels(sqIx);
		B.setPixels(sqIy);
		C.setPixels(multIxIy);

		// Gaussian convolution
		conv.convolve(A, bfilt, bfilt.length, 1); // convolve vertically with the blur filter
		conv.convolve(A, bfilt, 1, bfilt.length); // convolve horizontally with the blur filter

		conv.convolve(B, bfilt, bfilt.length, 1);
		conv.convolve(B, bfilt, 1, bfilt.length);

		conv.convolve(C, bfilt, bfilt.length, 1);
		conv.convolve(C, bfilt, 1, bfilt.length);

		// Corner response function - CRF
		Q = new FloatProcessor(w, h);
		float[] aPixels = (float[]) A.getPixels();
		float[] bPixels = (float[]) B.getPixels();
		float[] cPixels = (float[]) C.getPixels();
		float[] qPixels = (float[]) Q.getPixels();

		for (int index = 0; index < w * h; index++) {
			float a = aPixels[index];
			float b = bPixels[index];
			float c = cPixels[index];
			qPixels[index] = (a * b - c * c) - alpha * (a + b) * (a + b); // determinant(M) - alpha*trace*trace, where M
																			// = ((a c) (c b))
		}

		Q.setPixels(qPixels);

		// Get good candidates to corners points
		for (int v = border; v < h - border; v++) {
			for (int u = border; u < w - border; u++) {
				float q = qPixels[v * w + u];
				// Verify if the pixel is greater then the threshold and if it is the greatest
				// in 8-neighborhood
				if (q > threshold && isLocalMax(Q, u, v)) {
					Corner c = new Corner(u, v, q);
					corners.add(c);
				}
			}
		}
		
		// Sort corners in ascending form of value q
		Collections.sort(corners);

		// Remove weak corners
		int candidates = corners.size();
		Corner[] cornersArray = new Corner[candidates]; 
		cornersArray = corners.toArray(cornersArray);

		ArrayList<Corner> goodCorners = new ArrayList<Corner>();
		double sqDmin = dmin * dmin;
		
		// Iterate to cancel corners so close 
		for (int i = 0; i < candidates; i++) {
			if (cornersArray[i] != null) {
				Corner corner1 = cornersArray[i];
				goodCorners.add(corner1);
				// Iterate to delete all remaining corners close to corner 1
				for (int j = i + 1; j < candidates; j++) {
					if (cornersArray[j] != null) {
						Corner corner2 = cornersArray[j];
						if (corner1.dist2(corner2) < sqDmin) {
							cornersArray[j] = null; // Delete corner2 if it too close of corner 1
						}
					}
				}
			}
		}
		
		// Make corners global to class
		this.corners = goodCorners;
		
		// Count corners
		int corners = goodCorners.size();
		return corners;

	}
	
	// Method multiplies two given images
	private float[] mult(float[] aPixels, float[] bPixels) {
		float[] newPixels = new float[w * h];

		for (int index = 0; index < w * h; index++) {
			newPixels[index] = (float) (aPixels[index] * bPixels[index]);
		}
		return newPixels;
	}

	// Method verifies if the given pixel is the max value in the neighborhood
	private boolean isLocalMax(FloatProcessor fp, int u, int v) {
		if (u <= 0 || u >= w - 1 || v <= 0 || v >= h - 1) // if pixel is outside of the image or pixel is in the border
															// of the image
			return false;
		else {
			float[] pixels = (float[]) fp.getPixels(); // Get pixels of FloatProcessor that origin the pixel in (u,v)
			int i0 = (v - 1) * w + u; // index of superior pixel
			int i1 = v * w + u; // index of given pixel
			int i2 = (v + 1) * w + u; // index of inferior pixel
			float cp = pixels[i1];
			
			// Condition verifies if the center pixel is higher then all neighborhood
			return cp > pixels[i0 - 1] && cp > pixels[i0] && cp > pixels[i0 + 1] && cp > pixels[i1 - 1]
					&& cp > pixels[i1 + 1] && cp > pixels[i2 - 1] && cp > pixels[i2] && cp > pixels[i2 + 1];
		}
	}
	
	// Method draw the corners found in class of a given image
	 public ImageProcessor showCornerPoints(ImageProcessor ip) { 
		ByteProcessor ipResult = (ByteProcessor) ip.duplicate();  
		 // draw corners 
		 for (Corner c: corners) { 
			 c.draw(ipResult);
		} 
		 return ipResult;
	} 
	

}
