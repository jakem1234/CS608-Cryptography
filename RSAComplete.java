//RSAEncryption.java

//PlaintextNumericalFormDecryption

import java.util.Scanner;
import java.util.Arrays;
import java.util.*;
import java.math.BigInteger;


public class RSAComplete
{
	/**
	* 1. input p, q,
	* 2. choose a small number e coprime to phi(n)
	* 3. 
	* 4. 
	* 5. 
	*/
	public static void main(String []args) 
	{
		System.out.println("\n********************************** RSA Complete ******************************************");
		Scanner scan = new Scanner(System.in);

		System.out.print("\nEnter large prime p: ");
		BigInteger p_prime = scan.nextBigInteger();
		//BigInteger p_prime =  new BigInteger("746981");						// given

		System.out.print("Enter large prime q: ");
		BigInteger q_prime = scan.nextBigInteger();
		//BigInteger q_prime =  new BigInteger("746989");						// given

		System.out.print("Enter base (26, 36, 52, 62): ");
		// depending on what number is entered, subtract from the ocnversion as nessessary. if only lowercase, subtract 97 (a = 97)
		BigInteger base = scan.nextBigInteger();
		//BigInteger base = new BigInteger("26");	

		System.out.print("Enter Message m: ");
		String textMessage = scan.next();
		//String textMessage = "security";


		//long phi_Of_N = (p_prime-1)*(q_prime-1);
		BigInteger phi_Of_N = p_prime.subtract(BigInteger.valueOf(1)).multiply(q_prime.subtract(BigInteger.valueOf(1)));

		System.out.println("\nphi_Of_N = (p_prime-1)*(q_prime-1)\nphi_Of_N = (" + p_prime + "-1)*(" + q_prime + "-1)\n" 
				+ "phi_Of_N = " + phi_Of_N + "\n");

		BigInteger n_number = p_prime.multiply(q_prime);		// this is the mod_p
		System.out.println("n_number = part of public key\nn_number = p_prime * q_prime\nn_number = " + p_prime + " * " + q_prime
			+ "\nn_number = " + n_number + "\n");

		BigInteger e_number = findE(phi_Of_N);		// might be given or not; its a number coprime to phi(n)
		System.out.println("e_number = part of public key\ne_number = a number coprime to phi_Of_N\n" 
			+ "e_number = number e where GCD(e," + phi_Of_N + ") = 1\ne_number = " + e_number + "\n");

		int blockSize = blockSizeFinder(base, n_number);
		System.out.println("BlockSize: " + blockSize + "\n");
		while (textMessage.length() % blockSize != 0)
		{
			textMessage += "a";
		}
		BigInteger[] message = polyEncryption(textMessage, base, blockSize);

		System.out.println("Poly Encoded Blocks: ");
		printBigIntArray(message);
		BigInteger[] c_cipherText = new BigInteger[message.length];
		for (int i = 0; i < message.length; i++)
		{
			// loop to encrypt each polyAlphabetic item in the message[] array
			//encryption
			c_cipherText[i] = message[i].modPow(e_number, n_number);
			System.out.println("\ncipherText[" + i + "] = m^e mod n\ncipherText[" + i + "] = " 
							+ message[i] + "^" + e_number + " mod " + n_number + "\ncipherText[" + i + "] = " 
							+ c_cipherText[i]);
		}

		BigInteger d_number = e_number.modInverse(phi_Of_N);
		System.out.println("\nd_number = part of secret key\nd_number = modularInverse of e_number mod phi_Of_N\nd_number = modularInverse(" 
				+ e_number + ", " + phi_Of_N 
				+ ")\nd_number = " + d_number);

		BigInteger[] decryptedMessage = new BigInteger[message.length]; 
		for (int i = 0; i < message.length; i++)
		{
			//decryption
			decryptedMessage[i] = c_cipherText[i].modPow(d_number, n_number);
			System.out.println("\ndecryptedMessage[" + i + "] = c^d mod n\ndecryptedMessage[" + i + "] = " 
							+ c_cipherText[i] + "^" + d_number + " mod " + n_number + "\ndecryptedMessage[" + i + "] = " 
							+ decryptedMessage[i]);
		}

		polyDecryption(decryptedMessage, base, blockSize); // returns array containing origional string with padding 

		System.out.println("\n********************************** ^^^^^^^^^^^^ **********************************");

	} // end of main

	public static int blockSizeFinder(BigInteger base, BigInteger mod_p)
	{
		//System.out.print("\nEnter blockSize: \n");
		//int blockSize = scan.nextInt();

		int blockSize = 1;
		 //testBlockSize = BigInteger.ZERO;
		//System.out.println("testBlockSize: " + testBlockSize);
		for (BigInteger i = new BigInteger("1"); i.compareTo(mod_p) == -1; i = i.add(BigInteger.valueOf(1))) 
		{
			BigInteger testBlockSize = base.multiply(base.pow(i.intValue())).subtract(BigInteger.valueOf(1));
			if (testBlockSize.compareTo(mod_p) == -1)
			{
				blockSize = i.intValue();
			}
			else
			{
				BigInteger testBlockSize2 = base.multiply(base.pow((i.intValue()-1))).subtract(BigInteger.valueOf(1));
				System.out.println("blockSize i = highest exponent paired with a base that remains less then the modulus");
				System.out.println("blockSize i = base*base^i -1\nTesting value i = " + base + "*("+ base + "^" + i + ") -1");
				System.out.println("Maxamum number able to be represented with i chars given base:" 
					+ base + " = " + testBlockSize2);
				return blockSize;
			}
		}
		return blockSize;
	}

/**
	* 1. take one encoded value
	* 2. decode and form array; (cipherValue%base)=offset		then		newCiphervalue=(cipherValue-offset)/base
	* 3. convery each array number into their alphabetical representation
	* 4. 
	*/
	public static char[] polyDecryption(BigInteger[] cipherArray, BigInteger base, int blockSize) 
	{
		BigInteger cipherValue = new BigInteger("0");
		BigInteger[] resultIntArray = new BigInteger[blockSize*(cipherArray.length)];
		for (int p = 0; p < cipherArray.length; p++) 
		{
			cipherValue = cipherValue.multiply(BigInteger.valueOf(0));
			cipherValue = cipherValue.add(cipherArray[p]);

			BigInteger newCypherValue = new BigInteger("0");
			for (int i = 0; i < blockSize; i++)
			{
				BigInteger offset = cipherValue.mod(base);//(cipherValue%base);
				resultIntArray[(blockSize-i-1)+(p*blockSize)] = offset;		// enters the data in backwads so the letters will be ins order
				cipherValue = cipherValue.subtract(offset).divide(base); // (cipherValue-offset)/base
			}

		}// end outside loop that goes through each array
		System.out.println("\nresultIntArray");
		printBigIntArray(resultIntArray);

 	//two loops
		char[] resultCharArray = new char[resultIntArray.length];
		char[] alaphabetArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		 //{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L','M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

		for(int i = 0; i < resultIntArray.length; i++) //for the BigInteger array
		{
			resultCharArray[i] = alaphabetArray[resultIntArray[i].intValue()];
		}
		System.out.println("resultCharArray");
		printCharArray(resultCharArray);
		
	return resultCharArray;
	} // end of polyDecryption



	public static BigInteger[] polyEncryption(String text, BigInteger base2, int blockSize) 
	{

		char[] textArray = text.toCharArray();
		
		BigInteger[][] blockArray2 = new BigInteger[(textArray.length)/blockSize][blockSize];
		int count = 0;
		char[] alaphabetArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		 //{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L','M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

		 for(int i = 0; i < textArray.length; i++) //for the BigInteger array
		 {
		 	for(int j = 0; j < alaphabetArray.length; j++)
		 	{
		 		if (textArray[i] == alaphabetArray[j])
		 		{
		 			blockArray2[count][i%blockSize] = BigInteger.valueOf(j);
		 			break;
		 		}
		 	}

		 	if (i % blockSize == blockSize-1)
			{
				count++;
			}
		 }
		System.out.println("Numerical Encoded Letters:");
		printBI2DArray(blockArray2, ((textArray.length)/blockSize), blockSize);
		System.out.println();

		BigInteger subValue2 = new BigInteger("0");
		BigInteger[] resultArray2 = new BigInteger[((textArray.length)/blockSize)];
		// [numLetter] * 26^[block size]-1    +   [numLetter] * 26^[block size]-2 .....
		for(int i = 0; i < ((textArray.length)/blockSize); i++)
		{
			BigInteger result2 = new BigInteger("0");
			for(int j = 0; j < blockSize; j++)
			{
				subValue2 = blockArray2[i][j].multiply(base2.pow(blockSize-(j+1)));
				//System.out.println(blockArray[i][j] + " * " + base + "^" + blockSize + "-" + i + "+1  =  " + subValue);
				//System.out.println("subValue: " + subValue);
				result2 = result2.add(subValue2);

			}
			resultArray2[i] = result2;
		}
		return resultArray2;
	} // end polyEncryption

	public static BigInteger findE(BigInteger phi_Of_N)
	{
		for (BigInteger i = new BigInteger("2"); i.compareTo(phi_Of_N) == -1; i = i.add(BigInteger.valueOf(1)))
		{
			// GCD formula:				q					r
			// longer = shorter * [longer/shorter] + [longer%shorter]
			BigInteger calc_gcd = i.gcd(phi_Of_N);
		
			if (calc_gcd.compareTo(BigInteger.valueOf(1)) == 0) // if calc_gcd == 1
			{
				return i;
			}
		}
		return null;
	}

	/**
	* 1. row one contains the mod of the previous two rows
	* 2. row two contains the quitent of the the same numbers as step 1
	* 3. row three contains... see excel sheet
	* 4. 
	*/
	public static long modularInverse(long base, long mod_p) 
	{
		//System.out.println("\n******************* Modular Inverse Function *******************\n");
		long[] rowOne = new long[30]; // last numerical entry in this array must be 1
		rowOne[0] = mod_p;
		rowOne[1] = base;
		long[] rowTwo = new long[30];
		rowTwo[0] = 999;
		long[] rowThree = new long[30]; 
		int count = 0;
		for (int i = 1; rowOne[i] != 0; i++)
		{
			rowTwo[i] = rowOne[i-1] / rowOne[i];
			rowOne[i+1] = rowOne[i-1] % rowOne[i];
			count = i;
		}

		rowThree[count] = 0;
		rowThree[count-1] = 1;
		for (int i = 1; i < count; i++)
		{
			rowThree[count-i-1] = (rowTwo[count-i]) * (rowThree[count-i]) + (rowThree[count-i+1]);
		}

		long result = rowThree[0];

		if (count % 2 == 0)
		{
			result = mod_p - result;
		}

		//System.out.println("\nResult: " + result);
		//System.out.println("\n******************* ^^^^^^^^^^^^^^^^^^^^^^^^ *******************\n");
		return result;
	} // and of main

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
		return result;
	}// end of squareandmultiply function





	public static void printCharArray(char[] array) 
	{
		for (int i = 0; i < array.length; i++) 
		{
			System.out.print(" _" + array[i] + "_ ");
		}
		System.out.print("\n");
	}

	public static void printLongArray(long[] array) 
	{
	   for (int i = 0; i < array.length; i++) 
	   {
	      System.out.print("_" + array[i]);
	   }
	   System.out.print("_\n");
	}

	public static void printIntArray(int[] array) 
	{
	   for (int i = 0; i < array.length; i++) 
	   {
	      System.out.print("_" + array[i]);
	   }
	   System.out.print("_\n");
	}
	public static void printStringArray(String[] array) 
	{
	   for (int i = 0; i < array.length; i++) 
	   {
	      System.out.print(" _" + array[i]);
	   }
	   System.out.print("_\n");
	}

	public static void print2DArray(int[][] array, int one, int two) 
	{
		for(int i = 0; i < one; i++)
		{
			for(int j = 0; j < two; j++)
			{
				System.out.printf("%5d ", array[i][j]); // %5d is to seperate the numbers being printed to screen
			}
			System.out.println();
		}
	}
	public static void printBI2DArray(BigInteger[][] array, int one, int two) 
	{
		for(int i = 0; i < one; i++)
		{
			for(int j = 0; j < two; j++)
			{
				System.out.printf("%5d ", array[i][j]); // %5d is to seperate the numbers being printed to screen
			}
			System.out.println();
		}
	}

	public static void printBigIntArray(BigInteger[] array) 
	{
		for (int i = 0; i < array.length; i++) 
		{
			System.out.print(" _" + array[i] + "_ ");
		}
		System.out.print("\n\n");
	}







}// end of class



