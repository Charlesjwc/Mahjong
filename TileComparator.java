import java.util.*;

/**	Comparator class for Tile object in Mahjong game. 
 * 	This class is mainly used for PriorityQueues that contain Tile 
 * 	objects as elements.
 * 	
 * 	@author	Charles Chang
 * 	@since	23 September 2024
 */
public class TileComparator implements Comparator<Tile> {
	//	No constructor because not field variables -- default is enough
	
	/**	Compares 2 tiles, returning the difference of suit of value
	 * 	@param	Tile 1 to compare
	 * 	@param	Tile 2 to compare
	 * 	@return	difference of suit in int or value if suits are same
	 */
	public int compare(Tile a, Tile b) {
		//	Assign value to suits
		int suitA = suitNum(a);
		int suitB = suitNum(b);
		//	Compare suit value
		if (suitA != suitB)
			return suitA - suitB;
		//	If suits are same, compare values
		else
			return a.getValue() - b.getValue();
	}
	
	/**	Assigns an int to the suit of the given tile
	 * 	1 for TONG, 2 for TIAO, 3 for WAN, 4 for SPEC
	 * 	Helper method for compare
	 * 	@param	tile to assign value of suit to
	 * 	@return int value of suit
	 */
	private int suitNum(Tile t) {
		//	Assign value to suits
		switch(t.getSuit()) {
			case Tile.SUIT.TONG:	return 1;
			case Tile.SUIT.TIAO:	return 2;
			case Tile.SUIT.WAN:		return 3;
			//	Default includes SPEC
			default:	return 4;
		}
	}
}
