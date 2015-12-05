/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.Set;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineEdge;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineSequenceSet;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.ImageUtil;

/**
 * [SHORT_DESCRIPTION]
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 17.11.2015
 */
public class PotraceOutlineFinder extends AbstractOutlineFinder {
	private static final int PROCESSED = 1;

	private OutlineSequenceSet outlines;	

	public PotraceOutlineFinder(IOutlinePathFinder outlineFinder) {
		super(outlineFinder);
	}

	public PotraceOutlineFinder()
	{
		super();
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
			this.outlines = new OutlineSequenceSet();
		} else {
			return superOutlines;
		}

		this.findOuterPathes();
		this.findInnerPathes();

		return outlines;
	}

	private void findOuterPathes() {
		copyOriginalPixels();

		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				int pixel = originalPixels[x][y];

				if (PROCESSED != this.processedPixels[x][y] && ImageUtil.isForegoundPixel(pixel)) {
					Outline outerOutline = this.createPath(x, y, true);
					
					if (null != outerOutline) {
						outerOutline.setOriginalPixels(this.originalPixels);
						outerOutline.finishOutline();
				
						outlines.add(outerOutline);
					}		
				}
			}
		}
	}

	private void findInnerPathes() {
		copyOriginalPixels();

		OutlineSequenceSet outerOutlines = new OutlineSequenceSet(outlines);
		OutlineSequenceSet innerOutlines = new OutlineSequenceSet();

		this.invertPixelsInOutlines(outlines);

		for (Outline outerOutline : outerOutlines) {
			for (int y = outerOutline.getTopLimitY(); y <= outerOutline.getBottomLimitY(); y++) {
				Integer[] allXValues = outerOutline.getXValues(y);

				for (int x : allXValues) {
					int pixel = processingPixels[x][y];
		
					if (PROCESSED != this.processedPixels[x][y] && ImageUtil.isForegoundPixel(pixel)) {
						Outline innerOutline = this.createPath(x, y, false);
						
						if (null != innerOutline) {
							innerOutlines.add(innerOutline);
						}
					}
				}
			}
		}
		
		outlines.addAll(innerOutlines);
	}

	protected void copyOriginalPixels() {
		// copy the original pixels since we don't want to change them on
		// reversion
		this.processingPixels = java.util.Arrays.copyOf(this.originalPixels, this.originalPixels.length);
	}

	private void invertPixelsInOutlines(OutlineSequenceSet outerOutlines) {
		for (Outline outerOutline : outerOutlines) {
			for (int y = outerOutline.getTopLimitY(); y <= outerOutline.getBottomLimitY(); y++) {
				Integer[] allXValues = outerOutline.getXValues(y);

				for (Integer x : allXValues) {
					this.processingPixels[x][y] = ImageUtil.invertPixel(this.processingPixels[x][y]);
				}
			}
		}
	}

	private Outline createPath(int x, int y, boolean isOuter) {
		Outline sequence = new Outline(isOuter);

		OutlineEdge e = this.getInitialEdge(x, y, isOuter);

		if (null == e) {
			// initial edge doesn't match pattern (left black, right white)
			return null;
		}
		
		sequence.addEdge(e);

		while (null != e) {
			e = findNextEdgeOnOutline(e, sequence);

			if (sequence.hasEdge(e)) {
				// we reached the beginning
				e = null;
			} else if (null != e) {
				sequence.addEdge(e);

				if (this.isWithinImageBoundaries(e.getBlack())) {
					this.processedPixels[e.getBlack().getX()][e.getBlack().getY()] = PROCESSED;
				}
			}
		}

		return sequence;
	}

	private OutlineEdge getInitialEdge(int x, int y, boolean isOuter) {
		Vertex black = new Vertex(x, y); // current vertex (black pixel)
		Vertex whiteLeft = new Vertex(x - 1, y); // left neighbor of current
													// vertex (white pixel)
		if (this.isWithinImageBoundaries(black) && this.isWithinImageBoundaries(whiteLeft)
				&& ImageUtil.isForegoundPixel(this.processingPixels[black.getX()][black.getY()])
				&& !ImageUtil.isForegoundPixel(this.processingPixels[whiteLeft.getX()][whiteLeft.getY()])
				&& (!outlines.isSurroundedByAnExistingOutline(black, true) || !isOuter)
				) {
			return new OutlineEdge(whiteLeft, black);
		}
		
		return null;
	}

	private OutlineEdge findNextEdgeOnOutline(OutlineEdge startEdge, Outline sequence) {
		OutlineEdge potentialEdge = this.getFirstPatternEdge(startEdge);

		if (null != potentialEdge) {
			return potentialEdge;
		}

		potentialEdge = this.getSecondPatternEdge(startEdge);

		if (null != potentialEdge) {
			return potentialEdge;
		}

		potentialEdge = this.getThirdPatternEdge(startEdge);

		if (null != potentialEdge) {
			return potentialEdge;
		}

		potentialEdge = this.getFourthPatternEdge(startEdge);

		if (null != potentialEdge) {
			return potentialEdge;
		}

		return null;
	}

	/**
	 * Check for "pattern" BLACK BLACK BLACK WHITE
	 * 
	 * @param startEdge
	 * @return null if the pattern didn't match, otherwise turn right and return
	 *         next edge next edge
	 */
	private OutlineEdge getFirstPatternEdge(OutlineEdge currentEdge) {
		Vertex leftBlackAhead = new Vertex(currentEdge.getBlack().getX() + currentEdge.getDirectionX(),
				currentEdge.getBlack().getY() + currentEdge.getDirectionY());

		Vertex rightBlackAhead = new Vertex(currentEdge.getWhite().getX() + currentEdge.getDirectionX(),
				currentEdge.getWhite().getY() + currentEdge.getDirectionY());

		if (this.isWithinImageBoundaries(leftBlackAhead) && this.isWithinImageBoundaries(rightBlackAhead)
				&& ImageUtil.isForegoundPixel(this.processingPixels[leftBlackAhead.getX()][leftBlackAhead.getY()])
				&& ImageUtil.isForegoundPixel(this.processingPixels[rightBlackAhead.getX()][rightBlackAhead.getY()])) {
			// pattern matches
			return new OutlineEdge(currentEdge.getWhite(), rightBlackAhead);
		}

		return null;
	}

	/**
	 * Check for "pattern" BLACK WHITE BLACK WHITE
	 * 
	 * @param startEdge
	 * @return null if the pattern didn't match, otherwise go ahead and return
	 *         next edge
	 */
	private OutlineEdge getSecondPatternEdge(OutlineEdge currentEdge) {
		Vertex leftBlackAhead = new Vertex(currentEdge.getBlack().getX() + currentEdge.getDirectionX(),
				currentEdge.getBlack().getY() + currentEdge.getDirectionY());

		Vertex rightWhiteAhead = new Vertex(currentEdge.getWhite().getX() + currentEdge.getDirectionX(),
				currentEdge.getWhite().getY() + currentEdge.getDirectionY());

		if (this.isWithinImageBoundaries(leftBlackAhead)
				&& ImageUtil.isForegoundPixel(this.processingPixels[leftBlackAhead.getX()][leftBlackAhead.getY()])
				&& (!this.isWithinImageBoundaries(rightWhiteAhead) || !ImageUtil
						.isForegoundPixel(this.processingPixels[rightWhiteAhead.getX()][rightWhiteAhead.getY()]))) {
			// pattern matches
			return new OutlineEdge(rightWhiteAhead, leftBlackAhead);
		}

		return null;
	}

	/**
	 * Check for "pattern" WHITE WHITE BLACK WHITE
	 * 
	 * @param startEdge
	 * @return null if the pattern didn't match, otherwise turn left and return
	 *         next edge
	 */
	private OutlineEdge getThirdPatternEdge(OutlineEdge currentEdge) {
		Vertex leftWhiteAhead = new Vertex(currentEdge.getBlack().getX() + currentEdge.getDirectionX(),
				currentEdge.getBlack().getY() + currentEdge.getDirectionY());

		Vertex rightWhiteAhead = new Vertex(currentEdge.getWhite().getX() + currentEdge.getDirectionX(),
				currentEdge.getWhite().getY() + currentEdge.getDirectionY());

		if ((!this.isWithinImageBoundaries(leftWhiteAhead)
				|| !ImageUtil.isForegoundPixel(this.processingPixels[leftWhiteAhead.getX()][leftWhiteAhead.getY()]))
				&& (!this.isWithinImageBoundaries(rightWhiteAhead) || !ImageUtil
						.isForegoundPixel(this.processingPixels[rightWhiteAhead.getX()][rightWhiteAhead.getY()]))) {
			// pattern matches
			return new OutlineEdge(leftWhiteAhead, currentEdge.getBlack());
		}

		return null;
	}

	/**
	 * Check for "pattern" WHITE BLACK BLACK WHITE
	 * 
	 * @param startEdge
	 * @return null if the pattern didn't match, otherwise turn left and return
	 *         next edge
	 */
	private OutlineEdge getFourthPatternEdge(OutlineEdge currentEdge) {
		Vertex leftWhiteAhead = new Vertex(currentEdge.getBlack().getX() + currentEdge.getDirectionX(),
				currentEdge.getBlack().getY() + currentEdge.getDirectionY());

		Vertex rightBlackAhead = new Vertex(currentEdge.getWhite().getX() + currentEdge.getDirectionX(),
				currentEdge.getWhite().getY() + currentEdge.getDirectionY());

		if ((!this.isWithinImageBoundaries(leftWhiteAhead)
				|| !ImageUtil.isForegoundPixel(this.processingPixels[leftWhiteAhead.getX()][leftWhiteAhead.getY()]))
				&& this.isWithinImageBoundaries(rightBlackAhead)
				&& ImageUtil.isForegoundPixel(this.processingPixels[rightBlackAhead.getX()][rightBlackAhead.getY()])) {
			// pattern matches, so delegate to configured turn policy
			return this.turnPolicy.getNextEdge(currentEdge, leftWhiteAhead, rightBlackAhead);
		}

		return null;
	}
}
