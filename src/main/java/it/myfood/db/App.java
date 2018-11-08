package it.myfood.db;

import java.net.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

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
	
	static RepositoryConnection conn = null;
	
	@SuppressWarnings("restriction")
	public static void main(String[] args) throws IOException {

		//Initialized Rdf4j database
		String filename = "my-food-ontology-rdfxml.owl";
		InputStream input = App.class.getResourceAsStream("/" + filename);
		Model model = null;

		try {
			model = Rio.parse(input, "", RDFFormat.RDFXML);
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (UnsupportedRDFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Repository db = new SailRepository(new MemoryStore());
		db.initialize();
		
		conn = db.getConnection();
		
		try {
			conn.add(model);
			RepositoryResult<Statement> result = conn.getStatements(null, null, null, true ); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		  
		
		// To make it work on heroku
		int port = Integer.valueOf(System.getenv("PORT"));
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
				
		//to make it work locally
		//HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);
		  
		//Route to welcome page
		HttpContext context = server.createContext("/");
		context.setHandler(App::handleRequest);
		  
		//Route to /data/query
		HttpContext context_query = server.createContext("/data/query");
		context_query.setHandler(App::handleDB);
		  
		server.start();
		
	}
	
	

	@SuppressWarnings("restriction")
	private static void handleRequest(HttpExchange exchange) throws IOException {
		
		URI requestURI = exchange.getRequestURI();
		String response = "Welcome to myfood DB!!! " + requestURI;
		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
		
	}
	
	
	
	@SuppressWarnings("restriction")
	private static void handleDB(HttpExchange exchange) throws IOException {
		
		URI requestURI = exchange.getRequestURI();
	      
		//Let's try a query
	      
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
			e.printStackTrace();
		}
		
		TupleQueryResult result = null;
		
		try {
			result = query.evaluate();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
	
		String response = null;
		
		try {
			while (result.hasNext()) {
				BindingSet solution = result.next();
				response += "?subject = " + solution.getValue("subject") +"\n";		   
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		  	    
		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
  	    
	  }
		
	
	
}
    
    

