package base;
/**
 * Assignment 2
 * Submitted by: 
 * Shir Elbaz. 	ID# 204405690
 * Gal Arus. 	ID# 204372619
 * Matan Danino ID# 304802887
 */


public interface Compressor
{
	abstract public void Compress(String[] input_names, String[] output_names);
	abstract public void Decompress(String[] input_names, String[] output_names);

	abstract public byte[] CompressWithArray(String[] input_names, String[] output_names);
	abstract public byte[] DecompressWithArray(String[] input_names, String[] output_names);
}

