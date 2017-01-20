package ujm.dsc.ri.core;

import lombok.Data;

@Data
public class Score implements Comparable<Score> {

	private String sec;

	private Double score;

	public Score(String sec, Double score) {
		super();
		this.sec = sec;
		this.score = score;
	}

	@Override
	public int compareTo(Score s) {
		return (int) (this.score - s.score);
	}

}
