import java.util.*;

/**	HandSuit class in Mahjong game to help check for winning combinations
 * 	of Tile objects. Each HandSuit will contain all the cards of a certain
 * 	suit in a players hand. Because tiles have to be the same suit in order
 * 	to score, using HandSuits reduces the amount of incorrect combinations.
 * 
 * 	@author	Charles Chang
 * 	@since	26 September 2024
 */
public class HandSuit {
	//	Keep track of all of the tiles of a suit in a hand
	private final Queue<Tile> tiles;
	private Tile.SUIT type;
	
	//	Constructor
	public HandSuit(Queue<Tile> suitTiles, Tile.SUIT suitType) {
		tiles = suitTiles;
		type = suitType;
	}
	
	//	Accesors
	public int getSuitCount() {
		return tiles.size();
	}
	
	public Tile.SUIT getSuitType() {
		return type;
	}
	
	//	Check whether suit tiles have right # of tiles for any number of only sets 	
	public boolean isCountRight() {
		return getSuitCount() % 3 == 0;
	}
	//	Check whether suit tiles have right # of tilse for any number of sets + exactly 1 pair
	public boolean isCountRightPair() {
		return getSuitCount() % 3 == 2;
	}
	//	Check whether this hand contains only sets, if boolean is true, also check for pair
	public boolean isWinning(boolean shouldPair) {
		//	If suit is spec, only check for sets of same numbers
		if (getSuitType() == Tile.SUIT.SPEC)
			return checkSpecial(shouldPair);
		//	Try all combinations of sets that could contain the first tile
		//	Queue to list
		List<Tile> tileTemp = Tile.toList(new PriorityQueue<Tile>(this.tiles));
		//	Call helper
		return isWinningRecurse(shouldPair, tileTemp);
	}
	//	Helper method
	private boolean isWinningRecurse(boolean shouldPair, List<Tile> tiles) {
		if (tiles.size() == 0)
			return true;
		//	Handle case if last pair
		if (shouldPair && tiles.size() == 2) {
			//	System.out.println("final pair: " + tiles.get(0) + tiles.get(1));
			
			return tiles.get(0).equals(tiles.get(1));
		}
		//	Handle general pair
		else if (shouldPair && tiles.get(0).equals(tiles.get(1))) {
			//	Remove pair from array
			Tile temp1 = tiles.remove(0);
			Tile temp2 = tiles.remove(0);
			//	See if combo works
			boolean result = isWinningRecurse(false, tiles);
			//	Replace array
			tiles.add(0, temp1);
			tiles.add(1, temp2);
			//	Only return if true, otherwise, continue with triples
			if (result) {
				//	System.out.println("found pair: " + temp1 + temp2);
				
				return true;
			}
		}
		
		//	No pairs
		Tile t1 = tiles.get(0);
		int i1 = 0;
		Tile t2 = tiles.get(1);
		int i2 = 1;
		Tile t3 = tiles.get(2);
		int i3 = 2;
		
		try {
			//	Check if i1 and i3 are out of range for a set
			while (t3.getValue() <= t1.getValue() + 2) {
				//	Check if all 3 are the same
				if (t1.equals(t2) && t2.equals(t3)) {
					//	Remove set and test without this set
					tiles.remove(i3);
					tiles.remove(i2);
					tiles.remove(i1);
					boolean output = isWinningRecurse(shouldPair, tiles);
					//	Replace tils then return or adjust
					tiles.add(i1, t1);
					tiles.add(i2, t2);
					tiles.add(i3, t3);
					//	Only return if true, otherwise increment second and third by 2
					if (output) {
						// System.out.println("found 3 of kind" + t1);
						
						return true;
					}
					i2 += 2;
					i3 += 2;
					t2 = tiles.get(i2);
					t3 = tiles.get(i3);
				}
				//	Check if first 2 are the same
				else if (t1.equals(t2)) {
					i2++;
					i3++;
					t2 = tiles.get(i2);
					t3 = tiles.get(i3);
				}
				//	Check if last 2 are the same
				else if (t2.equals(t3)) {
					i3++;
					t3 = tiles.get(i3);
				}
				//	Otherwise 3 tiles are consecutive since first and last 
				//	differ by 2 at most and all 3 tiles are different
				else {
					//	Remove 3 tiles and return won or lost as no more
					//	valid triple to include first tile
					tiles.remove(i3);
					tiles.remove(i2);
					tiles.remove(i1);
					boolean result = isWinningRecurse(shouldPair, tiles);
					//	Replace tiles
					tiles.add(i1, t1);
					tiles.add(i2, t2);
					tiles.add(i3, t3);
					
					//	if (result)
					//		System.out.println("found sequence: " + t1 + t2 + t3);
					
					return result;
				}
			}
		}
		catch (IndexOutOfBoundsException e) {
			return false;
		}
		
		return false;
	}
	//	Return nth index of false -1 if doesn't exist
	private int falseInd(List<Boolean> list, int ind) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == false)
				ind--;
			if (ind == 0)
				return i;
		}
		return -1;
	}
	//	Check specials for only 3 of a kind and pairs if arg is true
	private boolean checkSpecial(boolean shouldPair) {
		Queue<Tile> tiles = new PriorityQueue<Tile>(this.tiles);
		if (!shouldPair)
			//	Check all triplets
			while (!tiles.isEmpty()) {
				Tile t1 = tiles.poll();
				Tile t2 = tiles.poll();
				Tile t3 = tiles.poll();
				if (t1.equals(t2) && t2.equals(t3)) {}
				else
					return false;
			}
		else {
			//	Check all triplets, considering pairs
			while (tiles.size() > 2) {
				Tile t1 = tiles.poll();
				Tile t2 = tiles.poll();
				Tile t3 = tiles.poll();
				//	t1 has to equal t2
				if (!t1.equals(t2))
					return false;
				//	Check t2 and t3 to see if trip or pair
				else {
					if (!t2.equals(t3)) {
						tiles.add(t3);
					}
				}
			}
			//	Check for leftover 2
			if (tiles.size() == 0)
				return true;
			return tiles.poll().equals(tiles.poll());
		}
		return true;
	}
}
