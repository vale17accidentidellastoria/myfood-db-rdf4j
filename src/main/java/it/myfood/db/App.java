package it.myfood.db;
import info.aduna.iteration.Iterations;

import org.openrdf.model.impl.LinkedHashModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import org.eclipse.rdf4j.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class App {
    public static void main(String[] args) throws RepositoryException, RDFHandlerException {
    		
    		// First load our RDF file as a Model.
    		String filename = "my-food-ontology-rdfxml.owl";
    		InputStream input = App.class.getResourceAsStream("/" + filename);
    		Model model = null;
			try {
				model = Rio.parse(input, "", RDFFormat.RDFXML);
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedRDFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			//Prints all the triples in a model
			for (Statement statement: model) {
				System.out.println(statement);
			}
			*/

    		//Creating the database
			Repository db = new SailRepository(new MemoryStore());
    		db.initialize();

    		// Open a connection to the database
    		RepositoryConnection conn = db.getConnection();
    		try {
    			// add the model
    			conn.add(model);

    			// let's check that our data is actually in the database
    			
    			RepositoryResult<Statement> result = conn.getStatements(null, null, null, true ); 
    				/*	
    				while (result.hasNext()) {
    					Statement st = result.next();
    					System.out.println("db contains: " + st);
    				}
    				*/
    
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    		// We do a simple SPARQL SELECT-query that retrieves all resources of
    	    // type `ex:Artist`, and their first names.
    	    String queryString = "PREFIX ex: <http://example.org/> \n";
    	    queryString += "PREFIX foaf: <" + FOAF.NAMESPACE + "> \n";
    	    queryString += "SELECT ?subject ?predicate ?object \n";
    	    queryString += "WHERE { \n";
    	    queryString += "    ?subject ?predicate ?object \n";
    	    queryString += "}";
    	    
    	    TupleQuery query = null;
			try {
				query = conn.prepareTupleQuery(new QueryLanguage("SPARQL"), queryString);
			} catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    // A QueryResult is also an AutoCloseable resource, so make sure it gets
    	    // closed when done.
    	    TupleQueryResult result = null;
			try {
				result = query.evaluate();
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		// we just iterate over all solutions in the result...
    		
    	    try {
				while (result.hasNext()) {
				    BindingSet solution = result.next();
				    // ... and print out the value of the variable bindings
				    // for ?s and ?n
				    System.out.println("?subject = " + solution.getValue("subject"));
				}
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    
    	
    		}
}
