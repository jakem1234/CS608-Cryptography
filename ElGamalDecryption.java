//ElGamalDecryption
import java.util.Scanner;
import java.util.Arrays;
import java.util.*;

public class ElGamalDecryption
{
	/**
	* 1. using private key b; q = (p-1)-b
	* 2. opener R = H^q modp
	* 3. decrypted message D = C * R mod p = m[message]
	* 4.  
	* 5. 
	* 6. 
	* 7. 
	* 8. 
	*/
	// public static int(long mod_p, long u2Private, long hint, long cipherText)
	public static void main(String []args) 
	{
		System.out.println("\n********************* El Gamal Decryption *********************");
		Scanner scan = new Scanner(System.in);

		System.out.print("\nEnter prime mod p: ");				// p
		//long mod_p = scan.nextLong();
		long mod_p = 53;
		
		System.out.print("Enter User 2's Private Key b: ");		// b
		//long u1Private = scan.nextLong();
		long u2Private = 13;

		System.out.print("Enter the recieved Hint H : ");		// H
		//long hint = scan.nextLong();
		long hint = 45;

		System.out.print("Enter the recieved ciphertext : ");	// C
		//long cipherText = scan.nextLong();
		long cipherText = 47;


		// Computing q : mod_p - 1 - [user 2's private key]
		long q = (mod_p-1-u2Private);
		System.out.println("\nCalculating q: " + mod_p + " - 1 - " + u2Private  + " = " + q);

		
		// Calculate the opener R: H^q mod_p
		long opener_R = squareAndMultiply(hint, q, mod_p);
		System.out.println("Calculating opener R: " + hint + "^" + q + " mod " + mod_p + " = " + opener_R);

		// Calculate decryptedMessage message D
		long decryptedMessage = (cipherText*opener_R)%mod_p;
		System.out.println("Calculating decryptedMessage: " + cipherText + "*" + opener_R + " mod " + mod_p + " = " + decryptedMessage);
		System.out.println("\n********************* ^^^^^^^^^^^^^^^^^^^ *********************\n");

	} // end of main

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



}// end of class