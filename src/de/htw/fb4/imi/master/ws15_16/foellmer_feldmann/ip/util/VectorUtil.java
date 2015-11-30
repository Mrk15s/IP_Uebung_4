/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public class VectorUtil {

	public static int calcCrossProduct(Vector2D a, Vector2D b) {
		return a.getX() * b.getY() - a.getY() * b.getX();	
	}

	
}
