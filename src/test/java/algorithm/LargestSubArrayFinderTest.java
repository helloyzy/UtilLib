package algorithm;

import static org.junit.Assert.*;

import org.junit.Test;


public class LargestSubArrayFinderTest {

	@Test
	public void test() {
		
		LargestSubArrayFinder finder = new LargestSubArrayFinder();
		
		// normal case
		int[] arrayA = new int[] {-1, 0, 8, 9, -2, 1};
		assertEquals(17, finder.sumOnLargestSubArray(arrayA));
		
		// normal case 2
		int[] arrayB = new int[] {-1, -2, 3, 8, 7, -5, 6};
		assertEquals(19, finder.sumOnLargestSubArray(arrayB));
		
		// negative case
		int[] negativeArray = new int[] {-100, -2, -3, -8, -1, -11};
		assertEquals(-1, finder.sumOnLargestSubArray(negativeArray));
	}
	
}
