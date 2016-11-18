package ujm.dsc.ri.core;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
@EqualsAndHashCode(exclude = { "value" })
public class Score implements Serializable, Comparable<Score> {

	private static final long serialVersionUID = -4302546949972038261L;

	private Long docId;

	private Long queryId;

	private Double value;

	@Override
	public int compareTo(Score score) {
		return (int) (this.value - score.getValue());
	}

}
