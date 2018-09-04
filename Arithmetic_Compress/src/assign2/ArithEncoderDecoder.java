package assign2;
/**
 * Assignment 2
 * Submitted by: 
 * Shir Elbaz. 	ID# 204405690
 * Gal Arus. 	ID# 204372619
 * Matan Danino ID# 304802887
 */

import base.Compressor;

import java.math.BigDecimal ;
import java.math.BigInteger;

public class ArithEncoderDecoder implements Compressor
{

	public ArithEncoderDecoder()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Compress(String[] input_names, String[] output_names)
	{
		for(int compIndex = 0; compIndex < input_names.length; compIndex++) {
			String data = input_names[compIndex];
			double [] freq = new double[10];
			int numOfChars = 0 ;
			//inital the frequency's and counting the number of different char's in the data
			numOfChars = getFrequency(freq,data) ;
			
			char [] dictionary = new char[numOfChars];
			double fixedFreq[] = new double [numOfChars] ;
			
			for(int i = 0, j = 0 ; i < freq.length ; i++) {
				
				if(freq[i] != 0) {
					
					dictionary[j] = (char)(i+48) ;
					fixedFreq[j] = freq[i] / data.length() ;
					j++ ;
				}
			}
			//getting accuracy of 3 numbers after the floating point for the frequence's
			for(int i = 0 ; i < fixedFreq.length ; i++){
				fixedFreq[i] = round(fixedFreq[i]);
			}
			/*initial the starting value's*/	
			//scaling range : 0-1
			//index starting from 0
			BigDecimal low = new BigDecimal("0") ;
			BigDecimal high = new BigDecimal("1") ;
			int index  = 0 ;
			//getting the Compressed string and storage it on BigDecimal type name 'code'
			BigDecimal code = recDec(fixedFreq, dictionary, input_names[compIndex], low, high, index) ;
			//Convert the compressed BigDecimal to binary string
			String BinaryString = toBinaryString(code, data.length()) ;
			//storage the binaryString in the string array
			String key = "";
			for(int i = 0; i < freq.length; i++) {
				String tmp = Integer.toBinaryString((int)freq[i]);
				while(tmp.length() < 32)
					tmp = "0" + tmp;
				key+= tmp;
			}
			output_names[compIndex] = key + BinaryString ;
		}
	}
	private int getFrequency(double[] freq, String data) {
		/*This function calculate the char's frequency and initial the results at double array called 'freq'
		 *the function return the number of different char's that appear in the data 
		 */
		int numOfChars = 0 ;
		for(int i = 0; i < data.length(); i++) {
			if(freq[data.charAt(i)-48] == 0) {
				numOfChars++ ;
			}
			freq[data.charAt(i)-48] ++ ;
		}
		return numOfChars;
	}
	private BigDecimal recDec(double freq[],char[] dictionary ,String data, BigDecimal low, BigDecimal high, int index) {
		
		//check if we done to compress the data
		if(index == data.length()) {
			//return the middle of the last scaling : (high+low) / 2
			BigDecimal code = low.add(high) ;
			BigDecimal divider = new BigDecimal("2") ;
			code = code.divide(divider);
			
			return code ;
		}
		
		//creating the arithmetic code scaling graph
		BigDecimal graph[] = makeGraph(freq, low, high) ;
		
		//check the current character to compress
		int currentCharIx = getCharIndex(dictionary, data, index) ; 
	
		//make recursive call to encode the next char in the 'data'
		return recDec(freq, dictionary, data, graph[currentCharIx], graph[currentCharIx+1], index+1) ;
	}
	private BigDecimal[] makeGraph(double freq[], BigDecimal low, BigDecimal high) {
		//creating a BigDecimal array type, to store the scaling value's of the char's
		BigDecimal graph[] = new BigDecimal[freq.length + 1] ;
		//Initialize the borders value's
		graph[0] = low ;
		graph[graph.length-1] = high ;
		
		//setting up the range value : high-low
		BigDecimal range = new BigDecimal((high.subtract(low)).toString());
		//range = range.subtract(low);
		
		for(int i = 1 ; i < graph.length-1 ; i++){
			BigDecimal floor = new BigDecimal(graph[i-1].toString()) ;
			String freqStr = String.valueOf(freq[i-1]);
			BigDecimal freqBD = new BigDecimal(freqStr);
			graph[i] = floor.add(freqBD.multiply(range));
		}
		return graph ;
	}
	private int getCharIndex(char[] dictionary, String data, int index){
		/*This function return the index of the current char to compress*/	
		int res = 0 ;
		for(int i = 0 ; i < dictionary.length ; i++) {
			if(data.charAt(index) == dictionary[i]) {
				res = i ;
				break;
			}
		}
		return res ;
	}
	private String toBinaryString(BigDecimal code, int len) {	
		//this function convert the code that in BigDecimal format to binary string
		BigDecimal codeCopy = new BigDecimal(code.toString());
		BigInteger codeInt = new BigInteger("0");
		BigDecimal mul = new BigDecimal("10");
		
		while(len != 0){
			codeCopy = codeCopy.multiply(mul);
			len-- ;
		}
		codeInt = codeCopy.toBigInteger();
		String codeBinary = codeInt.toString(2);
		
		return codeBinary;
		
	}
	public void Decompress(String[] input_names, String[] output_names){	
		//in this function we convert the coded data to uncoded data- the original data
		//we take the code from the output_names and store the uncoded input_names
		for(int dataIndex = 0; dataIndex < input_names.length; dataIndex++) {
			int sum = 0 ;
			int[] freq = new int [10];
			int index = 0;
			for(int i = 0; i < 10; i++) {
				int tmpFreq = 0;
				for(int j = 0; j < 32; j++) {
					if(input_names[dataIndex].charAt(index) == '1')
						tmpFreq+= Math.pow(2, 31 - j);
					index++;
				}
				freq[i] = tmpFreq;
			}
			int size = 0;
			for(int i = 0; i < 10; i++)
				if(freq[i] > 0) {
					size++;
					sum+=freq[i];
				}
			char[] dictionary = new char[size];
			double[] fixedFreq = new double[size];
			int iter = 0;
			for(int i = 0; i < 10; i++) {
				if(freq[i] > 0) {
					dictionary[iter] = (char)(i+48);
					fixedFreq[iter] = ((double)freq[i])/sum;
					iter++;
				}
			}
			//getting accuracy of 3 numbers after the floating point for the frequence's
			for(int i = 0 ; i < fixedFreq.length ; i++){
				fixedFreq[i] = round(fixedFreq[i]);
			}
			//Getting the data
			String binaryCode = input_names[dataIndex].substring(32*10);
			//Convert to the origin decimal code
			BigDecimal code = toDecimalCode(binaryCode, sum);
			//Convert the decimal code to the origin data
			//initial start's arguments
			String unCoded = "" ;
			BigDecimal low = new BigDecimal("0");
			BigDecimal high = new BigDecimal("1");
			unCoded = decRec(fixedFreq, dictionary, code, unCoded, low, high, sum);
			output_names[dataIndex] = unCoded;
		}
	}
	private String decRec(double[] freq, char[] dictionary,
			BigDecimal code,String unCoded, BigDecimal low, BigDecimal high, int length) {	
		//decRec is recursive function that compress string to decimal number
		if(unCoded.length() == length){
			return unCoded ;
		}
		
		BigDecimal graph[] = makeGraph(freq, low, high);
		int chIndex = range(graph, low, high, code);
		low = graph[chIndex-1];
		high = graph[chIndex] ;
		chIndex -- ;
		unCoded += dictionary[chIndex];
		
		return decRec(freq, dictionary, code, unCoded, low, high, length);
	}
	private BigDecimal toDecimalCode(String binaryCode, int len) {
		/*This function convert from Binary String compressed code to the origin Decimal code*/
		
		//Convert to integer number
		BigInteger intCode = new BigInteger(binaryCode, 2) ;
		
		//Convert from integer to the origin decimal code
		BigDecimal code = new BigDecimal(intCode) ;
		BigDecimal divider = new BigDecimal("10");
		
		while(len != 0) {
			
			code = code.divide(divider);
			len-- ;
		}		
		return code;
	}
	public byte[] CompressWithArray(String[] input_names, String[] output_names)
	{ 
		//question number3- the compress function of the convert to 8 bytes in array
		Compress(input_names, output_names);
		String data = output_names[0];
		byte[] retData = new byte[(int)Math.ceil(data.length()/8.0)];
		int retIndex = 0;
		int nextByte = 0;
		int byteIndex = 7;
		int dataIndex = 0;
		while(dataIndex < data.length()) {
			if(data.charAt(dataIndex) == '1')
				nextByte+=Math.pow(2, byteIndex);
			dataIndex++;
			byteIndex--;
			if(byteIndex == -1) {
				retData[retIndex] = (byte)nextByte;
				retIndex++;
				nextByte = 0;
				byteIndex = 7;
			}
			
		}
		return retData;
	}
	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names)
	{
		//question number 3- we convert the data to a 8 bytes number on array
		Decompress(input_names, output_names);
		String data = output_names[0];
		byte[] retData = new byte[(int)Math.ceil(data.length()/8.0)];
		int retIndex = 0;
		int nextByte = 0;
		int byteIndex = 7;
		int dataIndex = 0;
		while(dataIndex < data.length()) {
			if(data.charAt(dataIndex) == '1')
				nextByte+=Math.pow(2, byteIndex);
			dataIndex++;
			byteIndex--;
			if(byteIndex == 0) {
				retData[retIndex] = (byte)nextByte;
				retIndex++;
				nextByte = 0;
				byteIndex = 7;
			}
		}
		return retData;
	}
	int range(BigDecimal graph[], BigDecimal low, BigDecimal high, BigDecimal code){
		//this function use scaling graph that we create and return the index of the next range
		int index = 1 ;
		for(int i = 1 ; i < graph.length ; i++) {
			
			if(code.compareTo(graph[i]) < 0 ){
				return i;
			}
		}
		return index ;
	}	
	public static double round(double number) {
		//this function get accuracy of 3 numbers after the floating point
		int decimalPlace = 3 ;
		BigDecimal bd = new BigDecimal(number);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	

}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    