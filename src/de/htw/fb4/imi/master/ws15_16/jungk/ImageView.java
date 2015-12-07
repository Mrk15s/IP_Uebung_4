// Copyright (C) 2010 by Klaus Jung
// All rights reserved.
// Date: 2010-03-15
package de.htw.fb4.imi.master.ws15_16.jungk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vector2D;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.Vertex;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.Outline;
import de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip.potrace.models.OutlineEdge;

public class ImageView extends JScrollPane {

	private static final long serialVersionUID = 1L;

	static final int MIN_ZOOM = 10;

	static final Color COLOR_INNER_OUTLINE = Color.ORANGE;
	static final Color COLOR_OUTER_OUTLINE = Color.RED;
	static final Color COLOR_POLYGON = Color.BLUE;

	static final float WIDTH_OUTLINE = 2;
	static final float WIDTH_POLYGON = 3;
	static final int DIAMETER_POLYGON_DOT = 4;

	static final boolean SHOW_IMAGE_DEFAULT = true;
	static final boolean SHOW_OUTLINES_DEFAULT = false;
	static final boolean SHOW_INNER_OUTLINES_DEFAULT = false;
	static final boolean SHOW_OUTLINES_POLYGONS_DEFAULT = false;

	private ImageScreen screen = null;
	private Dimension maxSize = null;
	private int borderX = -1;
	private int borderY = -1;
	private double maxViewMagnification = 0.0; // use 0.0 to disable limits
	private boolean keepAspectRatio = true;
	private boolean centered = true;

	public static boolean boolRaster = false;

	protected Set<Outline> outlines;
	protected Set<Vector2D[]> polygons;

	private double zoom = MIN_ZOOM;
	int pixels[] = null; // pixel array in ARGB format

	private boolean showImage = SHOW_IMAGE_DEFAULT;
	private boolean displayInnerOutline = SHOW_INNER_OUTLINES_DEFAULT;
	private boolean showOutlines = SHOW_OUTLINES_DEFAULT;
	private boolean showPolygons = SHOW_OUTLINES_POLYGONS_DEFAULT;

	public ImageView(int width, int height) {
		// construct empty image of given size
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		init(bi, true);
	}

	public ImageView(File file) {
		// construct image from file
		loadImage(file);
	}
	
	public void setShowImage(boolean showImage) {
		this.showImage = showImage;
		screen.revalidate();
	}

	public void setDisplayInner(boolean selected) {
		this.displayInnerOutline = selected;
		screen.revalidate();
	}

	public void setShowOutlines(boolean showOutlines) {
		this.showOutlines = showOutlines;
		screen.revalidate();
	}

	public void setShowPolygons(boolean showPolygons) {
		this.showPolygons = showPolygons;
		screen.revalidate();
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
		screen.revalidate();
	}

	public Set<Outline> getOutlines() {
		return outlines;
	}

	public void setOutlines(Set<Outline> foundOutlines) {
		this.outlines = foundOutlines;
		screen.revalidate();
	}

	public void setOuterPolygons(Set<Vector2D[]> polygons2) {
		this.polygons = polygons2;
		screen.revalidate();
	}

	public void setMaxSize(Dimension dim) {
		// limit the size of the image view
		maxSize = new Dimension(dim);

		Dimension size = new Dimension(maxSize);
		if (size.width - borderX > screen.image.getWidth())
			size.width = screen.image.getWidth() + borderX;
		if (size.height - borderY > screen.image.getHeight())
			size.height = screen.image.getHeight() + borderY;
		setPreferredSize(size);
	}

	public int getImgWidth() {
		return screen.image.getWidth();
	}

	public int getImgHeight() {
		return screen.image.getHeight();
	}

	public void resetToSize(int width, int height) {
		// resize image and erase all content
		if (width == getImgWidth() && height == getImgHeight())
			return;

		screen.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = new int[getImgWidth() * getImgHeight()];
		screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());

		Dimension size = new Dimension(maxSize);
		if (size.width - borderX > width)
			size.width = width + borderX;
		if (size.height - borderY > height)
			size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}

	public int[] getPixels() {
		// get reference to internal pixels array
		if (pixels == null) {
			pixels = new int[getImgWidth() * getImgHeight()];
			screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
		}
		return pixels;
	}

	public void applyChanges() {
		// if the pixels array obtained by getPixels() has been modified,
		// call this method to make your changes visible
		if (pixels != null)
			setPixels(pixels);
	}

	public void setPixels(int[] pix) {
		// set pixels with same dimension
		setPixels(pix, getImgWidth(), getImgHeight());
	}

	public void setPixels(int[] pix, int width, int height) {
		// set pixels with arbitrary dimension
		if (pix == null || pix.length != width * height)
			throw new IndexOutOfBoundsException();

		if (width != getImgWidth() || height != getImgHeight()) {
			// image dimension changed
			screen.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			pixels = null;
		}

		screen.image.setRGB(0, 0, width, height, pix, 0, width);

		if (pixels != null && pix != pixels) {
			// update internal pixels array
			System.arraycopy(pix, 0, pixels, 0, Math.min(pix.length, pixels.length));
		}

		Dimension size = new Dimension(maxSize);
		if (size.width - borderX > width)
			size.width = width + borderX;
		if (size.height - borderY > height)
			size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}

	public double getMaxViewMagnification() {
		return maxViewMagnification;
	}

	// set 0.0 to disable limits
	//
	public void setMaxViewMagnification(double mag) {
		maxViewMagnification = mag;
	}

	public boolean getKeepAspectRatio() {
		return keepAspectRatio;
	}

	public void setKeepAspectRatio(boolean keep) {
		keepAspectRatio = keep;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	public void printText(int x, int y, String text) {
		Graphics2D g = screen.image.createGraphics();

		Font font = new Font("TimesRoman", Font.BOLD, 12);
		g.setFont(font);
		g.setPaint(Color.black);
		g.drawString(text, x, y);
		g.dispose();

		updatePixels(); // update the internal pixels array
	}

	public void clearImage() {
		Graphics2D g = screen.image.createGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, getImgWidth(), getImgHeight());
		g.dispose();

		updatePixels(); // update the internal pixels array
	}

	public void loadImage(File file) {
		// load image from file
		BufferedImage bi = null;
		boolean success = false;

		try {
			bi = ImageIO.read(file);
			success = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Bild konnte nicht geladen werden.", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			bi = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		}

		init(bi, !success);

		if (!success)
			printText(5, getImgHeight() / 2, "Bild konnte nicht geladen werden.");
	}

	public void saveImage(String fileName) {
		try {
			File file = new File(fileName);
			String ext = (fileName.lastIndexOf(".") == -1) ? ""
					: fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
			if (!ImageIO.write(screen.image, ext, file))
				throw new Exception("Image save failed");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Bild konnte nicht geschrieben werden.", "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void init(BufferedImage bi, boolean clear) {
		screen = new ImageScreen(bi);
		setViewportView(screen);

		maxSize = new Dimension(getPreferredSize());

		if (borderX < 0)
			borderX = maxSize.width - bi.getWidth();
		if (borderY < 0)
			borderY = maxSize.height - bi.getHeight();

		if (clear)
			clearImage();

		pixels = null;
	}

	private void updatePixels() {
		if (pixels != null)
			screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
	}

	class ImageScreen extends JComponent {
		private static final long serialVersionUID = 1L;

		private BufferedImage image = null;

		private int offsetX;

		private int offsetY;

		private Rectangle r;

		public ImageScreen(BufferedImage bi) {
			super();
			image = bi;
		}

		public void paintComponent(Graphics g) {
			if (image != null) {
				r = this.getBounds();

				// g.clearRect(0, 0, (int) r.getWidth(), (int) r.getHeight());

				// limit image view magnification
				if (maxViewMagnification > 0.0) {
					int maxWidth = (int) (image.getWidth() * maxViewMagnification + 0.5);
					int maxHeight = (int) (image.getHeight() * maxViewMagnification + 0.5);

					if (r.width > maxWidth)
						r.width = maxWidth;
					if (r.height > maxHeight)
						r.height = maxHeight;
				}

				// keep aspect ratio
				if (keepAspectRatio) {
					double ratioX = (double) r.width / image.getWidth();
					double ratioY = (double) r.height / image.getHeight();
					if (ratioX < ratioY)
						r.height = (int) (ratioX * image.getHeight() + 0.5);
					else
						r.width = (int) (ratioY * image.getWidth() + 0.5);
				}

				this.offsetX = 0;
				this.offsetY = 0;

				// set background for regions not covered by image
				if (r.height < getBounds().height) {
					g.setColor(SystemColor.window);
					if (centered)
						offsetY = (getBounds().height - r.height) / 2;
					g.fillRect(0, 0, getBounds().width, offsetY);
					g.fillRect(0, r.height + offsetY, getBounds().width, getBounds().height - r.height - offsetY);
				}

				if (r.width < getBounds().width) {
					g.setColor(SystemColor.window);
					if (centered)
						offsetX = (getBounds().width - r.width) / 2;
					g.fillRect(0, offsetY, offsetX, r.height);
					g.fillRect(r.width + offsetX, offsetY, getBounds().width - r.width - offsetX, r.height);
				}

				// draw image
				if (showImage) {
					g.drawImage(image, offsetX, offsetY, r.width, r.height, this);
				}

				if (boolRaster) {
					this.paintRaster(g);
				}

				if (showOutlines) {
					this.paintOutlines(g);
				}

				if (showPolygons) {
					this.paintPolygons(g);
				}
			}
		}

		private void paintRaster(Graphics g) {
			g.setColor(Color.GRAY);
			for (int i = 1; i < (image.getWidth()); i++) {
				for (int j = 1; j < (image.getHeight()); j++) {
					g.drawLine(i * (int) zoom, offsetY, i * (int) zoom, image.getHeight() * (int) zoom);
					g.drawLine(offsetX, j * (int) zoom, image.getWidth() * (int) zoom, j * (int) zoom);
				}
			}
		}

		private void paintOutlines(Graphics g) {

			for (Outline outline : outlines) {
				if (outline.isOuter() || displayInnerOutline) {
					if (outline.hasVerticesForEachCorner()) {
						paintOutlineCornerBased(g, outline);
					} else {
						paintOutline(g, outline);
					}
				}
			}
		}

		private void paintOutline(Graphics g, Outline outline) {
			for (OutlineEdge edgeOnOutline : outline.getEdges()) {
				paintEdgeOnOutline(g, outline, edgeOnOutline);
			}
		}
		
		private void paintOutlineCornerBased(Graphics g, Outline outline) {
			Vertex beforeBlack = null;
			for (OutlineEdge edgeOnOutline : outline.getEdges()) {
				if (null != beforeBlack) {
					beforeBlack = paintEdgeOnOutlineCornerBased(g, outline, edgeOnOutline, beforeBlack);
				} else {
					beforeBlack = edgeOnOutline.getBlack();
				}
			}
		}

		private Vertex paintEdgeOnOutline(Graphics g, Outline outline, OutlineEdge edgeOnOutline) {
			Color edgeColor = outline.isOuter() ? COLOR_OUTER_OUTLINE : COLOR_INNER_OUTLINE;

			// offset to set the correct black pixel corner
			int offsetX = 0;
			int offsetY = 0;
			if (edgeOnOutline.vertexIsLeftNeighbourOf(edgeOnOutline.getWhite(), edgeOnOutline.getBlack())) {
				// case: white pixel is left of black
				offsetX = 0;
				offsetY = 0;
				System.out.println("Case 0");
			} else if (edgeOnOutline.vertexIsRightNeighbourOf(edgeOnOutline.getWhite(), edgeOnOutline.getBlack())) {
				// case: white pixel is right of black
				offsetX = 1;
				offsetY = 1;
				System.out.println("Case 1");
			} else if (edgeOnOutline.vertexIsBelowNeighbourOf(edgeOnOutline.getWhite(), edgeOnOutline.getBlack())) {
				// case: white pixel is below of black
				offsetX = 0;
				offsetY = 1;
				System.out.println("Case 2");
			} else if (edgeOnOutline.vertexIsAboveNeighbourOf(edgeOnOutline.getWhite(), edgeOnOutline.getBlack())) {
				// case: white pixel is above of black
				offsetX = 1;
				offsetY = 0;
				System.out.println("Case 3");
			}

			// offsetX -= outline.isOuter() ? 0 : 1;
			int startX = this.calcScaledX(edgeOnOutline.getBlack().getX() + offsetX);
			int startY = this.calcScaledY(edgeOnOutline.getBlack().getY() + offsetY);
			int targetX = this.calcScaledX(edgeOnOutline.getBlack().getX() + edgeOnOutline.getDirectionX() + offsetX);
			int targetY = this.calcScaledY(edgeOnOutline.getBlack().getY() + edgeOnOutline.getDirectionY() + offsetY);

			((Graphics2D) g).setStroke(new BasicStroke(WIDTH_OUTLINE));
			g.setColor(edgeColor);
			g.drawLine(startX, startY, targetX, targetY);
			
			return edgeOnOutline.getBlack();
		}
		
		private Vertex paintEdgeOnOutlineCornerBased(Graphics g, Outline outline, OutlineEdge edgeOnOutline, Vertex blackBefore) {
			Color edgeColor = outline.isOuter() ? COLOR_OUTER_OUTLINE : COLOR_INNER_OUTLINE;

			((Graphics2D) g).setStroke(new BasicStroke(WIDTH_OUTLINE));
			g.setColor(edgeColor);
			drawLine(g, blackBefore, edgeOnOutline.getBlack());
			
			return edgeOnOutline.getBlack();
		}

		private void paintPolygons(Graphics g) {
			// System.out.println("Polygons:");

			g.setColor(COLOR_POLYGON);
			((Graphics2D) g).setStroke(new BasicStroke(WIDTH_POLYGON));
			for (Vector2D[] polygon : polygons) {
				// System.out.println();
				for (int i = 0; i < polygon.length; i++) {
					// System.out.print(polygon[i]);
					Vertex a = polygon[i].getA();
					Vertex b = polygon[i].getB();

					drawDot(g, a);
					drawDot(g, b);
					drawLine(g, a, b);
				}
			}
		}

		private void drawDot(Graphics g, Vertex a) {			
			int radius = DIAMETER_POLYGON_DOT / 2;
			
			int ovalX = this.calcScaledX(a.getX() + offsetX) - radius;
			int ovalY = this.calcScaledY(a.getY() + offsetY) - radius;
			
			g.drawOval(ovalX, ovalY, DIAMETER_POLYGON_DOT, DIAMETER_POLYGON_DOT);			
		}

		protected void drawLine(Graphics g, Vertex a, Vertex b) {
			int startX = this.calcScaledX(a.getX() + offsetX);
			int startY = this.calcScaledY(a.getY() + offsetY);
			int targetX = this.calcScaledX(b.getX() + offsetX);
			int targetY = this.calcScaledY(b.getY() + offsetY);

			g.drawLine(startX, startY, targetX, targetY);
		}

		private int calcScaledX(int x) {
			return (int) ((((x / this.image.getWidth()) * 100) + x) * zoom);
		}

		private int calcScaledY(int y) {
			return (int) ((((y / this.image.getHeight()) * 100) + y) * zoom);
		}

		public Dimension getPreferredSize() {
			if (image != null)
				return new Dimension((int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom));
			else
				return new Dimension(100, 60);
		}
	}
}