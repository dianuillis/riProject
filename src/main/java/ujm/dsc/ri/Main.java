package ujm.dsc.ri;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.Bootstrap;

public class Main {

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
		Bootstrap bs = new Bootstrap();
		bs.getJLineShellComponent().setApplicationContext(context);
		bs.getJLineShellComponent().start();
		context.registerShutdownHook();
	}

}
