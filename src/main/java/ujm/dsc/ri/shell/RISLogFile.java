package ujm.dsc.ri.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RISLogFile extends DefaultHistoryFileNameProvider {

	public String getHistoryFileName() {
		return "ris.log";
	}

	@Override
	public String getProviderName() {
		return "ris shell log file";
	}

}
