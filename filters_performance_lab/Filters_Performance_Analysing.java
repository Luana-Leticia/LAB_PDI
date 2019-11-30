package filters_performance_lab;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

/**
 *  This plugIn call a class that implements routines that analyzes 5 filters: 
 *  Moving Average, Median, Gaussian Blur, Wiener and IBSF.
 *  The windows dimensions of each filter is requested to the user.
 *   
 *   @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 *   
 *   Reference: Digital Images Processing - Wilhelm Burger.
 * 
 */

public class Filters_Performance_Analysing implements PlugIn {

	public Filters_Performance_Analysing() {
		// TODO Auto-generated constructor stub
	}
	
	// Call method that run dialog box and perform filters
	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		
		// Get paths of files and folder where new files will be saved
		GenericDialog gdPaths = createDirectoriesDialog();
		
		// Create a vector for storing the strings of dialog
		String[] paths = new String[3];
		
		// Iterate to get string of paths and store in the vector
		int strings = 3;
		for (int i = 0; i < strings; i++) {
			paths[i] = gdPaths.getNextString();
		}
		
		// Get windows dimensions of each filter
		GenericDialog gdParams = createParametersDialog();
		
		// Create a vector for storing the values of dialog
		int[] params = new int[10];
		
		// Iterate to get values of parameters and store in the vector
		int values = 10;		
		for (int i = 0; i < values; i++) {
			params[i] = (int) gdParams.getNextNumber();
		}
		
		// Initialize the performance class with the paths
		Filters_Performance fp = new Filters_Performance(paths);
		
		// Iterate for performing each filter with the respective windows parameters
		int filter = 5;
		
		for (int j = 0; j < filter; j++) {
			fp.filter_performance(j + 1, params[2 * j], params[2 * j + 1]);
		}

	}
	
	// Method create a dialog window to get windows dimensions of filters
	private GenericDialog createParametersDialog() {

		GenericDialog gd = new GenericDialog("Filters Windows");
		gd.addMessage("Choose the filters windows dimensions\n\n");
		gd.addMessage("Moving Average Filter MxN");
		gd.addNumericField("M: ", 3, 1);
		gd.addNumericField("N: ", 3, 1);
		gd.addMessage("Median Filter MxN");
		gd.addNumericField("M: ", 5, 1);
		gd.addNumericField("N: ", 5, 1);
		gd.addMessage("Gaussian Blur Filter MxN");
		gd.addNumericField("M: ", 11, 1);
		gd.addNumericField("N: ", 11, 1);
		gd.addMessage("Wiener Filter MxN");
		gd.addNumericField("M: ", 7, 1);
		gd.addNumericField("N: ", 7, 1);
		gd.addMessage("IBSF Filter MxN");
		gd.addNumericField("M: ", 9, 1);
		gd.addNumericField("N: ", 9, 1);
		gd.pack();
		gd.showDialog();
		return gd;
	}
	
	// Method create a dialog window to get directories of folder files
	private GenericDialog createDirectoriesDialog() {
			
			GenericDialog gd = new GenericDialog("Directories folder paths");
			gd.addMessage("Tell the paths folder used to\n\n");
			gd.addMessage("Get reference and noisy images:");
			gd.addStringField("Path:", "C:/Users/luana/Downloads/LAB03/lab03_imagens", 50);
			gd.addMessage("Save filtered images:");
			gd.addStringField("Path:", "C:/Users/luana/Downloads/LAB03/imagens-filtradas", 50);
			gd.addMessage("Save tables of metrics:");
			gd.addStringField("Path:", "C:/Users/luana/Downloads/LAB03/tabelas", 50);
			gd.addMessage("\n(please check if bars orientation is as default: '/') \n\n");
			gd.pack();	
			gd.showDialog();
			return gd;
		}

}
