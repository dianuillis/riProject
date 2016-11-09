package com.ujm.ri2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Main {
	
	public static void main(String[] args) throws IOException{
		
		// nombre de document total par exemple
		int nbdoc=9800;
        
		 // on cree une liste des requete
		 List<Query> listQuery=new ArrayList<Query>();
		 // on cree une liste des documents
		Set<Document> listDoc = new HashSet<Document>();
		
		
		
		// on cree le postingList
		
		Utilities util=new Utilities();
		util.createPosting();
		
		Map<Term, Map<Document,Integer>> post=util.getInvertedIndex();
		
		
		
			
		//w (t, d) = tf (t, d) × idf (t)
		//RSV (d, q) =sum (t∈q w (t, d) · w (t, q) )
		
		
		/*Format ltn */
		
//		tfl(t, d) = 0 si tf (t, d) = 0
//				    1 + log(tf (t, d)) sinon
//		idfi(t)=log(|D|/df(t))
		
		
		
	// pour chaque term on calcul le (tf, df,idf)
		
	for(Term t : post.keySet()){
		
		    t.setTf(post.get(t));
		    t.setDf(post.get(t).size());
		    t.setIdf(Math.log(nbdoc/t.getDf()));
		    
		  //w (t, d) = tf (t, d) × idf (t)
		    t.calculPoidDoc();
		    
		  //w (t, q) = tf (t, q) × idf (t)
		  // pour le moment =1
		    t.calculPoidQuery();
		    
		    
    
	}
	
	
/*
 * A modifier (construire la liste des documents et des requetes )
 * 
 * 
		 List<Query> listQuery=new ArrayList<Query>();
		              listQuery=util.getListQuery();
		             
		 
		Set<Document> listDoc = new HashSet<Document>();
		              listDoc=util.ListQuery();
 * 
 */
	

	
/*
 *  calcul du score pour le document et la requete
	RSV (d, q) =sum (t∈q w (t, d) · w (t, q) )
	on calcul le score du document pour chaque requete
 * 	
 */
	
	
	for (Document doc : listDoc){
		
		for(Query q : listQuery){
			double score =0;
			
			for(Term t : q.getListTerm()){
				score +=t.getPoid(doc)*t.getPoid(q);
			}
			
			
			
			doc.setScore(q,score);
			
		}
		
		
		
	}

	
	
	
	// on fait la formalisation  ltn 
	
		
   //tfl(t, d) = 0 si tf (t, d) = 0
  //				              1 + log(tf (t, d)) sinon
  
	
	
	for(Term t : post.keySet()){
		
	    t.setTfl();
	   
	    
	    

	}
	
	
	// en fin on genere les  fichiers txt 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	
	
	
	
	
		
		
	

}
