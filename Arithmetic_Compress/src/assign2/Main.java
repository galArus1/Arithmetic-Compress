package assign2;
/**
 * Assignment 2
 * Submitted by: 
 * Shir Elbaz. 	ID# 204405690
 * Gal Arus. 	ID# 204372619
 * Matan Danino ID# 304802887
 */


public class Main {
	public static void main(String[] args) {
		ArithEncoderDecoder x = new ArithEncoderDecoder();
		String[] input = new String[1];
		String[] output = new String[1];
		input[0]="12345678953279416783469835797378342394830735020284753";
		x.Compress(input,output);
		x.Decompress(output,input );
		
	}
}