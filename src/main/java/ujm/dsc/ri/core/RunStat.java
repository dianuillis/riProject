package ujm.dsc.ri.core;

public class RunStat {

	private String runName;
	private Double magp;
	private Double recall;
	private Double precision;

	public Double getMagp() {
		return magp;
	}

	public Double getPrecision() {
		return precision;
	}

	public Double getRecall() {
		return recall;
	}

	public String getRunName() {
		return runName;
	}

	public void setMagp(Double magp) {
		this.magp = magp;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public void setRecall(Double recall) {
		this.recall = recall;
	}

	public void setRunName(String runName) {
		this.runName = runName;
	}

}
