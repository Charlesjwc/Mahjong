import java.util.*;

/**	TileSet class to keep track of players' shown sets in Mahjong.
 * 	Shows PENG, KONG, and CHI sets.
 * 	
 * 	@author	Charles Chang
 * 	@since	28 September 2024
 */
public class TileSet {
	/*	Field variables	*/
	//	In the case of chi, t2 is the taken discard
	private Tile t1;
	private Tile t2;
	private Tile t3;
	//	In t4 remains null until Kong
	private Tile t4;
	//	Set types
	public static enum SET_TYPE {PENG, KONG, CHI};
	private SET_TYPE setType;
	
	/*	Constructors	*/
	/**	Constructor for CHI and PENG, assuming the three tiles are
	 * 	a valid CHI or PENG set.
	 * 	@param	tiles in the set
	 */
	public TileSet(Tile t1, Tile t2, Tile t3) {
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		if (t1.equals(t2))
			setType = SET_TYPE.PENG;
		else
			setType = SET_TYPE.CHI;
	}
	/**	Constructor for KONG without starting as PENG set
	 * 	@param 	tiles in the set
	 */
	public TileSet(Tile t1, Tile t2, Tile t3, Tile t4) {
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.t4 = t4;
		setType = SET_TYPE.KONG;
	}
	/**	Constructor for any set from arraylist of size 3 or 4
	 * 	If list is 4 tiles long, it is assumed to be a valid KONG
	 * 	If list is chi, it is assumed to be sorted from lowest to highest
	 * 	@param	list of tiles in the set
	 */
	public TileSet(List<Tile> list) {
		if (list.size() == 4) {
			t1 = list.remove(0);
			t2 = list.remove(0);
			t3 = list.remove(0);
			t4 = list.remove(0);
			setType = SET_TYPE.KONG;
		}
		else {
			t1 = list.remove(0);
			t2 = list.remove(0);
			t3 = list.remove(0);
			setType = t1.equals(t3) ? SET_TYPE.PENG : SET_TYPE.CHI;
		}
	}
	
	/**	Creates KONG set out of PENG
	 * 	@param	tile to complete KONG
	 * 	@return	whether KONG was valid or not
	 */
	public boolean kong(Tile t4) {
		if (setType == SET_TYPE.PENG && t1.equals(t4)) {
			this.t4 = t4;
			setType = SET_TYPE.KONG;
			return true;
		}
		else
			return false;
	}
	
	/**	Print tileSet line by line
	 * 	@param	line to print
	 */
	public void print(int layer) {
		t1.print(layer);
		t2.print(layer);
		t3.print(layer);
		if (setType == SET_TYPE.KONG)
			t4.print(layer);
	}
	
	/**	Return whether this set contains a certain tile
	 * 	@param	Tile to check for
	 * 	@return	Whether the set contains the tile
	 */
	public boolean hasTile(Tile t) {
		return t.equals(t1) || t.equals(t2) || t.equals(t3);
		//	No need to check t4, as it will be the same as other tiles if not null
	}
}
