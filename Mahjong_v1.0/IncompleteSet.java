import java.util.*;

/**	Incomplete set extends TileSet, helping any Bot Players to choose
 * 	what tile needs to be discarded.
 * 	An incomplete set is either a pair or 2 consecutive tiles of the same suit
 * 	or the first and third tile of a sequene
 * 	Only implemented by bots.
 * 	Any methods where t3 is used should not be called until this IncompleteSet
 * 	becomes a TileSet. When this set is completed, it is deleted from 
 * 	the hidden hand of the bot using it and replaced by the full hand.
 * 
 * 	@author	Charles Chang
 * 	@since	2 October 2024
 */
public class IncompleteSet extends TileSet {
	//	Tile(s) needed to complete the set
	private List<Tile> need;
	
	/*	Constructors	*/
	public IncompleteSet(Tile t1, Tile t2) {
		//	Construct TileSet with third tile null
		super(t1, t2, null);
		
		//	Initiate need list
		need = new ArrayList<>();
		
		//	Get t3 needed
		//	Pair
		if (t1.equals(t2))
			need.add(new Tile(t1));
		//	Sequence
		else if (t1.getSuit() == t2.getSuit()) {
			//	Edge missing
			if (Math.abs(t1.getValue() - t2.getValue()) == 1) {
				need.add(new Tile(t1.getSuit(), Math.min(t1.getValue(), t2.getValue()) - 1));
				need.add(new Tile(t1.getSuit(), Math.max(t1.getValue(), t2.getValue()) + 1));
			}
			//	Center missing
			else if (Math.abs(t1.getValue() - t2.getValue()) == 2) {
				need.add(new Tile(t1.getSuit(), (t1.getValue() + t2.getValue()) / 2));
			}
		}
	}
	
	/**	need Accesor method	*/
	public List<Tile> getNeededTiles() {
		return new ArrayList<>(need);
	}
	/**	Check if given tile is a needed tile
	 * 	@param	Tile to check
	 * 	@return whether tile is needed
	 */
	public boolean isNeeded(Tile extra) {
		for (Tile t: need) {
			if (t.equals(extra))
				return true;
		}
		return false;
	}
}
