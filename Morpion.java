package gomoku;

import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author vladimirlouis
 */
public class Morpion implements Comparable<Morpion>, Externalizable {
	private MorpionType morpionType;
	private Point coords;

	/**************************************************************************************************/
	public Morpion() {
		coords = new Point();
	}

	/**************************************************************************************************/
	public Morpion(MorpionType morpionType, Point coords) {
		assert morpionType != null : "MorpionType null";
		assert coords != null : "Coords is null";

		this.morpionType = morpionType;
		this.coords = coords;

	}

	/**************************************************************************************************/
	public MorpionType getMorpionType() {
		return morpionType;
	}

	/**************************************************************************************************/
	public Point getLocation() {
		return new Point(coords.x, coords.y);
	}

	/**************************************************************************************************/
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Morpion) || o == null) {
			return false;
		} else {
			Morpion temp = (Morpion) o;
			return this.coords.x == temp.getLocation().x && this.coords.y == temp.getLocation().y
					&& this.morpionType == temp.getMorpionType();
		}
	}

	/**************************************************************************************************/
	@Override
	public int compareTo(Morpion morpion) {

		assert morpion != null : "Morpion is null";

		if (coords.x < morpion.getLocation().x)
			return -1;

		if (coords.x > morpion.getLocation().x)
			return 1;

		if (coords.y < morpion.getLocation().y) {
			return -1;
		}

		if (coords.y > morpion.getLocation().y) {
			return 1;
		}

		return 0;
	}

	/**************************************************************************************************/
	/* @Override
	 public int hashCode(){ int hash=7; hash=71*hash+
	  Objects.hashCode(this.morpionType); hash=71*hash+
	  Objects.hashCode(this.coords);
	 
	  hash=71*hash+ Objects.hashCode(this.gameNumber); 
	  
	  return hash;
	  
	 } 
	 *******************************************************************************************************/
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeObject(morpionType);
		out.writeObject(coords);

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		morpionType = (MorpionType) in.readObject();
		coords = (Point) in.readObject();

	}
	/********************************************************************************************************/
}
