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
	 * 	If this is an Incomplete set, t3 is null
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
		//	Only print t3 if its not null
		if (t3 != null)
			t3.print(layer);
		//	Only print t4 if set is a KONG
		if (setType == SET_TYPE.KONG)
			t4.print(layer);
	}
	
	/**	Prints the entire tileSet	*/
	public void print() {
		for (int i = 1; i <= 5; i++) {
			print(i);
			System.out.println("");
		}
	}
	
	/**	Return whether this set contains a certain tile
	 * 	@param	Tile to check for
	 * 	@return	Whether the set contains the tile
	 */
	public boolean hasTile(Tile t) {
		return t.equals(t1) || t.equals(t2) || t.equals(t3);
		//	No need to check t4, as it will be the same as other tiles if not null
	}
	
	/**	@return	List of all non null tiles in set	*/
	public List<Tile> getTiles() {
		List<Tile> tileList = new ArrayList<>();
		tileList.add(t1);
		tileList.add(t2);
		if (t3 != null)
			tileList.add(t3);
		if (t4 != null)
			tileList.add(t4);
		return tileList;
	}
	
	/*	Methods for incompleteSet to implement	*/
	
	/**	Get needed tile to finish set
	 * 	Method for Incomplete Set to implement
	 * 	If TileSet isn't an incomplete set, it's already complete and
	 * 	returns null as no tiles are needed.
	 * @return	null
	 */
	public List<Tile> getNeededTiles() {
		return null;
	}
	
	/**	Returns whether tile is needed to complete set
	 * 	Since set is complete returns false
	 * 	@return	false
	 */
	public boolean isNeeded(Tile extra) {
		return false;
	}
	
	/**	Ccompletes set using tile t
	 * 	For complete sets, do nothing
	 */
	public TileSet complete(Tile lastTile) {
		if (t3 == null) {
			return new TileSet(t1, lastTile, t2);
		}
		else
			return this;
	}
	
	/**	@return whether or not TileSet contains the @param tile	*/
	public boolean contains(Tile t) {
		if (t.equals(t1))
			return true;
		if (t.equals(t2))
			return true;
		if (t.equals(t3))
			return true;
		if (t.equals(t4))
			return true;
		return false;
	}
	
	/**	@return set type	*/
	public SET_TYPE getSetType() {
		return setType;
	}
}
