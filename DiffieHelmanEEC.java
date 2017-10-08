import java.util.Scanner;
import java.util.Arrays;
import java.util.*;
import java.math.*;

class TableClass 
{
	private BigInteger x_Corr;
	
	private BigInteger y_Corr;
	
	public TableClass(BigInteger x, BigInteger y)
	{
		this.setX_Corr(x);
		this.setY_Corr(y);
	}

	public BigInteger getX_Corr() 
	{ return x_Corr; }

	public void setX_Corr(BigInteger x_Corr) 
	{ this.x_Corr = x_Corr; }

	public BigInteger getY_Corr() 
	{ return y_Corr; }

	public void setY_Corr(BigInteger y_Corr) 
	{ this.y_Corr = y_Corr; }

	public void printData()
	{ System.out.print("[ "+getX_Corr()+" , "+getY_Corr() + " ]  \t"); }
	
	public BigInteger[] getFirstPoint()
	{
		BigInteger result[] = new BigInteger[2];
		result[0] = getX_Corr();
		result[1] = getY_Corr();
		return result;
	}
	
} // and class TableClass
public class DiffieHelmanEEC
{
	/**
	* 1. 
	* 2. 
	* 3. 
	* 4. 
	* 5. 
	*/
	// make this publically available so its easier to add values
	public static ArrayList<ArrayList<TableClass>> master = new ArrayList<ArrayList<TableClass>>();
	public static ArrayList<ArrayList<TableClass>> master2 = new ArrayList<ArrayList<TableClass>>();
	public static long order;
	public static void main(String []args) 
	{
		System.out.println("\n********************************** EC Point Table ******************************************");
		System.out.println("");

		Scanner scan = new Scanner(System.in);

		System.out.print("\nEnter mod p: ");
		BigInteger mod_p = new BigInteger("7963");

		System.out.print("Enter a: ");
		BigInteger a_Number = new BigInteger("17");

		System.out.print("Enter b: \n");
		BigInteger b_Number = new BigInteger("9");	

		// A's private number
		System.out.print("Enter Xa: \n");
		BigInteger xA = new BigInteger("111");
		BigInteger a_PrivatePoint[] = new BigInteger[2];

		// B's private number
		System.out.print("Enter Xb: \n");
		BigInteger xB = new BigInteger("113");		
		BigInteger b_PrivatePoint[] = new BigInteger[2];


		System.out.println("a = " + a_Number + "\nb = " + b_Number);
		System.out.println("Equation: y^2 = x^3 + (" + a_Number + ")x + (" + b_Number + ")");

		for (BigInteger i = new BigInteger("0"); i.compareTo(mod_p) == -1;  i = i.add(BigInteger.valueOf(1)))
		{
			// these must go on the inside of the network so they are emptied each time
			// if public, they will contunutally be adding
			ArrayList<TableClass>  lst0 = new ArrayList<TableClass>();
			ArrayList<TableClass>  lst1 = new ArrayList<TableClass>();
			BigInteger x_Cor = i;


			//BigInteger a_To_SquareRoot = (Math.pow(x_Cor,3) + (a_Number*x_Cor) + b_Number)%mod_p;
			BigInteger a_To_SquareRoot = (x_Cor.pow(3).add(a_Number.multiply(x_Cor)).add(b_Number)).mod(mod_p);

			// we need to take the square root of that number 		
			//double y_Cor_Pos = SquareAndMultiply(a_To_SquareRoot, ((mod_p+1)/4), mod_p);
			BigInteger y_Cor_Pos = SquareAndMultiply(a_To_SquareRoot, mod_p.add(BigInteger.valueOf(1)).divide(BigInteger.valueOf(4)), mod_p);
			
			//double y_Cor_Neg = mod_p-(y_Cor_Pos%mod_p);
			// finding negative value
			BigInteger y_Cor_Neg = mod_p.subtract(y_Cor_Pos.mod(mod_p));

			//order = 0;
			if (SquareAndMultiply(y_Cor_Pos, BigInteger.valueOf(2), mod_p).compareTo(a_To_SquareRoot) == 0)
			{
				lst0.add(new TableClass(x_Cor, y_Cor_Pos));

				master.add(lst0);
			}
			if (SquareAndMultiply(y_Cor_Neg, BigInteger.valueOf(2), mod_p).compareTo(a_To_SquareRoot) == 0)
			{
				lst1.add(new TableClass(x_Cor, y_Cor_Neg));
				
				master.add(lst1);
			}
			
		}// end for loop fro find points

		order = master.size() + 1;
		System.out.println("\norder: " + order);

		// loop for adding points. P+P=2P...
		for (int i = 0; i < master.size(); i+=2)
		{
			// this list will be used to ass the entire line of points generated
			ArrayList<TableClass> finalList = new ArrayList<TableClass>();
			ArrayList<TableClass> finalList2 = new ArrayList<TableClass>();

			ArrayList<TableClass> tmp = master.get(i); // gets the first row index?
			ArrayList<TableClass> alt = master.get(i+1); // gets the first row index?

			TableClass tmp2 = tmp.get(0);
			TableClass alt2 = alt.get(0);

			//double point[] = tmp2.getFirstPoint();	
			BigInteger point_P[] = tmp2.getFirstPoint(); // contains the first point in each row
			// contains point opposite
			BigInteger point_Op[] = alt2.getFirstPoint();

			// account for single points; if ther is a single point, then the 
			//loop that uses the other point at a stoping point will go on forever
			if (point_P[0] != point_Op[0])
			{
				i++;
				continue;
			}
			// account for y equaling 0
			if (point_P[1] == BigInteger.valueOf(0) || point_Op[1] == BigInteger.valueOf(0))
			{
				i++;
				continue;
			}

			// add this point to finalList which will be added to master2 list later
			finalList.add(new TableClass(point_P[0], point_P[1]));
			finalList2.add(new TableClass(point_Op[0], point_Op[1]));
			
			// find P+P ==> P2 and add it to finalList			
			//System.out.println("\n\nDoubling: " + point_P[0] + " , " +point_P[1]);
			BigInteger point_P2[] = TangentPointPlusPoint(point_P[0], point_P[1], a_Number, mod_p);
			finalList.add(new TableClass(point_P2[0], point_P2[1]));
			//System.out.println();

			// other list; adding second point to finalList2
			BigInteger point_P2_2[] = TangentPointPlusPoint(point_Op[0], point_Op[1], a_Number, mod_p);
			finalList2.add(new TableClass(point_P2_2[0], point_P2_2[1]));



			// add P to PN stop one point before PN equals P again
			//System.out.println("Adding: " +point_P[0]+ " , " + point_P[1] + " to " +point_P2[0]+ " , " + point_P2[1]);


			if (point_P[0].compareTo(point_P2_2[0]) ==0 && point_P[1].compareTo(point_P2_2[1]) ==0 )
			{
				master2.add(finalList);
				master2.add(finalList2);
				continue;
			}



			BigInteger point_N[] = PointPlusPoint(point_P, point_P2, mod_p);
			//System.out.println("mod_p: ***********************************" + mod_p);
			while((point_N[0].compareTo(point_Op[0])!=0) || (point_N[1].compareTo(point_Op[1])!=0) )
			{
				//System.out.println("point_N: [" + point_N[0] + ", "+ point_N[1] + "]    point_Op: [" + point_Op[0] + ", "+ point_Op[1] + "]");	
				// start by adding the found point PN to the list
				finalList.add(new TableClass(point_N[0], point_N[1]));

				// find the next PN; if it is a valid point, the loop will start over again and it will be added
				//System.out.println("1 ----Not Equal, adding point_N: [" + point_N[0] + ", "+ point_N[1] + "]");
				point_N = PointPlusPoint(point_P, point_N, mod_p);


			}
			finalList.add(new TableClass(point_N[0], point_N[1]));
			
			//for testing
			if (finalList.size() >= order-10)
			{
				System.out.println(" first leg size: " + finalList.size());
			}

			// size of row must equal generator
			if (finalList.size() >= order-2)
			{	
				master2.add(finalList);
			}
			


			point_N = PointPlusPoint(point_Op, point_P2_2, mod_p);
			while((point_N[0].compareTo(point_P[0]) !=0) || (point_N[1].compareTo(point_P[1]) !=0) )
			{
				//System.out.println("point_N: [" + point_N[0] + ", "+ point_N[1] + "]    point_P: [" + point_P[0] + ", "+ point_P[1] + "]");	
				// start by adding the found point PN to the list
				finalList2.add(new TableClass(point_N[0], point_N[1]));
				//System.out.println("2 ----Not Equal, adding point_N: [" + point_N[0] + ", "+ point_N[1] + "]");

				// find the next PN; if it is a valid point, the loop will start over again and it will be added
				point_N = PointPlusPoint(point_Op, point_N, mod_p);

			}
			finalList2.add(new TableClass(point_N[0], point_N[1]));			

			//for testing
			if (finalList2.size() >= order-10)
			{
				System.out.println(" first leg size: " + finalList.size());
			}
			// size of row must equal generator
			if (finalList2.size() >= order-2)
			{

				master2.add(finalList2);
			}

			if (master2.size() == 30)
			{
				break;
			}
		}



		// order equals size of test points plus 1
		System.out.println();

		int genertorPointNumber = 0;

		// Print generator point
		ArrayList<TableClass> tmp = master2.get(genertorPointNumber); 
		TableClass tmp2 = tmp.get(0);
		System.out.print("Generator: ");
		tmp2.printData();		
		System.out.println("\n");


		// print A's secret key point
		System.out.println("Private Key for Alice: " + xA);
		tmp = master2.get(genertorPointNumber); 
		tmp2 = tmp.get((xA.subtract(BigInteger.valueOf(1))).intValue());
		System.out.print(xA +"th Point = ");
		tmp2.printData();
		System.out.println("\n\n");

		// print B's secret key point
		System.out.println("Private Key for Bob: " + xB);
		tmp = master2.get(genertorPointNumber); 
		tmp2 = tmp.get((xB.subtract(BigInteger.valueOf(1))).intValue());
		System.out.print(xB +"th Point = ");
		tmp2.printData();
		System.out.println();

		// find Key shared between them
		System.out.println("\nA Finding shared Key Ka");
		System.out.println("Alice finds Bob's private key by locating the Point he sent over");
		System.out.println("That number is multiplyed by her private key" );
		BigInteger k_SharedKey = xA.multiply(xB).mod(BigInteger.valueOf(order));
		System.out.println(xA + " * " + xB + " mod " + order + " = " + k_SharedKey);
		tmp = master2.get(genertorPointNumber); 
		tmp2 = tmp.get((k_SharedKey.intValue())-1);
		System.out.print(k_SharedKey +"th Point = ");
		tmp2.printData();
		System.out.println();







		System.out.println();

	}// end main

	public static BigInteger[] TangentPointPlusPoint(BigInteger x_Cor, BigInteger y_Cor, BigInteger a_Number, BigInteger mod_p)
	{
		BigInteger point[] = new BigInteger[2];
		//BigInteger top = x_Cor.pow(2).multiply(3).add(a_Number);
		//BigInteger top = (Math.pow(x_Cor, 2)*3)+a_Number);
		BigInteger top = (x_Cor.pow(2).multiply(BigInteger.valueOf(3)).add(a_Number)).mod(mod_p);
		//System.out.println("Top: " + top);

		//BigInteger bot = y_Cor.multiply(2);
		BigInteger bot = (y_Cor.multiply(BigInteger.valueOf(2))).mod(mod_p);

		if (bot.compareTo(BigInteger.valueOf(0)) == 0 || bot.compareTo(BigInteger.valueOf(0)) == -1)
		{
			System.out.println("bot: " + bot);
		}
		//must convert mod_p to BigInteger
		BigInteger modInverse = ModularInverse(bot, mod_p);

		BigInteger slope = modInverse.multiply(top).mod(mod_p);
		//System.out.println("slope: " + slope);

		// calaulate x 		slope^2 - 2(x_Cor)
		BigInteger new_X = (slope.pow(2).subtract(x_Cor.multiply(BigInteger.valueOf(2)))).mod(mod_p);
		//System.out.println("new_X: " + new_X);
		
		//calculate y 		slope(x_Cor - new_X) - y_Cor
		BigInteger new_Y = slope.multiply(x_Cor.subtract(new_X)).subtract(y_Cor).mod(mod_p);
		//System.out.println("new_Y: " + new_Y);

		point[0] = new_X;
		point[1] = new_Y;
		return point;
	}

	public static BigInteger[] PointPlusPoint(BigInteger[] p_Point, BigInteger[] q_Point, BigInteger mod_p)
	{
		BigInteger point[] = new BigInteger[2];
		
		BigInteger top = (q_Point[1].subtract(p_Point[1])).mod(mod_p);
		//System.out.println("top: " + top);

		BigInteger bot = (q_Point[0].subtract(p_Point[0])).mod(mod_p);
		//System.out.println("bot: " + bot);

		BigInteger modInverse = ModularInverse(bot, mod_p);
		BigInteger slope = (modInverse.multiply(top)).mod(mod_p);
		//System.out.println("Slope: " + slope);



		//calculate x 	new_X = slope - p_x - q_x
		BigInteger new_X = (slope.pow(2).subtract(p_Point[0]).subtract(q_Point[0])).mod(mod_p);
		//System.out.println("new_X: " + new_X);

		// calculate y 	new_Y = slope(p_x - new_X) - p_y
		BigInteger new_Y = (slope.multiply(p_Point[0].subtract(new_X)).subtract(p_Point[1])).mod(mod_p);
		//System.out.println("new_Y: " + new_Y);

		point[0] = new_X;
		point[1] = new_Y;
		return point;
	}


	public static void PrintArrayListTable(ArrayList<ArrayList<TableClass>> table)
	{
		System.out.println("\n******Print Table:******");

		for(int x =0;x<table.size();x++)		//iterate through the rows in master
		{	
			// each row is an object
			ArrayList<TableClass> tmp = table.get(x); // gets the first row index?

			// adds space between rows
			System.out.println();

			for(int y = 0; y<tmp.size();y++)	// iterate through the entries per row
			{	
				TableClass tmp2 = tmp.get(y);
				tmp2.printData();

			} // end inner loop
		}// end outer loop
		System.out.println();
	} // end PrintMasterTableTow




	public static void printDoubleArray(BigInteger[] array) 
	{
	   for (int i = 0; i < array.length; i++) 
	   {
	      System.out.print("_" + array[i]);
	   }
	   System.out.print("_\n");
	}



	public static BigInteger SquareAndMultiply(BigInteger baseUn, BigInteger exponent, BigInteger mod_p) 
	{
		/**
		* 1. take input for all values nessessary
		* 2. Convert exponent form int to binary to binary string array to binary int array
		* 3. Inside the main loop
		*	4. before the loop, mod p the base
		*	5. take the result and square it and mod p it [length of binary numbers] in the exponent TIMES.
		*		6. save the result to an array each time
		*	7. iterate through array for binary numbers
		*		8. if number == 1 then multiply the current result (start at 1) by the number in the other array
		*/

		BigInteger base = baseUn.mod(mod_p);

		long exp_Plus_Mod = exponent.intValue() % mod_p.intValue();						// simplifies the exponent

		String exp_Binary = Long.toBinaryString(exp_Plus_Mod);	// converts the exponent to binary


		int exp_Length = exp_Binary.length();						// gets binary length
		String exp_String_Array[] = exp_Binary.split("");			// splits binary number into string array
		int[] exp_Binary_Array = new int[exp_Length];
		for(int i = 1; i <= exp_Length; i++)						// loop to convery string array to number array
		{															// (possibly doesnt matter)
			int add = Integer.parseInt(exp_String_Array[i]);
			exp_Binary_Array[i-1] = add;
		}		
		BigInteger a_Base = base.mod(mod_p);

		BigInteger b_Base = new BigInteger("0");
		BigInteger[] math_Result_Array = new BigInteger[exp_Length];
		math_Result_Array[exp_Length-1] = a_Base;
		for(int i = 0; i < exp_Length-1; i++)
		{
			//b_Base = (a_Base*a_Base) % (mod_p);
			b_Base = a_Base.multiply(a_Base).mod(mod_p);

			math_Result_Array[exp_Length-i-2] = b_Base;
			a_Base = b_Base;
		}
		// result for loop
		BigInteger result = new BigInteger("1");
		for(int i = 0; i < exp_Binary_Array.length; i++)
		{
			if (exp_Binary_Array[i] == 1)
			{
				//result = (result*math_Result_Array[i]) % (mod_p);
				result = result.multiply(math_Result_Array[i]).mod(mod_p);
			}
		}
		// converts BigInteger to double
		return result;
	}// end of main

	public static BigInteger ModularInverse(BigInteger base, BigInteger mod_p) 
	{
		/**
		* 1. row one contains the mod of the previous two rows
		* 2. row two contains the quitent of the the same numbers as step 1
		* 3. row three contains... see excel sheet
		* 4. 
		*/
		// 117 mod 125 = 78
		// 1001 mod 1024 = 89

		BigInteger[] rowOne = new BigInteger[30]; // last numerical entry in this array must be 1
		rowOne[0] = mod_p;
		rowOne[1] = base;
		BigInteger[] rowTwo = new BigInteger[30];
		rowTwo[0] = new BigInteger("999");
		BigInteger[] rowThree = new BigInteger[30]; 
		int count = 0;
	
		//check this if broken
		for (BigInteger i = new BigInteger("1"); (rowOne[i.intValue()]).intValue() != 0 ; i = i.add(BigInteger.valueOf(1)))
		{
			//rowTwo[i] = rowOne[i-1] / rowOne[i];

			// divisor gets to a negative number here some how...
			rowTwo[i.intValue()] = rowOne[i.intValue()-1].divide(rowOne[i.intValue()]);

			rowOne[i.intValue()+1] = rowOne[i.intValue()-1].mod(rowOne[i.intValue()]);

			count = i.intValue();
		}

		rowThree[count] = new BigInteger("0");
		rowThree[count-1] = new BigInteger("1");
		for (int i = 1; i < count; i++)
		{
			//rowThree[count-i-1] = (rowTwo[count-i]) * (rowThree[count-i]) + (rowThree[count-i+1]);
			rowThree[count-i-1] = (rowTwo[count-i]).multiply((rowThree[count-i])).add((rowThree[count-i+1]));
		}

		BigInteger result = rowThree[0];

		if (count % 2 == 0)
		{
			//result = mod_p - result;
			result = mod_p.subtract(result);
		}

		return result;
	} // end of ModularInverse













} // end class
