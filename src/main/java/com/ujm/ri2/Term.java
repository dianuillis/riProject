package com.ujm.ri2;
import java.util.HashMap;
import java.util.Map;

public class Term implements Comparable<Term>{
	
	
	private String id;
	private int  df;
	private double idf;
	/* nombre d'occurrence du term dans le doc*/
	private Map<Document , Integer> tf=new HashMap<Document, Integer>();
	private Map<Document , Double> poid=new HashMap<Document, Double>();
	private Double poidQ=0.0;
	
	

	public Term(String id){
		this.id=id;
		
	}
	
	
	
	
	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public int getTf(Document document){
		
		for(Document doc : tf.keySet() ){
			if(doc.equals(document) ){
				return tf.get(doc);
			}
		}
		
		return 0;
		
	}
	
	
	//private Map<Document , Integer> tf=new HashMap<Document, Integer>();
	//w (t, d) = tf (t, d) × idf (t)
	public void calculPoidDoc() {
		
		for(Document doc : tf.keySet()){
			poid.put(doc, this.getTf(doc)*this.getIdf());
		}
		
		
	}


	public void calculPoidQuery() {
		 poidQ=1.0;
		
	}

	
	
	
	



	public int getDf() {
		return df;
	}




	public void setDf(int df) {
		this.df = df;
	}









	public double getIdf() {
		return idf;
	}









	public void setIdf(double d) {
		this.idf = d;
	}









	


	public double getPoid(Document doc) {
		
		return poid.get(doc);
	}












	public Map<Document, Integer> getTf() {
		return tf;
	}





	public void setTf(Map<Document, Integer> tf) {
		this.tf = tf;
	}



	



	public double getPoid(Query q) {
		return poidQ;
	}


	

	//1 + log(tf (t, d))
	public void setTfl() {
		for(Document doc : tf.keySet()){
			double val=tf.get(doc);
			tf.put(doc, (int) (1+Math.log(val)));
		}
		
		
		
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Term other = (Term) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}




	@Override
	public int compareTo(Term t) {
		// TODO Auto-generated method stub
		if(this.equals(t)){
			return 0;
		}
		return -1;
	}

























	











	
	
	
	
	
	
}
