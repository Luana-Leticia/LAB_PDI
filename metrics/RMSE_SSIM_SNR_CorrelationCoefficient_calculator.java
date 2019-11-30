package metrics;

/**
 * This class calculates metrics between two images. 
 * It is useful for evaluating filters performance. 
 * The metrics calculated are: 
 * RMSE, SSIM, Coefficient of Correlation (r) and SNR.
 * 
 * @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 * 
 */

public class RMSE_SSIM_SNR_CorrelationCoefficient_calculator {

	public RMSE_SSIM_SNR_CorrelationCoefficient_calculator() {
		// TODO Auto-generated constructor stub
	}

	// Method calculate the metrics, given two images
	public double[] calculateRMSE_SSIM_SNR_CorrelationCoefficient(int[] aPixels, int[] bPixels) {

		int N = aPixels.length;
		// Create a empty vector for storing metrics
		double[] metrics = new double[4];

		int aSum = 0;
		double aSqSum = 0;

		int bSum = 0;
		double bSqSum = 0;

		double productSum = 0;

		double diffSqSum = 0;

		// Iterate in images to get sum, sum of squared of each image
		// And the difference squared sum and the product of the two images
		for (int index = 0; index < N; index++) {

			double aSq = Math.pow(aPixels[index], 2);
			aSqSum += aSq;
			aSum += aPixels[index];

			double bSq = Math.pow(bPixels[index], 2);
			bSqSum += bSq;
			bSum += bPixels[index];

			double p = aPixels[index] * bPixels[index];
			productSum += p;

			double diffSq = Math.pow((aPixels[index] - bPixels[index]), 2);
			diffSqSum += diffSq;
		}

		// With values above, get mean, standard deviation of each image
		double aMean = aSum / N;
		double aStdDev = Math.sqrt((aSqSum - N * aMean * aMean) / (N - 1));

		double bMean = bSum / N;
		double bStdDev = Math.sqrt((bSqSum - N * bMean * bMean) / (N - 1));
		
		// Get covariance between the two images
		double abCovariance = ((productSum - N * aMean * bMean) / (N - 1));
		
		// Get signal power and noise power between the two images
		double signalPower = aSqSum; // image A considered the signal
		double noisePower = diffSqSum;
		
		// Define constants that avoid instability in metrics 
		double C1 = 6.5025;	// (0,01 * 255)^2
		double C2 = 58.5225; // (0,03 * 255)^2
		double C3 = 29.26125; // C2/2
		
		// Get luminance and contrast between the two images
		double luminance = (2 * aMean * bMean + C1) / (aMean * aMean + bMean * bMean + C1);

		double contrast = (2 * aStdDev * bStdDev + C2) / (aStdDev * aStdDev + bStdDev * bStdDev + C2);
		
		// Get metrics
		double corrCoeff = (abCovariance + C3) / (aStdDev * bStdDev + C3); 

		double ssim = corrCoeff * luminance * contrast;

		double rmse = Math.sqrt(diffSqSum / aSqSum);

		double snr = 10 * Math.log10(signalPower / noisePower);

		// Store metrics
		metrics[0] = rmse;
		metrics[1] = ssim;
		metrics[2] = corrCoeff;
		metrics[3] = snr;

		return metrics;
	}

}
