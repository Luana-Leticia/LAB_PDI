package filters_performance_lab;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

/**
 * This plugIn call a class that implements routines that analyzes 5 filters:
 * Moving Average, Median, Gaussian Blur, Wiener and IBSF. The windows
 * dimensions of each filter is requested to the user.
 * 
 * @authors: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 * 
 *           Reference: Digital Images Processing - Wilhelm Burger.
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

		// Show generic dialog and get path of folder of files that will be used
		// And folders where new files will be saved
		GenericDialog gdPaths = createDirectoriesDialog();

		// Create a vector for storing the strings paths of dialog
		String[] paths = new String[3];

		// Iterate to get the strings and store in the vector
		int strings = 3;
		for (int i = 0; i < strings; i++) {
			paths[i] = gdPaths.getNextString() + "/";
		}

		// Show generic dialog and get windows dimensions of each chosen filter
		GenericDialog gdParams = createParametersDialog();

		// Create a vector for storing the check boxes values for filters
		boolean[] checkboxes = new boolean[5];

		// Create a vector for storing the windows dimensions values of the filters
		int[] params = new int[10];

		// Iterate to get the value of check box and the values of windows dimensions for each filter
		// And store them in the respective vectors
		int filter = 5;
		for (int i = 0; i < filter; i++) {
			checkboxes[i] = gdParams.getNextBoolean(); // Returns if filter was selected
			// Windows dimensions of filter
			params[2 * i] = (int) gdParams.getNextNumber();
			params[2 * i + 1] = (int) gdParams.getNextNumber();
		}

		// Initialize the performance class with the paths
		Filters_Performance fp = new Filters_Performance(paths);

		// Iterate for performing each chosen filter with the given windows parameters
		for (int i = 0; i < filter; i++) {
			if (checkboxes[i]) {
				// If filter (i) was selected to be analyzed, analyze filter
				fp.filter_performance(i + 1, params[2 * i], params[2 * i + 1]);
			}
		}

	}

	// Method create a dialog window to get windows dimensions of filters
	private GenericDialog createParametersDialog() {

		GenericDialog gd = new GenericDialog("Filters Windows");
		gd.addMessage("Choose the filters windows dimensions\n\n");
		gd.addCheckbox("Moving Average Filter MxN", true);
		gd.addNumericField("M: ", 3, 1);
		gd.addNumericField("N: ", 3, 1);
		gd.addCheckbox("Median Filter MxN", true);
		gd.addNumericField("M: ", 5, 1);
		gd.addNumericField("N: ", 5, 1);
		gd.addCheckbox("Gaussian Blur Filter MxN", true);
		gd.addNumericField("M: ", 11, 1);
		gd.addNumericField("N: ", 11, 1);
		gd.addCheckbox("Wiener Filter MxN", true);
		gd.addNumericField("M: ", 7, 1);
		gd.addNumericField("N: ", 7, 1);
		gd.addCheckbox("IBSF Filter MxN", true);
		gd.addNumericField("M: ", 9, 1);
		gd.addNumericField("N: ", 9, 1);
		gd.pack();
		gd.showDialog();
		return gd;
	}

	// Method create a dialog window to get directories paths
	private GenericDialog createDirectoriesDialog() {

		GenericDialog gd = new GenericDialog("Directories paths");
		gd.addMessage("Tell the paths folder used to\n\n");
		gd.addMessage("Get reference and noisy images:");
		gd.addStringField("Path:", "C:/Users/luana/Downloads/LAB03/lab03_imagens", 70);
		gd.addMessage("Save filtered images:");
		gd.addStringField("Path:", "C:/Users/luana/Downloads/LAB03/imagens-filtradas", 70);
		gd.addMessage("Save tables of metrics:");
		gd.addStringField("Path:", "C:/Users/luana/Downloads/LAB03/tabelas", 70);
		gd.addMessage("\n(please check if slash orientation is as default:  '/' ) \n\n");
		gd.pack();
		gd.showDialog();
		return gd;
	}

}
