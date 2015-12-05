/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.IOriginalPixels;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineEdge;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineSequenceSet;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.ImageUtil;

/**
 * [SHORT_DESCRIPTION]
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 05.12.2015
 */
public class FileOutlineParser implements IOutlineParser {
	protected IOriginalPixels originalPixelsAlgorithm;

	public void setOriginalPixelsAlgorithm(IOriginalPixels originalPixelsAlgorithm) {
		this.originalPixelsAlgorithm = originalPixelsAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.parser.IOutlineParser#
	 * parse(java.io.File)
	 */
	@Override
	public OutlineSequenceSet parse(File inputFile) throws IOException {
		OutlineSequenceSet set = new OutlineSequenceSet();

		createOutlines(set, inputFile);

		return set;
	}

	private void createOutlines(OutlineSequenceSet set, File inputFile) throws IOException {
		FileReader fReader = null;
		BufferedReader bReader = null;

		try {
			fReader = new FileReader(inputFile);
			bReader = new BufferedReader(fReader);
			Outline outline = null;
			Pattern pPoint = Pattern.compile("p\\s([0-9]+)\\s([0-9]+)");

			while (bReader.ready()) {
				String line = bReader.readLine();

				if (line.equals("outer")) {
					if (null != outline) {
						set.add(outline);
					}

					outline = new Outline(true);
					outline.setOriginalPixels(this.originalPixelsAlgorithm.getOriginalBinaryPixels());
				} else if (line.equals("inner")) {
					if (null != outline) {
						set.add(outline);
					}

					outline = new Outline(false);
					outline.setOriginalPixels(this.originalPixelsAlgorithm.getOriginalBinaryPixels());
				} else {
					Matcher mPoint = pPoint.matcher(line);

					if (mPoint.matches()) {
						int x = Integer.parseInt(mPoint.group(1));
						int y = Integer.parseInt(mPoint.group(2));

						Vertex whiteVertex = getWhiteVertex(x, y);
						Vertex blackVertex = new Vertex(x, y);

						OutlineEdge edge = new OutlineEdge(whiteVertex, blackVertex);
						outline.addEdge(edge);
						outline.setVerticesForEachCorner(true);
					}
				}
			}

			if (null != outline) {
				// file end
				set.add(outline);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != bReader) {
				try {
					bReader.close();
				} catch (IOException e) {
				}
			}
			if (null != fReader) {
				try {
					fReader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private Vertex getWhiteVertex(int x, int y) {
		Vertex leftWhite = new Vertex(x - 1, y);

		if ((!this.originalPixelsAlgorithm.isWithinImageBoundaries(leftWhite)
				|| !ImageUtil.isForegoundPixel(this.originalPixelsAlgorithm.getOriginalBinaryPixels()[leftWhite.getX()][leftWhite.getY()]))) {
			return leftWhite;
		}
		
		Vertex rightWhite = new Vertex(x + 1, y);

		if ((!this.originalPixelsAlgorithm.isWithinImageBoundaries(rightWhite)
				|| !ImageUtil.isForegoundPixel(this.originalPixelsAlgorithm.getOriginalBinaryPixels()[rightWhite.getX()][rightWhite.getY()]))) {
			return rightWhite;
		}		

		Vertex topWhite = new Vertex(x, y - 1);

		if ((!this.originalPixelsAlgorithm.isWithinImageBoundaries(topWhite)
				|| !ImageUtil.isForegoundPixel(this.originalPixelsAlgorithm.getOriginalBinaryPixels()[topWhite.getX()][topWhite.getY()]))) {
			return topWhite;
		}		
		
		Vertex bottomWhite = new Vertex(x, y + 1);

		if ((!this.originalPixelsAlgorithm.isWithinImageBoundaries(bottomWhite)
				|| !ImageUtil.isForegoundPixel(this.originalPixelsAlgorithm.getOriginalBinaryPixels()[bottomWhite.getX()][bottomWhite.getY()]))) {
			return bottomWhite;
		}		

		return leftWhite;
	}
}
