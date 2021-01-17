package assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Run {
	public static ArrayList<String> pathArr = new ArrayList<String>();
	static String shortestPath="";
	static double previusAns=Integer.MAX_VALUE;

	public static void main(String[] args) throws FileNotFoundException {
		/**
		 * Assigment 3 Code
		 * @author Ece Ozaydin
		 * @since 16.04.2020
		 *
		 */
		//1: Reading the input file to convert it to the Place object and transfer them to the array list named Places
		File file = new File("txt/data3.txt");
		Scanner input = new Scanner(file);
		ArrayList<Place> Places = new ArrayList<Place>();
		int placeType;
		while (input.hasNextLine()) {
			String line= input.nextLine();
			String[] lineArray;
			lineArray = line.split(",");
			if(lineArray.length == 3) {
				//This means it is Migros row
				placeType = 1;
			}else {
				placeType = 2;
			}
			Places.add(new Place(placeType, Double.parseDouble(lineArray[0]), Double.parseDouble(lineArray[1])));

		}
		input.close();



		// n is the number of nodes i.e. V //for graph
		int n = Places.size(); 

		double[][] graph = new double[n][n];
		int i=0;
		//Calculate the distance between two coordinates by pulling data from files
		for(i=0; i<n; i++) {
			double x1, y1, x2, y2;
			x1 = Places.get(i).x;
			y1 = Places.get(i).y;
			for(int j=0; j<n; j++) {
				if(i == j) {
					graph[i][j] = 0;
				}else {
					x2 = Places.get(j).x;
					y2 = Places.get(j).y;
					graph[i][j] = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
				}
			}
		}

		//boolean array to check if it has been visited before 
		boolean[] v = new boolean[n]; 

		// Mark 0th node as visited 
		v[0] = true; 
		// ans is the minimum path Hamiltonian Cycle 
		double ans = Integer.MAX_VALUE; 

		// Find the minimum path Hamiltonian Cycle 
		ans = travellerSalesman(graph, v, 0, n, 1, 0, ans, ""); 


		//shortestPath = "0-1, 0-2, 2-1, 1-3, 1-4, 1-5, 1-6, 1-7, 7-3, 7-4, 7-5, 5-3, 5-4, 5-6, 5-8, 5-9, 9-3, 3-4, 3-6, 3-8, 8-4, 8-6, 8-10, 8-11, 11-4, 11-6, 11-10, 10-4, 4-6,";
		int counter=0;
		String text = "";
		int index=0;
		pathArr.add("0");
		String firstChar = shortestPath.substring(shortestPath.indexOf("-")+1, shortestPath.indexOf(","));
		//System.out.println(firstChar);
		if(shortestPath.contains(", 0-")) {
			text = ", 0-";
			index = shortestPath.lastIndexOf(text);
			firstChar = shortestPath.substring(index + text.length(), shortestPath.indexOf(",", index+text.length()));
		}

		while(counter < n-1) {
			counter++;
			pathArr.add(firstChar);
			//System.out.println(firstChar);
			text = ", " + firstChar + "-";
			index = shortestPath.lastIndexOf(text);
			firstChar = shortestPath.substring(index + text.length(), shortestPath.indexOf(",", index+text.length()));
		}

		pathArr.add("0");
		//System.out.println(pathArr);

		//Plotting on the screen using StdDrawv
		StdDraw.setCanvasSize(500, 500);
		int [] outputPath = new int[n];
		for(i=0; i<pathArr.size()-1; i++) {
			Place activePlace, nextPlace= new Place();
			double x1, y1, x2, y2;
			//System.out.println();
			activePlace = Places.get(Integer.parseInt(pathArr.get(i)));
			x1 = activePlace.x;
			y1 = activePlace.y;
			nextPlace = Places.get(Integer.parseInt(pathArr.get(i+1)));
			x2 = nextPlace.x;
			y2 = nextPlace.y;
			StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
			StdDraw.line(x1, y1, x2, y2);
			int activeIndex;

			activeIndex = Integer.parseInt(pathArr.get(i)) + 1;
			outputPath[i] = activeIndex;
			if(activePlace.placeType == 1) {
				StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
				StdDraw.filledCircle(x1, y1, 0.03);
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.text(x1, y1, Integer.toString(activeIndex));

			}else {
				StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
				StdDraw.filledCircle(x1, y1, 0.02);
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.text(x1, y1, Integer.toString(activeIndex));

			}
		}
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.text(0.3, 0.02, "Distance:"+Double.toString(ans));
		StdDraw.show();
		/**
		 *  provided the desired output image
		 *  @param:activePlace:current location
		 *  @param:bestPath:output image
		 */
		ArrayList<String> bestPath = new ArrayList<String>();
		for(i=0; i<pathArr.size()-1; i++) {
			Place activePlace= new Place();
			activePlace = Places.get(Integer.parseInt(pathArr.get(i)));
			if(activePlace.placeType == 1) {
				//if migros set as start point
				for(int j=0; j<pathArr.size()-1;j++) {
					bestPath.add(Integer.toString(outputPath[(i+j) % n]));
				}
				bestPath.add(Integer.toString(outputPath[i]));
				break;
			}
		}

		//Print outputs
		System.out.println("Best Path:" + bestPath); 
		System.out.println("Distance:" + Double.toString(ans)); 
	}



	/**
	 *Function to find the minimum path 
	 * Hamiltonian Cycle 
	 * @param:cost is path
	 * @currentPos graph ways
	 * @insidePaths is intermediate roads
	 */
	//travellerSalesman(graph, v, 0, n, 1, 0, ans, ""); 
	static double travellerSalesman(double[][] graph, boolean[] v,int currPos, int n,  
			int count, double cost, double ans, String insidePaths)  
	{ 
		//if it reaches the last node and has a connection to the start node
		//if the previous line is larger than now, ans = current value.

		if (count == n && graph[currPos][0] > 0)  
		{ 
			ans = Math.min(ans, cost + graph[currPos][0]);
			if(ans < previusAns) {
				previusAns = ans;
				shortestPath = insidePaths;
				//System.out.println(shortestPath);
			}
			return ans; 
		} 

		// BACKTRACKING STEP 
		// Loop created to find the proper path with branch and bound
		//when count increasing and currPoss node is cross
	for (int i = 0; i < n; i++)  
		{ 
			if (v[i] == false && graph[currPos][i] > 0)  
			{ 
				// Mark as visited 
				v[i] = true; 
				insidePaths += currPos + "-" +  i + ", ";
				ans = travellerSalesman(graph, v, i, n, count + 1,cost + graph[currPos][i], ans, insidePaths);
			
				v[i] = false; 
			}
		} 
		return ans; 
	} 	

}
