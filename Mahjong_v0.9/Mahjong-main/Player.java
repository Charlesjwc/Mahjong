import java.util.*;
import java.util.concurrent.TimeUnit;

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
	
	/**	Discard a tile prompted by the user
	 * 	@return	Tile discarded
	 */
	public Tile discard() {
		//	Keep prompting user until valid discard is chosen
		int discardIndex = 0;
		//	Print hand
		System.out.println("\n\nYour hand:");
		//	Number the tiles in hand
		for (int i = 0; i < hand.size(); i++) {
			System.out.print("   " + i + "    ");
			if (i < 10)
				System.out.print(" ");
		}
		System.out.println("");
		printHand();
		//	Get selection from user
		System.out.println("");
		discardIndex = Prompt.getInt("Which tile would you like to discard? "
				+ "(Tiles start from 0)", -1, hand.size() - 1);
		
		if (discardIndex == -1) {
			System.out.println("\nPROGRAM EXITED\n");
			System.exit(0);
		}
		
		//	Discard selected tile
		//	Create temp array to hold tiles
		List<Tile> temp = new ArrayList<>();
		for (int i = 0; i < discardIndex; i++) {
			temp.add(hand.poll());
		}
		//	Discard the actual tile
		Tile discarded = hand.poll();
		
		//	Return temp to hand
		while (!temp.isEmpty()) {
			hand.add(temp.remove(0));
		}
		
		return discarded;
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
	/**	Check if with the new tile, the current hand can be winning
	 * 	Assume shown is filled with valid sets
	 * 	Call after drawing a tile and before discarding
	 */
	public boolean hasWon(Tile finalTile) {
		//	Store original hand
		Queue<Tile> originalHand = getHand();
		//	Draw tile to hand
		draw(finalTile);
		//	Check if hand with final tile is winning
		boolean isWon = hasWon();
		//	Revert to original hand ("undraw" the tile)
		hand = originalHand;
		return isWon;
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
		//	Announce the kong
		System.out.println("KONG: Player " + getPlayerNum());
		try {
			//	Sleep 1 second
			TimeUnit.SECONDS.sleep(1);
		}
		catch (Exception e) {}
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
		//	Add new KONG set to shownimport java.util.concurrent.TimeUnit;
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
		//	Announce the peng
		System.out.println("PENG: Player " + getPlayerNum());
		try {
			//	Sleep 1 second
			TimeUnit.SECONDS.sleep(1);
		}
		catch (Exception e) {}
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
		if (discard.getSuit() == Tile.SUIT.SPEC)
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
	public void chi(Tile discard) {
		//	Announce the chi
		System.out.println("CHI: Player " + getPlayerNum());
		try {
			//	Sleep 1 second
			TimeUnit.SECONDS.sleep(1);
		}
		catch (Exception e) {}
		
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
			if (below2 != null && temp.equals(below2) && !hasBelow2) {
				hasBelow2 = true;
				below2 = temp;
			}
			else if (below1 != null && temp.equals(below1) && !hasBelow1) {
				hasBelow1 = true;
				below1 = temp;
			}
			else if (above1 != null && temp.equals(above1) && !hasAbove1) {
				hasAbove1 = true;
				above1 = temp;
			}
			else if (above2 != null && temp.equals(above2) && !hasAbove2) {
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
		
		//	Add all set options
		List<TileSet> chiOptions = new ArrayList<TileSet>();
		if (canLowerChi)
			chiOptions.add(new TileSet(below2, discard, below1));
		if (canMiddleChi)
			chiOptions.add(new TileSet(below1, discard, above1));
		if (canUpperChi)
			chiOptions.add(new TileSet(above1, discard, above2));
		
		//	If only one option, automatically add it to shown
		if (chiOptions.size() == 1) {
			//	Add set to shown
			shown.add(chiOptions.get(0));
			//	Replace hand
			this.hand = newHand;
			return;
		}
		
		//	Prompt used for which chi to take if more than one option and player isnt bot
		//	Message with valid sets
		
		int n = 0;
		//	Keep selection at 0 if is bot, otherwize, let user select
		int selection = 0;
		if (!isBot()) {
			System.out.println("Please choose the sequence you would like to make:\n");
			for (TileSet set: chiOptions) {
				System.out.println("Option " + n + ":");
				for (int i = 1; i <= 5; i++) {
					set.print(i);
					System.out.println("");
				}
				n++;
			}
			//	Get selection from user
			selection = Prompt.getInt("Which set should be made?", 0, 
													chiOptions.size());
		}
		
		//	Add selected set to shown
		shown.add(chiOptions.get(selection));
		
		//	Other unused tiles returned to hand
		//	Reuse booleans to check whether each tile should be added back
		//	to hand. If null or the added set, it should not be added to hand.
		hasBelow2 = below2 == null ? false : !chiOptions.get(selection).hasTile(below2);
		hasBelow1 = below1 == null ? false : !chiOptions.get(selection).hasTile(below1);
		hasAbove1 = above1 == null ? false : !chiOptions.get(selection).hasTile(above1);
		hasAbove2 = above2 == null ? false : !chiOptions.get(selection).hasTile(above2);
		
		//	Return tiles to new hand
		if (hasBelow2)
			newHand.add(below2);
		if (hasBelow1)
			newHand.add(below1);
		if (hasAbove1)
			newHand.add(above1);
		if (hasAbove2)
			newHand.add(above2);
		//	Replenish hand
		hand = newHand;
	}
	
	/*	Bot commands	*/
	/**	Bot discards a card based on sets it can form
	 * 	TEMPORARILY DISCARDS FIRST CARD
	 * 	@return	Tile discarded
	 */
	public Tile botDiscard() {
		return hand.poll();
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
	/**	@return whether or not player is bot*/
	public boolean isBot() {
		return isBot;
	}
	
	/**	@return whether this player is equal to another	*/
	public boolean equals(Player other) {
		return this.getPlayerNum() == other.getPlayerNum();
	}
}
