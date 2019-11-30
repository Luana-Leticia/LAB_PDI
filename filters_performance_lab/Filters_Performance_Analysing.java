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
		
		// Get windows dimensions of each filter
		GenericDialog gd = createParametersDialog();
		
		// Create a vector for storing the values of dialog
		int[] params = new int[10];

		int values = 10;
		
		for (int i = 0; i < values; i++) {
			params[i] = (int) gd.getNextNumber();
		}
		
		// Initialize the performance class
		Filters_Performance fp = new Filters_Performance();
		
		// Iterate for performing each filter with the respective windows
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

}
