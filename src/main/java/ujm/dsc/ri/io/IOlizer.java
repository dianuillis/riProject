package ujm.dsc.ri.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class IOlizer {

	private static final Logger log = LogManager.getLogger(IOlizer.class.getName());

	public void serialize(Object object, String filePath) throws IOException {
		log.info("Saving to " + filePath);
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("output/" + filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.close();
		fos.close();
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
	}

	public Object dserialize(String filePath) throws IOException, ClassNotFoundException {
		log.info("Reading from " + filePath);
		long start = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream("output/" + filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object object = ois.readObject();
		ois.close();
		fis.close();
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return object;
	}

}
