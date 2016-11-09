package com.ujm.ri2;
import java.util.ArrayList;
import java.util.List;

public class Query {
	private int Id;
	private List<Term> listTerm=new ArrayList<Term>();
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Id;
		return result;
	}
	
	
	
	
	
	public int getId() {
		return Id;
	}





	public void setId(int id) {
		Id = id;
	}





	public List<Term> getListTerm() {
		return listTerm;
	}





	public void setListTerm(List<Term> listTerm) {
		this.listTerm = listTerm;
	}





	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (Id != other.Id)
			return false;
		return true;
	}
	
	

}
