package filters_performance_lab;

import filtering.Gaussian_Blur_Filter;
import filtering.IBSF_Filter;
import filtering.Median_Filter;
import filtering.Moving_Average_Filter;
import filtering.Wiener_Filter;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import metrics.Corners_Counter;
import metrics.RMSE_SSIM_SNR_CorrelationCoefficient_calculator;

/**
 *  This class implements a routine that can analyze 5 filters: 
 *  Moving Average, Median, Gaussian Blur, Wiener and IBSF.
 *  The filters is chosen by index and windows dimensions provided. 
 *  
 *   @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class Filters_Performance {	
	
	// Directories paths
	private String filteredsPath = "C:/Users/luana/Downloads/LAB03/imagens-filtradas/"; // filtered images folder directory
	private String targetImagePath = "C:/Users/luana/Downloads/LAB03/lab03_imagens/Ref.tif"; // reference image directory
	private String noisyImagesPath = "C:/Users/luana/Downloads/LAB03/lab03_imagens/Noise_"; // noisy images directory
	private String tabsPath = "C:/Users/luana/Downloads/LAB03/tabelas/"; // tables folder directory
	
	// Filter 
	private String filterName = " ";

	
	public Filters_Performance() {
		// TODO Auto-generated constructor stub
	}
	
	// Method analyze some filter with MxN window
	public void filter_performance(int filterIndex, int m, int n) {

		// Open reference image and store in a vector (integers values)
		Opener op = new Opener(); // ImageJ class that open files
 		ImagePlus target = op.openImage(targetImagePath); 
		byte[] targetTemp = (byte[]) target.getProcessor().getPixels();
		int[] targetPixels = convertByteToInt(targetTemp); 
		
		// Create dummy image	
		int w = target.getWidth();
		int h = target.getHeight();
		ImagePlus dummy = NewImage.createByteImage("dummy", w, h, 1, NewImage.FILL_WHITE);
				
		// Initialize tables and set precisions and define columns titles
		ResultsTable tab1 = new ResultsTable(); // tab1 - compare original images with filtered images
		ResultsTable tab2 = new ResultsTable(); // tab2 - compare filtered images with reference image

		tab1.setPrecision(3); 
		tab2.setPrecision(3);

		String[] tabHeadings = { "RMSE", "SSIM", "r", "SNR", "Corners" }; // tables column titles
		
		// Iterate to get 10 filtered images and save them, and get the 2 tables of metrics of the filter
		int inputImages = 10;

		for (int i = 0; i < inputImages; i++) {
			
			// Open noisy image of the iteration and get pixels (integers values)
			ImagePlus noise = op.openImage(noisyImagesPath + i + ".tif"); 
			byte[] noiseTemp = (byte[]) noise.getProcessor().getPixels();
			int[] noisePixels = convertByteToInt(noiseTemp); 
			
			// Filter image, save, get image processor and store pixels in a vector (integers values)
			dummy = filtering(filterIndex, m, n, noise);
			IJ.save(dummy, filteredsPath + filterName + "-of-Noise-" + i); 
			ImageProcessor filteredIp = dummy.getProcessor(); 
			byte[] filteredTemp = (byte[]) filteredIp.getPixels();
			int[] filteredPixels = convertByteToInt(filteredTemp);
			
			// Initialize class that calculates metrics and returns this metrics in a vector, given two images
			RMSE_SSIM_SNR_CorrelationCoefficient_calculator mc = new RMSE_SSIM_SNR_CorrelationCoefficient_calculator();
			double[] metrics1 = mc.calculateRMSE_SSIM_SNR_CorrelationCoefficient(noisePixels, filteredPixels); // store some metrics of table 1
			double[] metrics2 = mc.calculateRMSE_SSIM_SNR_CorrelationCoefficient(targetPixels, filteredPixels); // store some metrics of table 2
			
			// Initialize class that calculates corners of a given image
			Corners_Counter cc = new Corners_Counter(filteredIp);
			int corners = cc.countering();
			
			int metrics = 4; 

			for (int j = 0; j < metrics; j++) {
				// Add metrics in the line i of the tables
				tab1.setValue(tabHeadings[j], i, metrics1[j]); 
				tab2.setValue(tabHeadings[j], i, metrics2[j]);
			}
			
			// Add corners in the line i of the tables
			tab1.setValue(tabHeadings[4], i, corners); 
			tab2.setValue(tabHeadings[4], i, corners);
			
			// Give a label to the line i in table (correspond to the noisy image of the current iteration)
			tab1.setLabel("Noise_" + i, i); 
			tab2.setLabel("Noise_" + i, i);
			
		}
		
		// Iterate to get mean and standard deviation at each column in the tables
		int metrics = 5; 
		
		for (int u = 0; u < metrics; u++) {
			// Store columns (u-index) of the tables in vectors
			double[] col1 = tab1.getColumnAsDoubles(u); 
			double[] col2 = tab2.getColumnAsDoubles(u);

			double sqSum1 = 0;
			double sqSum2 = 0;
			double sum1 = 0;
			double sum2 = 0;
			
			// Iterate in column to get mean and standard deviation of the column (u-index)
			int elementsInColumn = 10;

			for (int v = 0; v < elementsInColumn; v++) {
				// Calculus to get mean and stdDev in column
				sqSum1 += Math.pow(col1[v], 2); 
				sqSum2 += Math.pow(col2[v], 2);

				sum1 += col1[v];
				sum2 += col2[v];
			}
			
			int N = 10; // number of data
			
			double mean1 = sum1 / N;
			double mean2 = sum2 / N;

			double stdDev1 = Math.sqrt((sqSum1 - N * mean1 * mean1)/(N-1));
			double stdDev2 = Math.sqrt((sqSum2 - N * mean2 * mean2)/(N-1));

			// Add mean and standard deviation calculated in the end of the column (u-index)
			tab1.setValue(tabHeadings[u], 10, mean1); 
			tab2.setValue(tabHeadings[u], 10, mean2);

			tab1.setValue(tabHeadings[u], 11, stdDev1);
			tab2.setValue(tabHeadings[u], 11, stdDev2);

		}
		
		// Add name in lines that has means and standard deviation
		tab1.setLabel("Média", 10); 
		tab2.setLabel("Média", 10);
		
		tab1.setLabel("Desvio Padrão", 11);		
		tab2.setLabel("Desvio Padrão", 11);
		
		// Show tables
		tab1.show("Metrics of " + filterName + " with original image"); 
		tab2.show("Metrics of " + filterName + " with target image");
		
		// Save tables in CSV format
		tab1.save(tabsPath + "Metrics-of-original-with-" + filterName + ".csv"); 
		tab2.save(tabsPath + "Metrics-of-target-with-" + filterName + ".csv");

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
	
	// Method call filter by index, add the filter name in class attributes and return the filtered image
	 private ImagePlus filtering (int filterIndex, int m, int n, ImagePlus noise) {
		if (filterIndex == 1) {
			this.filterName = "Moving-Average-" + m + "x" + n;
			Moving_Average_Filter filter = new Moving_Average_Filter();
			ImagePlus filtered = filter.filtering(noise, m, n);
			return filtered;
		}
		
		if (filterIndex == 2) {
			this.filterName = "Median-" + m + "x" + n;;
			Median_Filter filter = new Median_Filter();
			ImagePlus filtered = filter.filtering(noise, m, n);
			return filtered;
		}
		
		if (filterIndex == 3) {
			this.filterName = "Gaussian-" + m + "x" + n;;
			Gaussian_Blur_Filter filter = new Gaussian_Blur_Filter();
			ImagePlus filtered = filter.filtering(noise, m, n);
			return filtered;
		}		
		
		if (filterIndex == 4) {
			this.filterName = "Wiener-" + m + "x" + n;;
			Wiener_Filter filter = new Wiener_Filter();
			ImagePlus filtered = filter.filtering(noise, m, n);
			return filtered;
		}		
		
		if (filterIndex == 5) {
			this.filterName = "IBSF-" + m + "x" + n;;
			IBSF_Filter filter = new IBSF_Filter();
			ImagePlus filtered = filter.filtering(noise, m, n);
			return filtered;
		}
		
		return null; // if invalid filter is given
	}


}