/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.parser.FileOutlineParser;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineSequenceSet;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 05.12.2015
 */
public class TxtOutlineReader extends AbstractOutlineFinder {
	private static final String HEAD_PNG = "head.png";
	private static final String KLEIN_PNG = "klein.png";

	private File imageFile;	
	private FileOutlineParser parser;
	
	public TxtOutlineReader()
	{
		super();
	}
	
	public TxtOutlineReader(IOutlinePathFinder outlineFinder) {
		super(outlineFinder);
	}
	
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
	
	public void setParser(FileOutlineParser parser) {
		this.parser = parser;
		this.parser.setOriginalPixelsAlgorithm(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.
	 * IOutlinePathFinder#find()
	 */
	@Override
	public Set<Outline> find() {
		Set<Outline> superOutlines = super.find();
		
		if (null == superOutlines) {			
			if (!this.applies()) {
				return null;
			} else {				
				Set<Outline> parseOutlinesFromFile = this.parseOutlinesFromFile();
				return parseOutlinesFromFile;
			}
		} else {
			return superOutlines;
		}
	}

	private boolean applies() {
		return imageFile.getName().equals(HEAD_PNG)
				|| imageFile.getName().equals(KLEIN_PNG);
	}
	
	private Set<Outline> parseOutlinesFromFile() {
		try {
			return this.parser.parse(getContoursFile());
		} catch (IOException e) {
			System.err.println("Can't parse contours file: " + e);
			return new OutlineSequenceSet();
		}
	}

	protected File getContoursFile() {
		return new File(imageFile.getAbsolutePath() + ".contours.txt");
	}
}
