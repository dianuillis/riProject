package ujm.dsc.ri.core;

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

	public Double getScore() {
		return score;
	}

	public String getSec() {
		return sec;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public void setSec(String sec) {
		this.sec = sec;
	}

}
