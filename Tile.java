import java.util.*;

/**	Tile class for Mahjong game. Each Tile object represents a tile in the
 * 	mahjong game tiles with the same value or suit aren't the same tile
 * 	object since there will be multiple of them.
 * 	
 * 	@author Charles Chang
 * 	@since	22 September 2024
 */
public class Tile {
	//	Pubilc enumerated suits
	public static enum SUIT {TONG, TIAO, WAN, SPEC};
	//	Turn value of special suit into wind or dragon
	public static final String[] SPEC_TYPES = new String[]{"N", "E", "S", "W", "R", "G", "B"};
	
	/*	Field variables		*/
	//	Suit of tile object
	private SUIT suit;
	//	Value ot tile object (type of honor if suit is special)
	private int value;
	
	/*	Constructors	*/
	/**	Creates new Tile from suit and value
	 * 	@param	suit	type of suit of the tile
	 * 	@param	value	value of tile or type of SPEC between 1-9 and
	 * 					and 1-7 respectiveley
	 */
	public Tile(SUIT suit, int value) {
		this.suit = suit;
		this.value = value;
	}
	/**	Creates new Tile from key String (suit + value)
	 * 	@param	key		String in valid form suit + value
	 */
	public Tile(String key) {
		//	Breeak key into suit and value
		String s = key.substring(0, key.length() - 1);
		//	Assign Suit from String
		switch (s) {
			case "TONG": 	this.suit = SUIT.TONG;	break;
			case "TIAO": 	this.suit = SUIT.TIAO;	break;
			case "WAN":		this.suit = SUIT.WAN;	break;
			case "SPEC":	this.suit = SUIT.SPEC;	break;
		}
		//	Assign value from last char
		this.value = Integer.parseInt(key.substring(key.length() - 1));
	}
	
	/*	Accessor Methods	*/
	/**	@return	SUIT	suit field variable*/
	public SUIT getSuit() {
		return suit;
	}
	/**	@return	int		value field variable*/
	public int getValue() {
		return value;
	}
	
	/**	@return	String	Tile in key format*/
	public String getKey() {
		return "" + suit + value;
	}
	/**	overrides default toString with getKey method
	 * 	@return	String	Tile in ket format
	 */
	public String toString() {
		return getKey();
	}
	
	
	/*	Print Methods	*/
	/**	Prints the entire tile */
	public void print() {
		//	Top word
		String num = "";
		//	Bottom word
		String word = "";
		//	Handle special suits
		if (suit == SUIT.SPEC) {
			num = " ";
			word = SPEC_TYPES[value - 1];
		}
		//	Handle normal tiles
		else {
			num = String.valueOf(value);
			switch(suit) {
				case SUIT.TONG: word = "O";	break;
				case SUIT.TIAO: word = "|";	break;
				case SUIT.WAN: word = "K";	break;
			}
		}
		System.out.println("+-----+");
		System.out.println("|  " + num + "  |");
		System.out.println("|     |");
		System.out.println("|  " + word + "  |");
		System.out.println("+-----+");
	}
	
	/**	Prints the given layer of a tile
	 * 	@param	layer of tile to print
	 */
	public void print(int row) {
		//	Prints hard coded layer based off row
		switch (row) {
			//	First layer and last layer are same
			case 1:	case 5:	System.out.print("+-----+");	break;
			//	Second layer - get num then print it in format, whitespace if SPEC
			case 2: String num = (suit == SUIT.SPEC) ? " " : String.valueOf(value);
					System.out.print("|  " + num + "  |");	break;
			//	Third layer
			case 3:	System.out.print("|     |");	break;
			//	Fourth layer
			case 4:	
				//	If suit is special, print out the type of SPEC
				String word = "";
				if (suit == SUIT.SPEC)
					word = SPEC_TYPES[value - 1];
				//	Otherwise, print a symbol for the suit
				else 
					switch(suit) {
						//	O for TONG
						case SUIT.TONG: word = "O";	break;
						//	| for TIAO
						case SUIT.TIAO: word = "|";	break;
						//	K for WAN
						case SUIT.WAN: word = "K";	break;
					}
				System.out.print("|  " + word + "  |");		break;
		}
	}
	
	/**	Checks whether this Tile is equal in suit and value to another
	 * 	Tile using TileComparator.
	 * 	Used in checking for three of a kinds.
	 * 	@param	Tile to compare to
	 * 	@return	whether or not tiles are equal
	 */
	public boolean equals(Tile other) {
		return new TileComparator().compare(this, other) == 0;
	}
	
	
	/*	Static Methods	*/
	
	/**	Prints a list of Tiles layer by layer
	 * 	@param	List to print
	 */
	public static void printTileList(List<Tile> tiles) {
		//	Print layer by layer
		for (int i = 1; i <= 5; i++) {
			for (Tile t: tiles) {
				t.print(i);
				System.out.print("  ");
			}
			System.out.print("\n");
		}
	}
	
	/**	Prints a queue of tiles by converting queue to list and
	 * 	calling printTilesList
	 * 	@param	Queue of tiles
	 */
	public static void printTileQueue(Queue<Tile> tiles) {
		printTileList(toList(tiles));
	}
	/**	Converts a queue of tilse to list
	 * 	@param	queue of tiles
	 * 	@return	list of tiles
	 */
	public static List<Tile> toList(Queue<Tile> queue) {
		List<Tile> list = new ArrayList<>();
		while (!queue.isEmpty())
			list.add(queue.poll());
		return list;
	}
	
	/**	Prints a list of sets, grouped up
	 *	@param	list to print
	 */
	public static void printSetList(List<TileSet> list) {
		//	Print layer by layer
		for (int i = 1; i <= 5; i++) {
			for (TileSet set: list) {
				set.print(i);
				System.out.print("  ");
			}
			System.out.print("\n");
		}
	}
}
