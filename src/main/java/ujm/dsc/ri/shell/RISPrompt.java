package ujm.dsc.ri.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RISPrompt extends DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "ris > ";
	}

	@Override
	public String getProviderName() {
		return "ris shell prompt";
	}

}
