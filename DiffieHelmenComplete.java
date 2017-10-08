// Author:	Jake Mola
// Date: 	3/21/17
// implements all aspects of the Diffie-Hellmen key exchange

import java.util.Scanner;
import java.util.Arrays;
import java.util.*;
import java.math.BigInteger;


public class DiffieHelmenComplete 
{

	public static void main(String []args) 
	{
		Scanner scan = new Scanner(System.in);

		System.out.print("Enter mod p: ");
		//long mod_p = scan.nextLong();
		BigInteger mod_p = scan.nextBigInteger();

		//System.out.print("\nEnter Generator: ");
		//long generator = scan.nextLong();
		BigInteger generator = BigInteger.valueOf(generatorFinder(mod_p.intValue())); //scan.nextBigInteger();
		System.out.println("generator ===== " + generator);		// please please make sure mod is less then 4 billion

		System.out.print("Enter Alices Private Key: ");
		//long alicePrivate = scan.nextLong();
		BigInteger alicePrivate = scan.nextBigInteger();

		System.out.print("Enter Bobs Private Key: ");
		//long bobPrivate = scan.nextLong();
		BigInteger bobPrivate = scan.nextBigInteger();

		System.out.println("\n======================================");
		System.out.println("Generator:\t\t" + generator + "\nMod p:\t\t\t" + mod_p + "\nAlices Private Key: \t" + alicePrivate + "\nBobs Private Key: \t" + bobPrivate);
		System.out.println("======================================");

		//long fromAlice = squareAndMultiply(generator, alicePrivate, mod_p);
		BigInteger fromAlice = generator.modPow(alicePrivate, mod_p);

		//long fromBob = squareAndMultiply(generator, bobPrivate, mod_p);
		BigInteger fromBob = generator.modPow(bobPrivate, mod_p);

		//long aResult = squareAndMultiply(fromBob, alicePrivate, mod_p);
		BigInteger aResult = fromBob.modPow(alicePrivate, mod_p);

		//long bResult = squareAndMultiply(fromAlice, bobPrivate, mod_p);
		BigInteger bResult = fromAlice.modPow(bobPrivate, mod_p);


		System.out.println("\n------------------------------------------Node Calculations---------------------------------------");
		System.out.println("   ****Alice****\t\t\t\t\t\t\t****Bob****");
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println(generator + " mod" + mod_p + " -->\t\t\t\t|\t\t\t<-- " + generator + " mod" + mod_p);
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println("Alices private number: " + alicePrivate + "\t\t\t|\t\t" + "Bobs Private Number: " + bobPrivate);
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println(generator + "^" + alicePrivate + " mod" + mod_p + " = " + fromAlice + " -->\t\t\t|\t\t\t<-- " + generator + "^" + bobPrivate + " mod" + mod_p + " = " + fromBob);
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println(fromBob + "^" + alicePrivate + " mod" + mod_p + " = " + aResult + "\t\t\t|\t\t\t" + fromAlice + "^" + bobPrivate + " mod" + mod_p + " = " + bResult);
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("Result key = " + aResult + "\n");

	}// end of main


	public static long generatorFinder(int mod_p)
	{
		/**
		* 1. Factor [modulus number] - 1; do not allow repeats
		* 2. test each factor; [nums between 1 and p-1]^(p/[each factor])
		* 3. if none of results are equal to 1, then the number is a potential generator. 
		* 4. 
		*/
		long workingMod_p = mod_p - 1;		// the number being factored 
		long modMinusOne = mod_p-1;			// for the second part
		int[] factors = new int[30];		// this will hold the factors 
		int count = 0;
		while (workingMod_p != 1)
		{
			// divide number by an incrementing value to see if there is not remainder
			for (int i = 2; (i-1) <= workingMod_p; i++)
			{
				long remainder = workingMod_p % i;
				if (remainder == 0)
				{
					factors[count] = i;
					workingMod_p = workingMod_p / i;
					count++;
					break;
				}
			} // end first for loop
		} // end outer while loop
				
		int numberOfGeneratorsToFind = 5; // used to limit the reaults to the first [###] of generators for mod_p
		long[] result = new long[numberOfGeneratorsToFind];
		int count2 = 0;			// only increment if a number is added to the result array

		for (int i = 2; i < modMinusOne; i++)
		{
			for (int j = 0; factors[j]!= 0 && (count2 < numberOfGeneratorsToFind) ; j++) 
			{
				// 				[all test numbers from 2 to p-2]^(modMinusOne/[each factor]) mod p
				long test = squareAndMultiply(i, (modMinusOne/factors[j]), mod_p);
				if (test == 1)
				{
					break; // breaks out of j loop
				}
				if (test != 1 && (j+1) == count) 	// if test is not 1 and we have tested the last entry in facters indicated by
				{								// value stored in count form the above loop; it is a generator
					result[count2] = i;
					count2++;
				}	
			}// end j loop

		} // end i loop
		
		return result[0];
	} // end of GeneratorFinder


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
	public static long squareAndMultiply(long baseUn, long exponent, long mod_p) 
	{
		//System.out.println("\n************** Square and Multiply Function **************");

		long base = baseUn % mod_p;
		long exp_Plus_Mod = exponent % mod_p;						// simplifies the exponent
		String exp_Binary = Long.toBinaryString(exp_Plus_Mod);	// converts the exponent to binary
		int exp_Length = exp_Binary.length();						// gets binary length
		String exp_String_Array[] = exp_Binary.split("");			// splits binary number into string array
		int[] exp_Binary_Array = new int[exp_Length];
		for(int i = 1; i <= exp_Length; i++)						// loop to convery string array to number array
		{															// (possibly doesnt matter)
			int add = Integer.parseInt(exp_String_Array[i]);
			exp_Binary_Array[i-1] = add;
		}
		//System.out.println("\nBase:\t\t" + base + "\nExponent:\t" + exponent + "\nModulus p:\t" + mod_p + "\n");
		// prints working equasion
		//System.out.println("\n" + baseUn + "^" + exponent + " mod " + mod_p);
		// prints simpleifies equasion
		//System.out.println("\n" + base + "^" + exp_Plus_Mod + " mod " + mod_p + "   <-- Simplified Equation" + "\n");

		//System.out.println("exp_Binary:\t" + exp_Binary + "\n");
		
		long a_Base = base % mod_p;
		long b_Base = 0;
		//System.out.println(base + "^1 mod " + mod_p + " == " + a_Base);
		long[] math_Result_Array = new long[exp_Length];
		math_Result_Array[exp_Length-1] = a_Base;
		for(int i = 0; i < exp_Length-1; i++)
		{
			b_Base = (a_Base*a_Base) % (mod_p);
			math_Result_Array[exp_Length-i-2] = b_Base;
			//System.out.println(a_Base + "^2 mod " + mod_p + " == " + b_Base);
			a_Base = b_Base;
		}
		// result for loop
		long result = 1;
		for(int i = 0; i < exp_Binary_Array.length; i++)
		{
			if (exp_Binary_Array[i] == 1)
			{
				//System.out.print(result + " * " + math_Result_Array[i] + " mod " + mod_p + " = ");
				result = (result*math_Result_Array[i]) % (mod_p);
				//System.out.println(result);
			}
		}
		//System.out.println("\nResult: " + result);
		//System.out.println("\n***********************************************************\n");
		return result;
	}// end of squareandmultiply function

}// and of class: DiffieHelmenComplete