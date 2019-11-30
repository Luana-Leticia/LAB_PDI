package metrics;

/**
 *  Class edited from: Digital Images Processing - Wilhelm Burger.
 *  
 *  By: Luana Letícia de Souza Silva // Bianca de Lima Xavier Gomes
 * 
 */

import ij.process.ImageProcessor;

public class Corner implements Comparable<Corner> {
	
	int u;
	int v;
	float q;

	public Corner(int u, int v, float q) {
		this.u = u;
		this.v = v;
		this.q = q;
	}

	public int compareTo(Corner c2) {
		// used for sorting corners by corner strength q
		if (this.q > c2.q)
			return -1;
		if (this.q < c2.q)
			return 1;
		else
			return 0;
	}

	double dist2(Corner c2) {
		// returns the squared distance between this corner and corner c2
		float dx = this.u - c2.u;
		float dy = this.v - c2.v;
		return (dx * dx) + (dy * dy);
	}
	
	void draw(ImageProcessor ip) {
		// draw this corner as a black cross in ip
		int paintvalue = 0; // black
		int size = 4;
		ip.setValue(paintvalue);
		ip.drawLine(u - size, v, u + size, v);
		ip.drawLine(u, v - size, u, v + size);
	}

}
