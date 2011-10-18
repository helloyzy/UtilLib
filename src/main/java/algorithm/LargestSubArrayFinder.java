package algorithm;

public class LargestSubArrayFinder {

	public int sumOnLargestSubArray(int[] array) {
		int sumOfSubArray = 0;
		int largestSumOfSubArray = array[0];
		for (int i : array) {
			if (sumOfSubArray <= 0) {
				sumOfSubArray = i;
			} else {
				sumOfSubArray += i;
			}
			if (sumOfSubArray >= largestSumOfSubArray) {
				largestSumOfSubArray = sumOfSubArray;
			}
		}
		return largestSumOfSubArray;
	}
	
}
