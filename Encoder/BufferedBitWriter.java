import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BufferedBitWriter {
	private byte currentByte;     	// The byte that is being filled
	private byte numBitsWritten;  	// Number of bits written to the current byte
	public static int maxBytes = 1000000000;  // So can bail out if file gets too big
	private int totalBytes;			// Exception when exceeds max
	private BufferedOutputStream output; // The output byte stream

	public BufferedBitWriter(String pathName) throws FileNotFoundException {
		currentByte = 0;
		numBitsWritten = 0;
		totalBytes = 0;
		output = new BufferedOutputStream(new FileOutputStream(pathName));
	}

	public void writeBit(boolean bit) throws IOException {
		numBitsWritten++;
		currentByte |= (bit?1:0) << (8 - numBitsWritten);
		if(numBitsWritten == 8) {  // Have we got a full byte?
			output.write(currentByte);
			numBitsWritten = 0;
			currentByte = 0;
			totalBytes++;
			if (totalBytes >= maxBytes) throw new IOException("file overflow");
		}
	}

	public void close() throws IOException {
		output.write(currentByte);
		output.write(numBitsWritten);
		output.close();
	}
}
