import java.util.*;

/**	A class to test Mahjong game, and classes for the Mahjong game. This
 * 	class is not used in the Mahjong game.
 * 	
 * 	@author	Charles Chang
 * 	@since	22 September 2024
 */
public class Tester {
	public static void main(String[] args) {
		/**	Test for too many incomlete hands	*/
		Player p5 = new Bot(1);
		p5.draw(new Tile("TONG1"));
		p5.draw(new Tile("TONG1"));
		p5.draw(new Tile("TONG2"));
		p5.draw(new Tile("TONG2"));
		p5.draw(new Tile("TONG3"));
		p5.draw(new Tile("TONG3"));
		p5.draw(new Tile("TONG4"));
		p5.draw(new Tile("TONG4"));
		p5.draw(new Tile("TONG5"));
		p5.draw(new Tile("TONG5"));
		p5.hiddenChi();
		System.out.println("Hidden:");
		p5.printHidden();
		System.out.println("Shown:");
		p5.printShown();
		System.out.println("Junk:");
		p5.printJunk();
		
		/**	Test bot hidden hands
		Player p4 = new Bot(0);
		p4.draw(new Tile(Tile.SUIT.WAN, 3));
		p4.draw(new Tile(Tile.SUIT.WAN, 3));
		p4.draw(new Tile(Tile.SUIT.WAN, 3));
		p4.draw(new Tile(Tile.SUIT.WAN, 1));
		p4.draw(new Tile(Tile.SUIT.WAN, 1));
		p4.hiddenPeng();
		p4.peng(new Tile(Tile.SUIT.WAN, 1));
		p4.draw(new Tile("SPEC1"));
		p4.draw(new Tile("SPEC1"));
		p4.peng(new Tile("SPEC1"));
		System.out.println("Hidden:");
		p4.printHidden();
		System.out.println("Shown:");
		p4.printShown();
		*/
		
		/*	Test discard 
		Player p3 = new Player(3, false);
		p3.draw(new Tile(Tile.SUIT.WAN, 1));
		p3.draw(new Tile(Tile.SUIT.WAN, 2));
		p3.draw(new Tile(Tile.SUIT.WAN, 3));
		p3.draw(new Tile(Tile.SUIT.WAN, 4));
		p3.draw(new Tile(Tile.SUIT.WAN, 5));
		p3.draw(new Tile(Tile.SUIT.WAN, 6));
		p3.draw(new Tile(Tile.SUIT.WAN, 7));
		p3.draw(new Tile(Tile.SUIT.WAN, 8));
		p3.draw(new Tile(Tile.SUIT.WAN, 8));
		p3.draw(new Tile(Tile.SUIT.WAN, 9));
		
		p3.discard().print();
		p3.printHand();
		*/
		
		/*	Test Kong and CHI methods
		Player p2 = new Player(2, false);
		p2.draw(new Tile(Tile.SUIT.WAN, 4));
		p2.draw(new Tile(Tile.SUIT.TIAO, 5));
		p2.draw(new Tile(Tile.SUIT.WAN, 3));
		p2.draw(new Tile(Tile.SUIT.WAN, 4));
		p2.draw(new Tile(Tile.SUIT.WAN, 5));
		p2.draw(new Tile(Tile.SUIT.WAN, 6));
		p2.draw(new Tile(Tile.SUIT.SPEC, 1));
		p2.draw(new Tile(Tile.SUIT.SPEC, 1));
		p2.draw(new Tile(Tile.SUIT.SPEC, 1));
		p2.draw(new Tile(Tile.SUIT.TIAO, 2));
		p2.draw(new Tile(Tile.SUIT.TONG, 4));

		System.out.println(p2.canChi(new Tile("WAN7")));
		System.out.println(p2.canChi(new Tile("WAN6")));
		System.out.println(p2.canChi(new Tile("WAN5")));
		System.out.println(p2.canChi(new Tile("WAN4")));
		System.out.println(p2.canChi(new Tile("WAN3")));
		System.out.println(p2.canChi(new Tile("WAN2")));
		System.out.println(p2.canChi(new Tile("WAN1")));
		
		p2.kong(new Tile("SPEC1"));
		p2.chi(new Tile("WAN5"));
		p2.printShown();
		p2.printHand();
		*/
		
		
		/*	Test player hasWon() method
		Player p1 = new Player(1, false);
		p1.draw(new Tile(Tile.SUIT.TONG, 1));
		p1.draw(new Tile(Tile.SUIT.TONG, 2));
		p1.draw(new Tile(Tile.SUIT.TONG, 3));
		p1.draw(new Tile(Tile.SUIT.TONG, 7));
		p1.draw(new Tile(Tile.SUIT.TIAO, 5));
		p1.draw(new Tile(Tile.SUIT.TIAO, 5));
		p1.draw(new Tile(Tile.SUIT.TONG, 7));
		p1.draw(new Tile(Tile.SUIT.TONG, 7));
		p1.draw(new Tile(Tile.SUIT.SPEC, 4));
		p1.draw(new Tile(Tile.SUIT.SPEC, 4));
		p1.draw(new Tile(Tile.SUIT.SPEC, 4));
		p1.draw(new Tile(Tile.SUIT.WAN, 8));
		p1.draw(new Tile(Tile.SUIT.WAN, 9));
		p1.draw(new Tile(Tile.SUIT.WAN, 7));
		
		System.out.print(p1.hasWon());
		*/
		
		/*	Test handsuit isWinning() method
		Queue<Tile> hand1 = new PriorityQueue<>(new TileComparator());
		hand1.add(new Tile(Tile.SUIT.TONG, 1));
		hand1.add(new Tile(Tile.SUIT.TONG, 2));
		hand1.add(new Tile(Tile.SUIT.TONG, 3));
		hand1.add(new Tile(Tile.SUIT.TONG, 5));
		hand1.add(new Tile(Tile.SUIT.TONG, 5));
		hand1.add(new Tile(Tile.SUIT.TONG, 7));
		hand1.add(new Tile(Tile.SUIT.TONG, 7));
		hand1.add(new Tile(Tile.SUIT.TONG, 7));
		
		HandSuit test1 = new HandSuit(hand1, Tile.SUIT.TONG);
		System.out.println(test1.isWinning(true));
		hand1.poll();
		hand1.poll();
		hand1.poll();
		
		HandSuit test2 = new HandSuit(hand1, Tile.SUIT.TONG);
		System.out.println(test2.isWinning(true));
		
		hand1.poll();
		hand1.poll();
		hand1.poll();
		
		HandSuit test3 = new HandSuit(hand1, Tile.SUIT.TONG);
		System.out.println(test3.isWinning(true));
		
		Queue<Tile> hand2 = new PriorityQueue<>(new TileComparator());
		hand1.add(new Tile(Tile.SUIT.TONG, 7));
		hand1.add(new Tile(Tile.SUIT.TONG, 7));
		HandSuit test4 = new HandSuit(hand2, Tile.SUIT.TONG);
		System.out.println(test4.isWinning(true));
		*/
	}
}
