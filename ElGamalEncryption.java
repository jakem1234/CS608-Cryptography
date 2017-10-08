import java.util.Scanner;
import java.util.Arrays;
import java.util.*;

public class ElGamalEncryption
{
	/** 		given two public keys, a private/secret key, a mod and a message, encrypt
	* 1. each user agrees on a prime p and generator; take these inputs first
	* 2. each user needs to generate a public key = generator^privateKey mod p
	* 	3. public key should be sent to all users
	* 4.  
	* 5. 
	* 6. 
	* 7. 
	* 8. 
	*/
	// public static int[] ElGamalEncryption(long mod_p, long u1Private, long u2Private, long u1Secret, long message)
	public static void main(String []args) 
	{
		System.out.println("\n********************* El Gamal Encryption *********************");
		Scanner scan = new Scanner(System.in);

		System.out.print("\nEnter prime mod p: ");			// p
		long mod_p = scan.nextLong();

		long generator = generatorFinder(mod_p);
		System.out.println("Lowest Generator: " + generator);	// g

		System.out.print("Enter User 1's Private Key: ");	// a
		long u1Private = scan.nextLong();

		System.out.print("Enter User 2's Private Key: ");	// b
		long u2Private = scan.nextLong();

		System.out.print("Enter User 1's Secret Key k: ");	// k
		long u1Secret = scan.nextLong();		

		System.out.print("Enter Messsage: ");				// m
		long message = scan.nextLong();


		// find publickeys for A and B
		//calaulated by alice and sent to bob
		System.out.println("\nCalculating User1's Public Key: " + generator + "^" + u1Private + " mod " + mod_p);
		long u1Public = squareAndMultiply(generator, u1Private, mod_p);
		System.out.println("User1's Public key: " + u1Public);

		// calculated by bob and sent to alice
		System.out.println("\nCalculating User2's Public Key: " + generator + "^" + u2Private + " mod " + mod_p);
		long u2Public = squareAndMultiply(generator, u2Private, mod_p);
		System.out.println("User2's Public key: " + u2Public);



		// Mask: [user2's public key]^[user1's private key] mod p // square and multi function
		// mask can not be 1
		System.out.println("\nCalculating Mask: " + u2Public + "^" + u1Secret + " mod " + mod_p);
		long mask = squareAndMultiply(u2Public, u1Secret, mod_p);
		if (mask == 1)
		{
			System.out.println("Mask equals: " + mask + " Exiting code\n");
			System.exit(0);
		}
		System.out.println("Mask: " + mask);

		// Ciphertext: message * mask mod p 
		System.out.println("\nCalculating Ciphertext: " + message + "*" + mask + " mod " + mod_p);
		long cipherText = (message*mask) % mod_p;
		System.out.println("Ciphertext: " + cipherText);

		// Hint: [generator]^[user1's secret key] mod p
		System.out.println("\nCalculating Hint: " + generator + "^" + u1Secret + " mod " + mod_p);
		long hint = squareAndMultiply(generator, u1Secret, mod_p);
		System.out.println("Hint: " + hint);

		// send[Ciohertext, Hint] over internet to user2
		System.out.println("\nSent\t[   Ciphertext: _"+ cipherText + "_   Hint: _" + hint + "_   ]\n");
		//int sent[] = {cipherText, hint};

		System.out.println("********************* ^^^^^^^^^^^^^^^^^^^ *********************\n");

	} // and of main


	/**
	* 1. Factor [modulus number] - 1; do not allow repeats
	* 2. test each factor; [nums between 1 and p-1]^(p/[each factor])
	* 3. if none of results are equal to 1, then the number is a potential generator. 
	* 4. 
	*/
	public static long generatorFinder(long mod_p) 
	{
		//System.out.println("\n************** Generator Finder Function **************");

		long workingMod_p = mod_p - 1;		// the number being factored 
		long modMinusOne = mod_p-1;			// for the second part
		int[] factors = new int[30];		// this will hold the factors 
		int count = 0;
		while (workingMod_p != 1)
		{
			// divide number by an incrementing value to see if there is not remainder
			for (int i = 2; (i-1) <= workingMod_p; i++)
			{
				//System.out.println("\n\t\ti: " + i + "\tworkingMod_p: " + workingMod_p);
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
		int numberOfGeneratorsToFind = 2; // used to limit the reaults to the first [###] of generators for mod_p
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
		//System.out.println("\n************** ^^^^^^^^^^^^^^^^^^^^^^^^^ **************\n");
		// assumign that the lowest generator will always work...
		return result[0];
	} // end of generator finder


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
		//System.out.println("************** Square and Multiply Function **************");

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
		//System.out.println("\n" + baseUn + "^" + exponent + " mod " + mod_p + "\t" + base + "^" + exp_Plus_Mod 
		//	+ " mod " + mod_p + "   <-- Simplified Equation" + "\n");
		// prints simpleifies equasion		
		long a_Base = base % mod_p;
		long b_Base = 0;
		long[] math_Result_Array = new long[exp_Length];
		math_Result_Array[exp_Length-1] = a_Base;
		for(int i = 0; i < exp_Length-1; i++)
		{
			b_Base = (a_Base*a_Base) % (mod_p);
			math_Result_Array[exp_Length-i-2] = b_Base;
			a_Base = b_Base;
		}
		// result for loop
		long result = 1;
		for(int i = 0; i < exp_Binary_Array.length; i++)
		{
			if (exp_Binary_Array[i] == 1)
			{
				result = (result*math_Result_Array[i]) % (mod_p);
			}
		}
		//System.out.println("Result: " + result);
		//System.out.println("\n************** ^^^^^^^^^^^^^^^^^^^^^^^^^^^^ **************");
		return result;
	}// end of squareandmultiply function



}// and of class





