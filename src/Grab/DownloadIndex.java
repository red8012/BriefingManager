package Grab;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadIndex implements Runnable {
	String yy, mm;
	String url = "http://www.twse.com.tw/ch/trading/indices/MI_5MINS_HIST/MI_5MINS_HIST.php?myear=[yy]&mmon=[mm]";

	public DownloadIndex(Integer yyyy, Integer mm) {
		yyyy -= 1911;
		this.yy = yyyy.toString();
		this.mm = (mm < 10 ? "0" : "") + mm.toString();
	}

	public void run() {
		try {
			url = url.replace("[yy]", yy).replace("[mm]", mm);
			URLConnection conn = new URL(url).openConnection();
			ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
			FileOutputStream fos = new FileOutputStream("temp/index/" +
					(yy.length() < 3 ? ("0" + yy) : yy) + mm + ".html");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			rbc.close();
			fos.close();

			System.out.print("*");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
