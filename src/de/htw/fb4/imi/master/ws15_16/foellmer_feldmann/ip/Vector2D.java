/**
 * Image Processing WiSe 2015/16
 *
 * Authors: Markus Föllmer, Sascha Feldmann
 */
package de.htw.fb4.imi.master.ws15_16.foellmer_feldmann.ip;

/**
 * [SHORT_DESCRIPTION] 
 *
 * @author Sascha Feldmann <sascha.feldmann@gmx.de>
 * @since 30.11.2015
 */
public class Vector2D {

	private final int x;
	private final int y;
	
	private final Vertex a;
	private final Vertex b;
	
	public Vector2D(int x, int y, Vertex a, Vertex b) {
		this.x = x;
		this.y = y;
		
		this.a = a;
		this.b = b;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public Vertex getA() {
		return a;
	}
	
	public Vertex getB() {
		return b;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2D other = (Vector2D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vector2D [getX()=");
		builder.append(getX());
		builder.append(", getY()=");
		builder.append(getY());
		builder.append(", getA()=");
		builder.append(getA());
		builder.append(", getB()=");
		builder.append(getB());
		builder.append("]");
		return builder.toString();
	}
}
