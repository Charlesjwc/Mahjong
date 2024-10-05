import java.util.*;

/**	Bot class extends Player to simulate other players in Mahjong.
 * 	Overrides discard method to automatically discard something rather
 * 	than prompt the player.
 * 	The entirety of the Bot's hand is represented a second time in hidden
 * 	and handjunk.
 * 	
 * 	@author	Charles Chang
 * 	@since	2 October 2024
 * 	
 *	Bot keeps track of hidden incomplete sets that it will be trying to fill
 */

public class Bot extends Player {
	/*	Field variables		*/
	//	Hidden complete and incomplete sets for bot to keep track of
	//	There should never be more than 4 sets in heres
	//	Complete sets will be added to the start of the list,
	//	Incomplete sets will be added to the back of the list
	List<TileSet> hidden;
	
	//	Junk tiles to be discarded or made into incomplete/complete hands
	Queue<Tile> handJunk;
	
	//	Constructor with playerNum
	public Bot (int playerNum) {
		//	Bot is a player with isBot == true
		super(playerNum, true);
		//	Initialize bot field variables
		hidden = new ArrayList<>();
		handJunk = new PriorityQueue<>(new TileComparator());
	}
	
	
	//	Override draw method
	/**	Draw card, but also update handJunk
	 * 	@param	Tile to add to hand
	 */
	public void draw(Tile t) {
		super.draw(t);
		handJunk.add(t);
	}
	
	//	New discard method
	/**	Discard a tile decided by hidden sets
	 * 	@return	Tile discarded
	 */
	public Tile discard() {
		//	Update hidden tiles with junk Tiles
		updateHand();
		
		//	Choose a random tile from discard
		int randomTile = (int)(Math.random() * handJunk.size());
		
		//	Temp queue containing all tiles in hand junk
		Queue<Tile> temp = getHandJunk();
		//	Reconstruct newhand
		Queue<Tile> newHand = new PriorityQueue<>(new TileComparator());
		
		//	Create new queue without discarded tile
		for (int i = 1; i < randomTile; i++)
			newHand.add(temp.poll());
		
		Tile discarded = temp.poll();
		
		while (!temp.isEmpty())
			newHand.add(temp.poll());
		
		//	Update handjunk with updated queue without discard
		handJunk = newHand;
		
		//	Delete card from hand in super
		handDelete(discarded);
		
		//	Return discarded tile
		return discarded;
	}
	
	
	/**	Update hidden hand based on tiles in junk	*/
	public void updateHand() {
		//	Finish incomplete sets
		finishIncomplete();
		
		//	Form incomplete chi and pengs or complete ones if able
		hiddenPeng();
		hiddenChi();
		
		//	Check for extra sets (if shown sets and hidden/incomplete sets > 4)
		checkExtra();
	}
	
	
	/**	PENG or KONG tiles from hand into hidden if possible */
	public void hiddenPeng() {
		//	See of there are any sets of 3 of the same tile
		Queue<Tile> temp = getHandJunk();
		HashMap<String, Integer> tileCount = new HashMap<>();
		
		while (!temp.isEmpty()) {
			Tile t = temp.poll();
			tileCount.put(t.toString(), tileCount.getOrDefault(t.toString(), 0) + 1);
		}
		
		//	If there is a 3 of a kind, add it to a set
		//	If there is a 2 of a kind, add it to an incomplete set
		temp = new PriorityQueue<>(new TileComparator());
		while (!handJunk.isEmpty()) {
			if (tileCount.get(handJunk.peek().toString()) == 4) {
				hidden.add(0, new TileSet(handJunk.poll(), handJunk.poll(), handJunk.poll(), handJunk.poll()));
			}
			else if (tileCount.get(handJunk.peek().toString()) == 3) {
				hidden.add(0, new TileSet(handJunk.poll(), handJunk.poll(), handJunk.poll()));
			}
			else if (tileCount.get(handJunk.peek().toString()) == 2) {
				hidden.add(new IncompleteSet(handJunk.poll(), handJunk.poll()));
			}
			else {
				temp.add(handJunk.poll());
			}
		}
		
		//	Replace handjunk
		handJunk = temp;
	}
	
	/**	CHI tiles from junk into hidden if possible
	 * 	Assume there are no pairs in junk as it is assumed hiddenPeng
	 * 	is called before hiddenChi
	 * 	Prioritize forming sets of 3 over 2 sets of 2 in the case of 4
	 * 	consecutive tiles
	 */
	public void hiddenChi() {
		//	Replacement queue for handjunk
		Queue<Tile> replace = new PriorityQueue<>(new TileComparator());
		//	Move tiles from junk to replacement or a TileSet
		while (!handJunk.isEmpty()) {
			//	Current tile
			Tile current = handJunk.poll();
			
			//	Current tile is special, add to replacement then continue;
			if (current.getSuit() == Tile.SUIT.SPEC) {
				replace.add(current);
				continue;
			}
			
			Tile next = handJunk.peek();
			//	Next tile
			if (next == null) {
				replace.add(current);
				break;
			}
			
			//	If next tile is this tile + 1 and suits are the same
			if (current.getSuit() == next.getSuit() && current.getValue() + 1 == next.getValue()) {
				//	Poll tile
				next = handJunk.poll();
				//	Check for third
				Tile third = handJunk.peek();
				//	Check if a full tile set can be formed
				if (third != null && current.getSuit() == third.getSuit() && current.getValue() + 2 == third.getValue()) {
					hidden.add(0, new TileSet(current, next, third));
					handJunk.poll();
					continue;
				}
				//	Otherwise form an incomplete set
				else {
					hidden.add(new IncompleteSet(current, next));
					continue;
				}
			}
			//	If next tile is this tile + 2 and suits are the same
			if (current.getSuit() == next.getSuit() && current.getValue() + 2 == next.getValue()) {
				//	Poll tile
				next = handJunk.poll();
				//	Form an incomplete set
				hidden.add(new IncompleteSet(current, next));
				continue;
			}
			//	If tiles 1 and 2 arent consecutive or with 1 tile gap, keep current in junk
			else
				replace.add(current);
		}
		//	Replace handjunk which is now empty wiht replacement
		handJunk = replace;
	}
	
	/**	Finish incomplete sets with tiles in handjunk if possible */
	public void finishIncomplete() {
		//	Check all hidden sets
		for (int i = 0; i < hidden.size(); i++) {
			TileSet ts = hidden.get(i);
			//	If the hidden set is incomplete, check if the hand contains
			//	A tile to complete it
			if (ts instanceof IncompleteSet) {
				//	Rebuild handjunk as original is used
				Queue<Tile> temp = new PriorityQueue<>(new TileComparator());
				
				//	Check for tiles to complete this set
				while (!handJunk.isEmpty()) {
					Tile t = handJunk.poll();
					//	If tile completes set, remove incomplete set and
					//	add a complete set and dont add back to junk
					if (ts.isNeeded(t)) {
						//	Update ts to complete set
						ts = ts.complete(t);
					}
					//	Otherwise add it to temp
					else
						temp.add(t);
				}
				//	Replace handjunk
				handJunk = temp;
				
				//	Update ts into hidden
				//	If ts has been completed, remove i from hidden then add
				//	the complete set to the beginning
				if (!(ts instanceof IncompleteSet)) {
					hidden.remove(i);
					hidden.add(0, ts);
				}
			}
		}
		
	}
	
	/**	Checks whether there are too many hidden hands
	 * 	Deletes any incomplete sets over the limit and turns the tiles
	 * 	into junk tiles
	 */
	public void checkExtra() {
		//	If handsize is too big, there is an error elsewhere and this method is pointless
		if (getHand().size() > 14) {
			System.out.println("ERROR: Hand Size > 14. Can't check for extra hidden.");
			return;
		}
		//	While there are too many hidden sets, keep junking them
		while (getShown().size() + hidden.size() > 4) {
			TileSet ts = hidden.remove(hidden.size() - 1);
			junkSet(ts);
		}
	}
	
	/**	Turns current set into hand junk
	 * 	Assumes t1 and t2 arent null, and t3 and t4 are null
	 * 	@param	TileSet to convert to junk
	 */
	public void junkSet(TileSet ts) {
		List<Tile> tileList = ts.getTiles();
		for (Tile t: tileList)
			handJunk.add(t);
	}
	
	/**	Checks if bot can KONG the tile being discarded
	 * 	@param	Tile to check if takable
	 * 	@return Whether or not tile can be KONG
	 */
	public boolean canKong(Tile discard) {
		//	If discard is null, can't KONG
		if (discard == null)
			return false;
		//	Find a full hidden PENG set
		for (TileSet ts: hidden) {
			if (!(ts instanceof IncompleteSet) && ts.getSetType() == 
				TileSet.SET_TYPE.PENG && ts.contains(discard))
				return true;
		}
		return false;
	}
	/**	KONG the given tile with tiles in hand into shown
	 * 	Precondition: canKong
	 * 	@param	Tile to KONG
	 */
	public void kong(Tile discard) {
		//	Announce the kong
		System.out.println("KONG: Player " + getPlayerNum());
		//	Find a full hidden PENG set
		for (int i = 0; i < hidden.size(); i++) {
			//	Current tileset
			TileSet ts = hidden.get(i);
			//	If current tileset is a complete PENG set, turn it into a KONG set
			if (!(ts instanceof IncompleteSet) && ts.getSetType() == TileSet.SET_TYPE.PENG) {
				hidden.remove(i);
				ts.kong(discard);
				addShownSet(ts);
				return;
			}
		}
	}
	
	/**	Checks if player can PENG the tile being discarded without
	 * 	assuming the player before is throwing the tile
	 * 	@param	Tile to check if takable
	 * 	@return Whether or not the tile can be PENG
	 */
	public boolean canPeng(Tile discard) {
		//	Find an incomplete chi tile set that needs the discarded tile
		for (TileSet ts: hidden) {
			if (ts.getSetType() == TileSet.SET_TYPE.PENG && ts.isNeeded(discard))
				return true;
		}
		return false;
	}
	/*	Override peng kong and chi	*/
	/**	PENG the given tile with tiles in hand into shown
	 * 	Precondition: canPeng
	 * 	@param	Tile to PENG
	 */
	public void peng(Tile discard) {
		//	Announce the peng
		System.out.println("PENG: Player " + getPlayerNum());
		//	Complete the first incomplete PENG set this tile can complete
		for (int i = 0; i < hidden.size(); i++) {
			TileSet ts = hidden.get(i);
			//	If discard completes incomplete chi
			if (ts.getSetType() == TileSet.SET_TYPE.PENG && ts.isNeeded(discard)) {
				//	Remove incomplete set
				hidden.remove(i);
				//	Add complete set
				addShownSet(ts.complete(discard));
				return;
			}
		}
	}
	
	/**	Checks if player can CHI the tile being discarded without
	 * 	assuming the player before is throwing the tile
	 * 	@param	Tile to check if takable
	 * 	@return Whether or not the tile can be CHI
	 */
	public boolean canChi(Tile discard) {
		//	Find an incomplete chi tile set that needs the discarded tile
		for (TileSet ts: hidden) {
			if (ts.getSetType() == TileSet.SET_TYPE.CHI && ts.isNeeded(discard))
				return true;
		}
		return false;
	}
	
	/**	CHI the given tile with tiles in hand into shown
	 * 	Precondition: canChi
	 * 	@param	Tile to CHI
	 */
	public void chi(Tile discard) {
		//	Announce the chi
		System.out.println("CHI: Player " + getPlayerNum());
		//	Complete the first incomplete CHI set this tile can complete
		for (int i = 0; i < hidden.size(); i++) {
			TileSet ts = hidden.get(i);
			//	If discard completes incomplete chi
			if (ts.getSetType() == TileSet.SET_TYPE.CHI && ts.isNeeded(discard)) {
				//	Remove incomplete set
				hidden.remove(i);
				//	Add complete set
				addShownSet(ts.complete(discard));
				return;
			}
		}
	}
	
	/*	Accessor methods, returns copy when applicable	*/
	/**	@return copy of hidden	*/
	public List<TileSet> getHidden() {
		return new ArrayList<>(hidden);
	}
	/**	@return copy of handJunk	*/
	public Queue<Tile> getHandJunk() {
		return new PriorityQueue<>(handJunk);
	}
	
	/*	Extra print methods for testing/troubleshooting	*/
	/**	Prints hidden sets	*/
	public void printHidden() {
		for (TileSet ts: hidden) {
			ts.print();
			System.out.println("");
		}
	}
	/**	Prints junk tiles	*/
	public void printJunk() {
		Tile.printTileQueue(getHandJunk());
	}
}
