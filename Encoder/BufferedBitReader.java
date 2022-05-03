import java.io.*;

public class BufferedBitReader {

	int current;
	int next;
	int afterNext;
	int bitMask;

	BufferedInputStream input;

	public BufferedBitReader(String pathName) throws IOException {
		input = new BufferedInputStream(new FileInputStream(pathName));

		current = input.read();
		if(current == -1)
			throw new EOFException("File did not have two bytes");

		next = input.read();
		if(next == -1) 
			throw new EOFException("File did not have two bytes");	

		afterNext = input.read();
		bitMask = 128;
	}

	public boolean hasNext() {
		return afterNext != -1 || next != 0;
	}

	public boolean readBit() throws IOException {
		boolean returnBit;

		if(afterNext == -1)

			if(next == 0)
				throw new EOFException("No more bits");
			else {
				if((bitMask & current) == 0)
					returnBit = false;
				else 
					returnBit = true;

				next--;
				bitMask = bitMask >> 1;
				return returnBit;
			}
		else {
			if((bitMask & current) == 0)
				returnBit = false;
			else 
				returnBit = true;

			bitMask = bitMask >> 1;

			if(bitMask == 0)  {
				bitMask = 128;
				current = next;
				next = afterNext;
				afterNext = input.read();
			}
			return returnBit;
		}
	}

	public void close() throws IOException {
		input.close();
	}



}
