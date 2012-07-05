package game;

//An implementation of the Quick Sort algorithm using Java
//This code is based upon the example quick sort algorithm
//in Chapter 12, Java: How to program, Barry Holmes, 1998

public class QuickSort {

	// Swap method

	static void swap(double[][] array, int i, int j) {
		double temp = array[i][0];

		array[i][0] = array[j][0];

		array[j][0] = temp;

		double temp1 = array[i][1];

		array[i][1] = array[j][1];

		array[j][1] = temp1;
	}

	// Method to choose the pivot element in the array

	static int partition(double[][] array, int first, int last) {
		double pivot = array[first][0];
		int low = first;
		int high = last;

		while (low <= high) {
			while ((low <= last) && (array[low][0] <= pivot))
				low++;

			while (array[high][0] > pivot)
				high--;

			if (low < high) {
				swap(array, low, high);
				low++;
				high--;
			}
		}
		swap(array, first, high);
		return high;
	}

	// Quick sort method

	public static void sort(double[][] array, int first, int last) {
		int pivotIndex;

		if (first < last) {
			pivotIndex = partition(array, first, last);

			sort(array, first, pivotIndex - 1);
			sort(array, pivotIndex + 1, last);
		}
	}
}
