import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.CRC32;

public class Ex2Client {


	public static void main(String[] args) {
		try (Socket socket = new Socket("18.221.102.182", 38102)) {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			System.out.println("Connected to server.");

			byte[] bytes = receiveBytes(is);
			sendCRC(os, bytes);

			getServerResponse(is);
			System.out.println("Disconnected from server.");
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendCRC(OutputStream os, byte[] bytes) throws IOException {
		CRC32 crc = new CRC32();
		crc.update(bytes);

		long crcValue = crc.getValue();
		System.out.printf("\nGenerated CRC32: %02X", crcValue);
		System.out.println("");

		for (int i = 3; i >= 0; i--) {
			os.write((int) crcValue >> (i * 8));
		}
	}

	private static void getServerResponse(InputStream is) throws IOException {
		int response = is.read();
		if (response == 1) {
			System.out.println("Response good.");
		} else {
			System.out.println("Response bad.");
		}
	}

	private static byte[] receiveBytes(InputStream is) throws IOException {
		byte[] bytes = new byte[100];
		byte firstByte;
		byte secondByte;
		byte comByte;

		System.out.println("Received bytes: ");
		for (int i = 0; i < 100; i++) {
			firstByte = (byte) is.read();
			secondByte = (byte) is.read();

			firstByte = (byte) (firstByte << 4);
			comByte = (byte) (firstByte + secondByte);

			comByte = (byte) (firstByte + secondByte);
			bytes[i] = comByte;

			if (i % 20 == 0 && i != 0) {
				System.out.println("");
			}

			System.out.printf("%02X", bytes[i]);
		}

		return bytes;
	}
}