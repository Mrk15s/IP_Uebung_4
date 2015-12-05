/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.parser;

import java.io.File;
import java.io.IOException;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineSequenceSet;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 05.12.2015
 */
public interface IOutlineParser {

	/**
	 * Parse outlines from a given file.
	 * @param inputFile
	 * @return
	 */
	OutlineSequenceSet parse(File inputFile) throws IOException;
}
