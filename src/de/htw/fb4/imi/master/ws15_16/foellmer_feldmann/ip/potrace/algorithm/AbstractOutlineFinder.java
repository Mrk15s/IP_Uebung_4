/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm;

import java.util.Set;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.util.ImageUtil;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 05.12.2015
 */
public abstract class AbstractOutlineFinder implements IOutlinePathFinder {
	protected TurnPolicy turnPolicy = TurnPolicy.TURN_RIGHT;
	protected int width;
	protected int height;
	protected int[][] originalPixels;
	private IOutlinePathFinder outlinePathFinder;
	protected int[][] processingPixels;
	protected int[][] processedPixels;

	public AbstractOutlineFinder() {
		this.outlinePathFinder = null;
	}
	
	public AbstractOutlineFinder(IOutlinePathFinder outlinePathFinder)
	{
		this.outlinePathFinder = outlinePathFinder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.IOriginalPixels#
	 * setOriginalBinaryPixels(int, int, int[])
	 */
	@Override
	public void setOriginalBinaryPixels(int width, int height, int[] originalPixels) {		
		this.setOriginalBinaryPixels(ImageUtil.get2DFrom1DArray(width, height, originalPixels));
	}

	public TurnPolicy getTurnPolicy() {
		return turnPolicy;
	}

	public void setTurnPolicy(TurnPolicy turnPolicy) {
		this.turnPolicy = turnPolicy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.IOriginalPixels#
	 * setOriginalBinaryPixels(int[][])
	 */
	@Override
	public void setOriginalBinaryPixels(int[][] originalPixels) {
		this.width = originalPixels.length;
		this.height = originalPixels[0].length;

		this.originalPixels = originalPixels;
		this.processedPixels = new int[width][height];
		
		if (null != this.outlinePathFinder) {
			this.outlinePathFinder.setOriginalBinaryPixels(originalPixels);
		}
	}
	
	@Override
	public int[][] getOriginalBinaryPixels() {
		return this.originalPixels;
	}

	public boolean isWithinImageBoundaries(int x, int y) {
		return (x >= 0) && (x < width) && (y >= 0) && (y < height);
	}

	public boolean isWithinImageBoundaries(Vertex v) {
		return this.isWithinImageBoundaries(v.getX(), v.getY());
	}

	/* (non-Javadoc)
	 * @see de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.algorithm.IOutlinePathFinder#find()
	 */
	@Override
	public Set<Outline> find() {
		if (null != this.outlinePathFinder) {
			return this.outlinePathFinder.find();
		}
		
		return null;
	}

}
