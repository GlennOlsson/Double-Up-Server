package Server.Backend.QuickSort;

import java.util.ArrayList;
import java.util.Random;

public class QuickSort<T extends Comparable<T>> {
	
	private Random random;
	
	private int K_VALUE = 20;
	
	public QuickSort(){
		random = new Random();
	}
	
	public void sort(ArrayList<T> v) {
		quickSort(v, 0, v.size());
	}
	
	private void quickSort(ArrayList<T> list, int lowerBound, int upperBound){
		if(upperBound - lowerBound > K_VALUE) {
			Pivot pivotIndex = partitionRandom(list, lowerBound, upperBound);
			
			quickSort(list, lowerBound, pivotIndex.lower);
			quickSort(list, pivotIndex.upper, upperBound);
		}
		else {
			insertSort(list, lowerBound, upperBound);
		}
	}
	
	/**
	 * Method that returns a Pivot object, used in the Quick Sort algorithms. This avoids duplication
	 * @param list the array
	 * @param lowerBound the lower bound of the array. This will also the chosen as the pivot
	 * @param upperBound the upper bound
	 * @return the Pivot object of the array
	 */
	public Pivot getFixedPartition(ArrayList<T> list, int lowerBound, int upperBound){
		T pivot = list.get(lowerBound);
		
		int low = lowerBound;
		int midIndex = low + 1;
		int high = upperBound;
		
		T a;
		
		while(midIndex < high){
			a = list.get(midIndex);
			
			if(a.compareTo(pivot) < 0){
				list.set(midIndex, list.get(low));
				list.set(low, a);
				low++;
				midIndex++;
			}
			else if(a == pivot){
				midIndex++;
			}
			else{
				high--;
				list.set(midIndex, list.get(high));
				list.set(high, a);
			}
		}
		
		return new Pivot(low, high);
	}
	
	/**
	 * A class that models a Pivot. The bounds are used in the Quick Sort method. You can access the fields from anywhere,
	 * but there are no getter methods
	 */
	static class Pivot{
		int lower;
		int upper;
		
		public Pivot(int lower, int upper){
			this.lower = lower;
			this.upper = upper;
		}
	}
	
	private Pivot partitionRandom(ArrayList<T> list, int lowerBound, int upperBound){
		int randomIndex = random.nextInt(upperBound - lowerBound) + lowerBound;
		
		T lowBefore = list.get(lowerBound);
		list.set(lowerBound, list.get(randomIndex));
		list.set(randomIndex, lowBefore);
		
		return getFixedPartition(list, lowerBound, upperBound);
	}
	
	/**
	 * Sorts the inout list, using insertion sorting
	 * @param list the list to be sorted
	 */
	public void insertSort(ArrayList<T> list, int lowerBound, int upperBound){
		for (int i = lowerBound + 1; i < upperBound; i++) {
			int j = i;
			while (j > 0 && list.get(j-1).compareTo(list.get(j)) > 0){
				T tempValue = list.get(j);
				list.set(j, list.get(j - 1));
				list.set(j - 1, tempValue);
				j--;
			}
		}
	}
	
	public static void main(String[] args) {
		int[] array = new int[]{5, 72, 81, 4, 67, 53, 15, 7, 19};
		QuickSort<Integer> quicksorter = new QuickSort<>();
		ArrayList<Integer> list = new ArrayList<>();
		
		for(int i : array){
			list.add(i);
		}
		
		System.out.print("Unsorted list: ");
		for(Integer integer : list){
			System.out.print(integer + ", ");
		}
		
		System.out.println();
		
		quicksorter.sort(list);
		
		System.out.print("Sorted list: ");
		for(Integer integer : list){
			System.out.print(integer + ", ");
		}
		
		System.out.println();
		
	}
	
}
