package ujm.dsc.ri.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RISBanner extends DefaultBannerProvider {

	public String getBanner() {
		StringBuffer b = new StringBuffer();
		b.append(" ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░█▀▀▀▀▀▀▀█░▌ ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀▀▀ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░▌       ▐░▌     ▐░▌     ▐░▌ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░█▄▄▄▄▄▄▄█░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄▄▄ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░░░░░░░░░░░▌     ▐░▌     ▐░░░░░░░░░░░▌ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░█▀▀▀▀█░█▀▀      ▐░▌      ▀▀▀▀▀▀▀▀▀█░▌ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░▌     ▐░▌       ▐░▌               ▐░▌ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░▌      ▐░▌  ▄▄▄▄█░█▄▄▄▄  ▄▄▄▄▄▄▄▄▄█░▌ " + OsUtils.LINE_SEPARATOR);
		b.append("▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌ " + OsUtils.LINE_SEPARATOR);
		b.append(" ▀         ▀  ▀▀▀▀▀▀▀▀▀▀▀  ▀▀▀▀▀▀▀▀▀▀▀ " + OsUtils.LINE_SEPARATOR);
		b.append("version: " + this.getVersion());
		return b.toString();
	}

	public String getVersion() {
		return "1.0-SNAPSHOT";
	}

	public String getWelcomeMessage() {
		return "welcome to RIS shell";
	}

	@Override
	public String getProviderName() {
		return "ris";
	}

}