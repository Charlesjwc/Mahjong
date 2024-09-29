import java.util.*;

/**	Player class in Mahjong game. Player class keeps track of one of 4
 * 	players in the game. Each player has a player number, a hand of hidden
 * 	tiles, a list of shown tiles, and may be a bot.
 * 	
 * 	@author	Charles Chang
 * 	@since	22 September 2024
 */
public class Player {
	//	Field variables
	//	NEVER PASS hand IN AS PARAMETER, ALWAYS USE getHand() to make copy
	private Queue<Tile> hand;
	//	NEVER PASS shown IN AS PARAMETER, ALWAYS USE getShown() to make copy
	private List<TileSet> shown;
	//	Player number, same as index in array of players in Mahjong class
	private int playerNum;
	//	Whether or not this player is a bot
	private boolean isBot;
	
	/*	Constructors	*/
	/**	Player constructor with only player number
	 * 	Assume plaer is a bot
	 * 	@param	player number
	 */
	public Player(int playerNumber) {
		hand = new PriorityQueue<Tile>(new TileComparator());
		shown = new ArrayList<TileSet>();
		playerNum = playerNumber;
		isBot = true;
	}
	/**	Player constructor with player number and isBot input
	 * 	@param	player number
	 * 	@param	whether or not this player is a bot
	 */
	public Player(int playerNumber, boolean isBot) {
		hand = new PriorityQueue<Tile>(new TileComparator());
		shown = new ArrayList<TileSet>();
		playerNum = playerNumber;
		this.isBot = isBot;
	}
	
	/**	Draw a tile by adding the tile to hand
	 * 	@param	tile to draw
	 */
	public void draw(Tile t) {
		hand.offer(t);
	}
	
	/**	Prints hand	*/
	public void printHand() {
		Tile.printTileQueue(getHand());
	}
	/**	Prints shown tiles	*/
	public void printShown() {
		Tile.printSetList(getShown());
	}
	
	/**	Check if current hand can be winning
	 * 	Assume shown filled with valid sets
	 * 	Only call after drawing a card and before discarding
	 * 	Also used for checking if a discard can be used to win the game
	 */
	public boolean hasWon() {
		//	Create four queues for each suit and move hand into these queues
		Queue<Tile> hand = getHand();
		//	Queue for each suit
		Queue<Tile> tongs = new PriorityQueue<>(new TileComparator());
		Queue<Tile> tiaos = new PriorityQueue<>(new TileComparator());
		Queue<Tile> wans = new PriorityQueue<>(new TileComparator());
		Queue<Tile> specs = new PriorityQueue<>(new TileComparator());
		//	Adding hand to each suit queue
		while (!hand.isEmpty() && hand.peek().getSuit() == Tile.SUIT.TONG) {
			tongs.add(hand.poll());
		}
		while (!hand.isEmpty() && hand.peek().getSuit() == Tile.SUIT.TIAO) {
			tiaos.add(hand.poll());
		}
		while (!hand.isEmpty() && hand.peek().getSuit() == Tile.SUIT.WAN) {
			wans.add(hand.poll());
		}
		while (!hand.isEmpty() && hand.peek().getSuit() == Tile.SUIT.SPEC) {
			specs.add(hand.poll());
		}
		
		//	Turn queues into HandSuit objects
		HandSuit tongSuit = new HandSuit(tongs, Tile.SUIT.TONG);
		HandSuit tiaoSuit = new HandSuit(tiaos, Tile.SUIT.TIAO);
		HandSuit wanSuit = new HandSuit(wans, Tile.SUIT.WAN);
		HandSuit specSuit = new HandSuit(specs, Tile.SUIT.SPEC);
		
		//	Keep track of number of suits that could be winning
		int numGoodSuits = 0;
		int numGoodSuitsPairs = 0;
		//	Add 1 to numGoodSuits if there the right amount of tiles to make
		//	any number of sets of 3's
		if (tongSuit.isCountRight())
			numGoodSuits++;
		if (tiaoSuit.isCountRight())
			numGoodSuits++;
		if (wanSuit.isCountRight())
			numGoodSuits++;
		if (specSuit.isCountRight())
			numGoodSuits++;
		//	Add 1 to numGoodSuitsPairs if there are the right amout of tiles
		//	to make any number of sets of 3's and exactly 1 pair
		if (tongSuit.isCountRightPair())
			numGoodSuitsPairs++;
		if (tiaoSuit.isCountRightPair())
			numGoodSuitsPairs++;
		if (wanSuit.isCountRightPair())
			numGoodSuitsPairs++;
		if (specSuit.isCountRightPair())
			numGoodSuitsPairs++;
		//	There can only be one pair
		if (numGoodSuitsPairs != 1)
			return false;
		//	There has to be 3 good suits without pairs
		if (numGoodSuits + 1 != 4)
			return false;
		//	Check whether tiles in each suit can be fully used to make sets
		//	and a pair if it should contain one
		return tongSuit.isWinning(tongSuit.isCountRightPair()) &&
				tiaoSuit.isWinning(tiaoSuit.isCountRightPair()) &&
				wanSuit.isWinning(wanSuit.isCountRightPair()) &&
				specSuit.isWinning(specSuit.isCountRightPair());
	}
	
	/*	Check if player can take tile from center	*/
	/**	Checks if player can KONG the tile being discarded
	 * 	@param	Tile to check if takable
	 * 	@return Whether or not tile can be KONG
	 */
	public boolean canKong(Tile discard) {
		//	Count how many copies of discard are in hand
		int copyCount = 0;
		Queue<Tile> hand = getHand();
		while (!hand.isEmpty())
			if (hand.poll().equals(discard))
				copyCount++;
		return copyCount == 3;
	}
	/**	KONG the given tile with tiles in hand into shown
	 * 	Precondition: canKong
	 * 	@param	Tile to KONG
	 */
	public void kong(Tile discard) {
		Queue<Tile> newHand = new PriorityQueue<>(new TileComparator());
		//	List of tiles to make into tileSet
		List<Tile> kongSet = new ArrayList<>();
		kongSet.add(discard);
		//	Remove tiles to be KONG from hand and add to list to be tileSet
		while (!hand.isEmpty()) {
			Tile temp = hand.poll();
			if (temp.equals(discard))
				kongSet.add(temp);
			else
				newHand.add(temp);
		}
		//	Replace hand without including tiles used
		hand = newHand;
		//	Add new KONG set to shown
		shown.add(new TileSet(kongSet));
	}
	
	/**	Checks if player can PENG the tile being discarded
	 * 	@param	Tile to check if takable
	 * 	@return Whether or not tile can be PENG
	 */
	public boolean canPeng(Tile discard) {
		//	Count how many copies of discard are in hand
		int copyCount = 0;
		Queue<Tile> hand = getHand();
		while (!hand.isEmpty())
			if (hand.poll().equals(discard))
				copyCount++;
		return copyCount >= 2;
	}
	/**	PENG the given tile with tiles in hand into shown
	 * 	Precondition: canPeng
	 * 	@param	Tile to PENG
	 */
	public void peng(Tile discard) {
		Queue<Tile> newHand = new PriorityQueue<>(new TileComparator());
		//	List of tiles to make into tileSet
		List<Tile> pengSet = new ArrayList<>();
		pengSet.add(discard);
		//	Remove tiles to be PENG from hand and add to list to be tileSet
		//	Do not take more than2 tiles from hand in case a kong is valid
		//	but not chosen
		while (!hand.isEmpty()) {
			Tile temp = hand.poll();
			if (temp.equals(discard) && pengSet.size() <= 2)
				pengSet.add(temp);
			else
				newHand.add(temp);
		}
		//	Replace hand without including tiles used
		hand = newHand;
		//	Add new KONG set to shown
		shown.add(new TileSet(pengSet));
	}
	
	/**	Checks if player can CHI the tile being discarded
	 * 	@param	Tile to check if takable
	 * 	@param	player throwing tile
	 * 	@return	Whether or not the tile can be CHI
	 */
	public boolean canChi(Tile discard, Player other) {
		if ((other.getPlayerNum() + 1) % 4 != this.getPlayerNum())
			return false;
		return canChi(discard);
	}
	/**	Checks if player can CHI the tile being discarded without
	 * 	assuming the player before is throwing the tile
	 * 	@param	Tile to check if takable
	 * 	@return Whether or not the tile can be CHI
	 */
	public boolean canChi(Tile discard) {
		//	If the tile is an honor, it cannot be CHI
		if (discard.getSuit == Tile.SUIT.SPEC)
			return false;
		//	Check hand for tiles with values 1 and 2 below and above
		Tile below2 = null;
		Tile below1 = null;
		Tile above1 = null;
		Tile above2 = null;
		if (discard.getValue() - 2 > 0)
			below2 = new Tile(discard.getSuit(), discard.getValue() - 2);
		if (discard.getValue() - 1 > 0)
			below1 = new Tile(discard.getSuit(), discard.getValue() - 1);
		if (discard.getValue() + 1 < 10)
			above1 = new Tile(discard.getSuit(), discard.getValue() + 1);
		if (discard.getValue() + 2 < 10)
			above2 = new Tile(discard.getSuit(), discard.getValue() + 2);
		
		//	Check which of the above the hand contains
		boolean hasBelow2 = false;
		boolean hasBelow1 = false;
		boolean hasAbove1 = false;
		boolean hasAbove2 = false;
		
		Queue<Tile> hand = getHand();
		while(!hand.isEmpty()) {
			Tile temp = hand.poll();
			if (below2 != null && temp.equals(below2))
				hasBelow2 = true;
			else if (below1 != null && temp.equals(below1))
				hasBelow1 = true;
			else if (above1 != null && temp.equals(above1))
				hasAbove1 = true;
			else if (above2 != null && temp.equals(above2))
				hasAbove2 = true;
		}
		
		//	True if combinations met, false otherwise
		if (hasBelow2 && hasBelow1)
			return true;
		if (hasBelow1 && hasAbove1)
			return true;
		if (hasAbove1 && hasAbove2)
			return true;
		
		return false;
	}
	/**	CHI the given tile with tiles in hand into shown
	 * 	Precondition: canChi
	 * 	@param	Tile to CHI
	 */
	public void chi(Tile t1) {
		//	If the tile is an honor, it cannot be CHI
		if (discard.getSuit == Tile.SUIT.SPEC)
			return false;
		//	Check hand for tiles with values 1 and 2 below and above
		Tile below2 = null;
		Tile below1 = null;
		Tile above1 = null;
		Tile above2 = null;
		if (discard.getValue() - 2 > 0)
			below2 = new Tile(discard.getSuit(), discard.getValue() - 2);
		if (discard.getValue() - 1 > 0)
			below1 = new Tile(discard.getSuit(), discard.getValue() - 1);
		if (discard.getValue() + 1 < 10)
			above1 = new Tile(discard.getSuit(), discard.getValue() + 1);
		if (discard.getValue() + 2 < 10)
			above2 = new Tile(discard.getSuit(), discard.getValue() + 2);
		
		//	Check which of the above the hand contains
		boolean hasBelow2 = false;
		boolean hasBelow1 = false;
		boolean hasAbove1 = false;
		boolean hasAbove2 = false;
		
		Queue<Tile> newHand = new PriorityQueue<>(new TileComparator());
		while(!hand.isEmpty()) {
			Tile temp = hand.poll();
			if (below2 != null && temp.equals(below2)) {
				hasBelow2 = true;
				below2 = temp;
			}
			else if (below1 != null && temp.equals(below1)) {
				hasBelow1 = true;
				below1 = temp;
			}
			else if (above1 != null && temp.equals(above1)) {
				hasAbove1 = true;
				above1 = temp;
			}
			else if (above2 != null && temp.equals(above2)) {
				hasAbove2 = true;
				above2 = temp;
			}
			else
				newHand.add(temp);
		}
		
		//	Check the combination of CHIs that are allowed
		//	Ask the player which CHI they would like to do
		//	Lower - hand contains lower 2, discard is high tile
		boolean canLowerChi = false;
		//	Middle - hand contains first and last, discard is middle
		boolean canMiddleChi = false;
		//	Upper - hand contains upper 2, discard is the low card
		boolean canUpperChi = false;
		
		if (hasBelow2 && hasBelow1)
			canLowerChi = true;
		if (hasBelow1 && hasAbove1)
			canMiddleChi = true;
		if (hasAbove1 && hasAbove2)
			canUpperChi = true;
	}
	
	
	/*	Accessors, returning copies of objects if applicable	*/
	/**	@return	a copy of hand queue*/
	public Queue<Tile> getHand() {
		return new PriorityQueue<Tile>(hand);
	}
	/**	@return	a copy of shown list*/
	public List<TileSet> getShown() {
		return new ArrayList<TileSet>(shown);
	}
	/**	@return playerNum field variable*/
	public int getPlayerNum() {
		return playerNum;
	}
}
