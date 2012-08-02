package quicksort;

import game.QuickSort;
import java.io.IOException;

class QuickSortApp {

	static void PrintArray(double[][] array) {
		int loop_counter = 0;

		for (loop_counter = 0; loop_counter < array.length; loop_counter++) {
			System.out.print(loop_counter + ":"
					+ Math.round(array[loop_counter][0]) + ":"
					+ Math.round(array[loop_counter][1]) + "\t");
		}
		System.out.println();
	}

	public static void main(String args[]) throws IOException {
		int size = 10;

		double[][] array = new double[size][2];
		for (int i = 0; i < size; i++) {
			array[i][0] = Math.random() * 100;
			array[i][1] = i;
		}
		
		QuickSort myQuickSort = new QuickSort();
		System.out.println("Array BEFORE quicksort");
		PrintArray(array);

		myQuickSort.sort(array, 0, size - 1);
		System.out.println("Array after sorting:");
		PrintArray(array);

	}
}