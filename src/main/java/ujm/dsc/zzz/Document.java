package ujm.dsc.zzz;

import java.util.HashMap;
import java.util.Map;

public class Document implements Comparable<Document> {
	private int DocId;

	// w (t, d) = tf (t, d) × idf (t)
	// RSV (d, q) =sum (t∈q w (t, d) · w (t, q) )

	private Map<Query, Double> mapScore = new HashMap<Query, Double>();

	public Document(int docId) {
		super();
		DocId = docId;
	}

	public int getDocId() {
		return DocId;
	}

	public void setDocId(int docId) {
		DocId = docId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + DocId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (DocId != other.DocId)
			return false;
		return true;
	}

	public void setScore(Query q, double score) {
		mapScore.put(q, score);

	}

	@Override
	public int compareTo(Document d) {
		// TODO Auto-generated method stub
		return this.DocId = d.getDocId();
	}

}
