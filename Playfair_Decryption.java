// this program will take a string as input and decrypt it using the standard 
// english alphabet as a key
// 2/5/17
// Jake Mola


import java.util.Scanner;
import java.util.Arrays;
import java.util.*;

public class Playfair_Decryption 
{
	public static void main(String []args) 
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("\nEnter Cipher text:");
		String plainText = scan.nextLine();

		System.out.println("\nCipher Text:\t" + plainText);

		String[] blockArray = digraphGenerator(plainText);

		String resultString = subsitutionActions(blockArray);
		System.out.println("\nPlain Text:\t" + resultString + "\n\n");
	}// end of main


//**************************************************************************************************************************************************
	/**
	* Function will add padding to the end of the string if nessessary
	* correct for double letters
	* and return an array of 2 letter strings made from the input string
	* 1. remove spaces
	* 2. cycle throuh chars in string
	* 3. if pair of chars are the same, add an x in between them and check again by decrementing vars by 2
	* 4. once all pairs are checked, add a padding x at the end if total length is uneven
	* 5. form string array using regex and split function and return it. 
	*/
	public static String[] digraphGenerator(String str)
	{
		String plainText = str.replaceAll("\\s+",""); // removes spaces
		int c1 = 0;
		int c2 = 1;
		while( c2 > -6 )
		{
			if (plainText.charAt(c1) == plainText.charAt(c2))
			{
				plainText = plainText.substring(0, c2) + "x" + plainText.substring(c2, plainText.length());
				c1-=2;
				c2-=2;
			}
			c1+=2;
			c2+=2;
			if (c2 >= plainText.length())
			{
				if (plainText.length() % 2 == 1)
				{
					plainText += "x";
				}
				String[] blockArray = plainText.split(String.format("(?<=\\G.{%1$d})", 2)); // splits the string into groups of 2
				return blockArray;
			}
		}
		return null;
	}// end of digraphGenerator

//**************************************************************************************************************************************************
	/**
	* Function to deal with the blocks of plaintext 
	* 1. make 2d array containing alphabet
	* 2. loop through all entries in that array adn assign both letters to variables
	* 3. locate the coordinates of the letters in the sd arrayusing nested loops
	* 4. 3 if statements are used to find the relation between the letters; recatngle, vertical, horizintal
	* 5. inside each if statement, the new letters are found
	* 6. lastly, the chars are individually added to a char array then assembledinto a string and returned 
	*/
	public static String subsitutionActions(String[] blockArray)
	{
		char[][] alaphabetArray = 
		{ //  0	   1	2	 3	  4
			{'a', 'b', 'c', 'd', 'e'}, // 0
			{'f', 'g', 'h', 'i', 'k'}, // 1				// must figure out how to handle i/j
			{'l', 'm', 'n', 'o', 'p'}, // 2
			{'q', 'r', 's', 't', 'u'}, // 3
			{'v', 'w', 'x', 'y', 'z'}  // 4
		};
		char[][] alaphabetArrayReversed = 
		{ //  0	   1	2	 3	  4
			{'z', 'y', 'x', 'w', 'v'}, // 0
			{'u', 't', 's', 'r', 'q'}, // 1				// must figure out how to handle i/j
			{'p', 'o', 'n', 'm', 'l'}, // 2
			{'k', 'i', 'h', 'g', 'f'}, // 3
			{'e', 'd', 'c', 'b', 'a'}  // 4
		};


		int q = 0;
		char resultCharArray[] = new char[blockArray.length*2];
		for (int i = 0; i < blockArray.length; i++)
		{
			char letter1 = blockArray[i].charAt(0);
			char letter2 = blockArray[i].charAt(1);

			char newLetter1 = ' ';
			char newLetter2 = ' ';	

			int l1j = 0;
			int l1k = 0;
			int l2j = 0;
			int l2k = 0;
			
			for (int j = 0; j < 5; j++)
			{
				for (int k = 0; k < 5; k++)
				{
					if (alaphabetArray[j][k] == letter1)
					{
						l1j = j;
						l1k = k;
					}
					else if (alaphabetArray[j][k] == letter2)
					{
						l2j = j;
						l2k = k;
					}
				} // end of k loop
			} // end of j loop
			if (l1j != l2j && l1k != l2k) // rectangle relation
			{
				newLetter1 = alaphabetArray[l1j][l2k];
				newLetter2 = alaphabetArray[l2j][l1k];
			}
			else if (l1k == l2k) // vertical relation
			{
				int a = (l1j - 1);
				int b = (l2j - 1);
				if ((l1j - 1) == -1) {a = 4;}
				if ((l2j - 1) == -1) {b = 4;}

				newLetter1 = alaphabetArray[a][l1k];
				newLetter2 = alaphabetArray[b][l2k];
			}
			else if (l1j == l2j) // horizontal relation
			{
				int c = (l1k - 1);
				int d = (l2k - 1);
				if ((l1k - 1) == -1) {c = 4;}
				if ((l2k - 1) == -1) {d = 4;}

				newLetter1 = alaphabetArray[l1j][c];
				newLetter2 = alaphabetArray[l2j][d];
			}
			resultCharArray[q] = newLetter1;
			q++;
			resultCharArray[q] = newLetter2;
			q++;
		} // end of blockArray cycle for loop
		String resultString = new String(resultCharArray);
		return resultString;
	} // end of subsitutionActions

} // end of class