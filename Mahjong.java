import java.util.*;
import java.util.concurrent.TimeUnit;

/** Chinese Mahjong 13 tiles game that runs in Powershell. This version
 * 	doesn not count score or use flower cards.
 * 
 * 	@author	Charles Chang
 * 	@since	22 September 2024
 * 
 * 	There are 4 players, each with 13 tiles. Each turn, a player draws a
 * 	tile and discards a tile. If after drawing, a player has 4 sets of 3
 * 	tiles and one pair of 2 tiles, they win. A "set" here is defined as 
 * 	having a three of a kind (3 of the exact same tile) or having 3 tiles
 * 	of the same suit and consecutive numbers, i.e. 1, 2, 3; 2, 3, 4; etc.
 * 	A pair here is defined as having 2 of the exact same tile.
 * 
 * 
 * 	There are a total of 136 tiles in 4 suits: 
 * 	TONG (circle), TIAO (sticks/bamboo), WAN (10,000), and Honors
 * 	
 * 	For the suits TONG, TIAO, and WAN, there are tiles numbered 1-9
 * 	Honors on the other hand have North, East, South, and West winds,
 * 	as well as Red, Green, and White dragons. There are 4 of every Tile.
 * 	This means 36 TONG + 36 TIAO + 36 WAN + 28 HONORS = 136.
 * 	
 * 
 * 	In a normal turn, the player to the "right" of the previous player
 * 	draws a tile from the center. However, other players make start their
 * 	turn -- sometimes regardless of order -- if they can do one of 3 things:
 * 
 * 	PENG - A player ends their turn by discarding a tile, and another player
 * 	can use that tile to form a three of a kind. The other player, regardless
 * 	of position may take the tile from the discard and treat it as their
 * 	draw tile. They now discard to end their turn.
 * 	
 * 	KONG - Same thing as peng, but instead, if there are 3 tiles in the
 * 	player's hands, and they want to make a four of a kind, they can take
 * 	the tile out of the discard to form their four of a kind. They then
 * 	get to take a normal turn after, as this four of a kind still counts
 * 	as a single set and it is treated as a regular set. However, the
 * 	player draws from the back in the case of a KONG.
 * 	
 * 	CHI = A player ends their turn by discarding a tile, and the player
 * 	to the right can use that tile to form a sequence. THe player right
 * 	after the player who just discarded can take that tile from the discard
 * 	and treat it as their draw tile. Now discard to end their turn.
 * 
 * 	Note that in the case of PENG and CHI, players are always taking one
 * 	tile and discarding one tile. After doing any of these 3 actions, the
 * 	set (recall that quadruplets count as sets) is revealed face up and
 * 	cannot be discarded, or used to form a different set. There are always
 * 	13 tiles in between turns in all players' hands and revealed tiles
 * 	(once again treating a quadruplet as 3 tiles).
 * 	Also note that PENG and KONG always overrides CHI if one player wants
 * 	to PENG/KONG and another wants to CHI the same discarded tile.
 * 
 * 	A KONG may also occur when a player with a revealed PENG set draws a
 * 	tile from the center that can for a quadruplet with the PENG set.
 * 	This card is added to the PENG to make a KONG, and a tile is drawn
 * 	from the back, as the tile count stays at 13 otherwise.
 * 	A KONG may not occur with a player adding a discarded tile with a 
 * 	PENG set to make a KONG set: only tiles drawn from the center may be
 * 	added to a revealed PENG set.
 * 
 * 	The game ends when a player win with a hand of 4 sets and a pair. The
 * 	sets may be hidden in your hand, or shown as a PENG, KONG, or CHI.
 * 
 * 
 * 	Code notes:
 * 	- Chinese characters may have trouble loading in Powershell, thus enlish
 * 	letters are used instead. O represents TONG as it means circle, |
 * 	represents TIAO as it means sticks, K represents WAN although K
 * 	technically represents 1,000 instead of 10,000 that WAN represents.
 * 	Each of the Winds are replaced with the first letter of the direction:
 * 	N for North, E for East, S for South, W for West. Because W isn't used
 * 	for WAN because of West. Dragons are represented by the first letter
 * 	of their color - R for Red, G for Green, B for Blank/White.	The characters
 * 	themselves don't matter as SPEC suit Tils don't have values and are 
 * 	only matched with other tiles with the same character.
 * 	- Honors in the code are treated mostly the same as the other suits,
 * 	with each honor being stored as a number in the suit SPEC for special.
 * 	Only when checking for winning hands are they are treated differently.
 * 	- A TileComparator class is used as a Comparator for PriorityQueues
 * 	instead of Tiles extending Comparable. Because of this, all PQ's
 * 	that contain tiles have to use this Comparator in its parameter to
 * 	correctly sort tiles.
 */
public class Mahjong {
	//	All tiles in a deck of Mahjong
	private static final String[] ALL_TILES = new String[]{
		"TONG1", "TONG2", "TONG3", "TONG4", "TONG5", "TONG6", "TONG7", "TONG8", "TONG9", 
		"TIAO1", "TIAO2", "TIAO3", "TIAO4", "TIAO5", "TIAO6", "TIAO7", "TIAO8", "TIAO9", 
		"WAN1", "WAN2", "WAN3", "WAN4", "WAN5", "WAN6", "WAN7", "WAN8", "WAN9", 
		"SPEC1", "SPEC2", "SPEC3", "SPEC4", "SPEC5", "SPEC6", "SPEC7",
		"TONG1", "TONG2", "TONG3", "TONG4", "TONG5", "TONG6", "TONG7", "TONG8", "TONG9", 
		"TIAO1", "TIAO2", "TIAO3", "TIAO4", "TIAO5", "TIAO6", "TIAO7", "TIAO8", "TIAO9",
		"WAN1", "WAN2", "WAN3", "WAN4", "WAN5", "WAN6", "WAN7", "WAN8", "WAN9",
		"SPEC1", "SPEC2", "SPEC3", "SPEC4", "SPEC5", "SPEC6", "SPEC7",
		"TONG1", "TONG2", "TONG3", "TONG4", "TONG5", "TONG6", "TONG7", "TONG8", "TONG9",
		"TIAO1", "TIAO2", "TIAO3", "TIAO4", "TIAO5", "TIAO6", "TIAO7", "TIAO8", "TIAO9", 
		"WAN1", "WAN2", "WAN3", "WAN4", "WAN5", "WAN6", "WAN7", "WAN8", "WAN9",
		"SPEC1", "SPEC2", "SPEC3", "SPEC4", "SPEC5", "SPEC6", "SPEC7",
		"TONG1", "TONG2", "TONG3", "TONG4", "TONG5", "TONG6", "TONG7", "TONG8", "TONG9",
		"TIAO1", "TIAO2", "TIAO3", "TIAO4", "TIAO5", "TIAO6", "TIAO7", "TIAO8", "TIAO9",
		"WAN1", "WAN2", "WAN3", "WAN4", "WAN5", "WAN6", "WAN7", "WAN8", "WAN9",
		"SPEC1", "SPEC2", "SPEC3", "SPEC4", "SPEC5", "SPEC6", "SPEC7"
	};
	
	/*	Field variables	*/
	//	Deck of tiles to draw from, has a front and back to take from
	private ArrayDeque<Tile> deck;
	//	Stack of discarded tiles
	private Deque<Tile> discardPile;
	//	Current turn, player index to play
	private int turn;
	//	4 Players
	private Player[] players;
	
	/*	Constructors	*/
	/**	No args default constructor */
	public Mahjong() {
		//	Initiallize field variables
		deck = new ArrayDeque<>();
		discardPile = new ArrayDeque<>();
		players = new Player[4];
		
		//	Add all players to player array
		players[0] = new Player(0, false);
		players[1] = new Player(1, true);
		players[2] = new Player(2, true);
		players[3] = new Player(3, true);
	}
	
	//	Main method
	public static void main(String[] args) {
		Mahjong game = new Mahjong();
		game.setup();
		game.run();
		
	}
	
	/**	Setup method:
	 * 	Copies all the tiles into the deck in a random order.
	 * 	Then distributes tiles to the players in this fashion:
	 * 	Each player draws 4 tiles going in a circle.
	 * 	Repeat 2 more times until 12 tiles in hand.
	 * 	Each player draws single tile.
	 */
	private void setup() {
		//	Set up deck
		//	Create instance of ALL_TILES as ArrayList
		List<String> unshuffled = new ArrayList<>();
		for (String tile: ALL_TILES) {
			unshuffled.add(tile);
		}
		//	Shuffle and add unshuffled tiles to deck
		while (!unshuffled.isEmpty()) {
			//	Pick a random tile from unshiffled tiles
			int randInd = (int)(Math.random() * unshuffled.size());
			//	Add tile to deck, creating Tile object with key in unshuffled
			String key = unshuffled.remove(randInd);
			deck.add(new Tile(key));
		}
		
		//	Distribute tiles to players
		//	Distribute in fours to twelve
		for (int i = 0; i < 3; i++) {
			//	Each player draws 4
			for (Player player: players) {
				player.draw(deck.pollFirst());
				player.draw(deck.pollFirst());
				player.draw(deck.pollFirst());
				player.draw(deck.pollFirst());
			}
			try {
				//	Sleep 1 second to simulate others drawing
				TimeUnit.SECONDS.sleep(1);
			}
			catch (Exception e) {}
			//	Print player's hand as they draw
			players[0].printHand();
		}
		//	Distribute 13th
		for (Player player: players)
			player.draw(deck.pollFirst());
		try {
				TimeUnit.SECONDS.sleep(1);
			}
			catch (Exception e) {}
		
		//	Print final hand after drawing last tile
		players[0].printHand();
	}
	
	/**	Run method:
	 * 	Game runs as sepcified until a player wins or all tiles in the deck
	 * 	run out, resulting in a draw;
	 */
	private void run() {
		//	Random starting player
		//turn = (int)(Math.random() * 4);
		turn = 0;
		
		//	Remember if a player wins or if game ends in a draw
		boolean isGameWon = false;
		
		//	Keep track of drawn/discarded tile
		Tile drawTile = deck.poll();
		
		
		//	Keep game running until player wins or tiles run out
		//	Each loop starts with a discard and ends with a draw
		while (!isGameWon && !deck.isEmpty()) {
			//	Current player
			Player thisPlayer = players[turn];
			
			//	If player is not a bot, print their hand and all shown sets
			if (!thisPlayer.isBot()) {
				//	Print other players' shown sets
				for (Player p: players) {
					if (!p.equals(thisPlayer)) {
						System.out.println("Player " + p.getPlayerNum() + 
										"\'s shown tiles:");
						p.printShown();
					}
				}
				//	Print own shown set
				System.out.println("\nYour shown tiles:");
				thisPlayer.printShown();
			}
			
			//	Check if with this 14th tile, the game is won
			if (drawTile != null && thisPlayer.hasWon(drawTile)) {
				//	Ask if the player wants to win right now
				int input = Prompt.getInt("Would you like to win? (0 - NO, 1 - YES)", 0, 1);
				//	If yes, print winscreen and end the game
				if (input == 1) {
					isGameWon = true;
					printWinScreen(thisPlayer);
					return;
				}
			}
			
			//	Take player or bot turn, then update drawTile to discard
			if (players[turn].isBot()) {
				drawTile = takeBotTurn(players[turn], drawTile);
			}
			else {
				drawTile = takePlayerTurn(players[turn], drawTile);
				//	If null is returned, keep drawing from last and taking turns until not null
				while(drawTile == null)
					drawTile = takePlayerTurn(players[turn], deck.pollLast());
			}
			
			//	Print discarded tile
			System.out.println("Tile discarded:");
			drawTile.print();
			try {
				//	Sleep 1 second to simulate others drawing
				TimeUnit.SECONDS.sleep(1);
			}
			catch (Exception e) {}
			
			//	Check for KONG, then PENG, then CHI from other players
			//	If a player decides to PENG, or CHI, drawTile becomes
			//	null going into the next turn, prompting Player to discard
			//	If a player decides to KONG, add the shown set, but also draw
			//	If no action is made, a tile is drawn and turn is incremented
			
			//	KONG checks
			for (Player p: players) {
				//	If p is a bot and can KONG, do it
				if (p.canKong(drawTile)) {
					if (p.isBot()) {
						p.kong(drawTile);
						drawTile = null;
						turn = p.getPlayerNum() - 1;
						break;
					}
					//	Otherwise ask the player if they would like to if it wasn't their turn
					else if (!p.equals(thisPlayer)) {
						int input = Prompt.getInt(
							"Would you like to KONG discarded tile? (0 - NO, 1 - YES)", 0, 1);
						if (input == 1) {
							p.kong(drawTile);
							drawTile = null;
							turn = p.getPlayerNum() - 1;
							break;
						}
					}
				}
			}
			
			//	PENG checks if drawTile isn't already null
			if (drawTile != null)
				for (Player p: players) {
					//	If p is a bot and can PENG, do it
					if (p.canPeng(drawTile)) {
						if (p.isBot()) {
							p.peng(drawTile);
							drawTile = null;
							turn = p.getPlayerNum() - 1;
							break;
						}
						//	Otherwise ask the player if they would like to if it wasn't their turn
						else if (!p.equals(thisPlayer)) {
							int input = Prompt.getInt(
								"Would you like to PENG discarded tile? (0 - NO, 1 - YES)", 0, 1);
							if (input == 1) {
								p.peng(drawTile);
								drawTile = null;
								turn = p.getPlayerNum() - 1;
								break;
							}
						}
					}
				}
			
			//	CHI checks if drawTile isn't already null
			if (drawTile != null) {
				Player pNext = players[(turn) % 4];
				if (pNext.canChi(drawTile)) {
					//	If player is bot, chi
					if (pNext.isBot()) {
						pNext.chi(drawTile);
						drawTile = null;
						turn = pNext.getPlayerNum() - 1;
					}
					//	Otherwise ask the player if they would like to if it wasn't their turn
					else if (!pNext.equals(thisPlayer)) {
						int input = Prompt.getInt(
							"Would you like to CHI discarded tile? (0 - NO, 1 - YES)", 0, 1);
						if (input == 1) {
							pNext.chi(drawTile);
							drawTile = null;
							turn = pNext.getPlayerNum() - 1;
							break;
						}
					}
				}
			}
			
			//	If drawTile still isn't null, add it to discard, and update
			//	it with a new Tile from the deck
			if (drawTile != null) {
				discardPile.push(drawTile);
				drawTile = deck.poll();
			}
			
			//	Increment turn
			turn ++;
			turn %= 4;
			
			//	End of turn --
			//	Player should have taken a tile by PENG, KONG, CHI, or drawing by now
		}
		
		//	Draw message if no winner
		if (!isGameWon)
			System.out.println("DRAW: Out of tiles");
	}
	
	/**	Prints the discard pile*/
	private void printDiscard() {
		Deque<Tile> temp = new ArrayDeque<>(discardPile);
		while(!temp.isEmpty()) {
			List<Tile> row = new ArrayList<>(10);
			int i = 0;
			while (!temp.isEmpty() && i < 10) {
				row.add(temp.pop());
			}
			Tile.printTileList(row);
		}
	}
	
	/**	Player takes a turn
	 * 	@param	Player to take turn
	 * 	@param	Tile taken, if null, only discard
	 * 	@return	Tile discarded
	 */
	private Tile takePlayerTurn(Player p, Tile t) {
		//	If tile is null, a PENG KONG or CHI happened, and only discard
		if (t != null) {
			//	If player can kong, ask if they want to
			if (p.canKong(t)) {
				int input = Prompt.getInt(
					"Do you want to KONG drawn tile? (0 - NO, 1- YES", 0, 1);
				//	If player KONG
				if (input == 1) {
					p.kong(t);
					return null;
				}
			}
			//	Add the tile to hand
			p.draw(t);
		}
		
		//	Discard tile
		return p.discard();
	}
	
	/**	Bot takes a turn
	 * 	TEMPORARILY DISCARDS DRAWN TILE OR RANDOMY DISCARDS IF NULL
	 *	@param	Bot to take turn
	 * 	@param	Tile taken, if null, only discard
	 * 	@return til discarded
	 */
	private Tile takeBotTurn(Player p, Tile t) {
		if (t != null)
			return t;
		return p.botDiscard();
	}
	
	/**	Prints the winscreen for the winning player	
	 * 	@param	Player who won
	 */
	private void printWinScreen(Player winner) {
		
	}
}	
