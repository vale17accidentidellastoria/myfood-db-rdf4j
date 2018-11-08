package it.myfood.db;

import java.net.*;
import java.io.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
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

public class App{
	/*
    public static void main(String[] args) throws RepositoryException, RDFHandlerException {

    		String filename = "my-food-ontology-rdfxml.owl";
    		InputStream input = App.class.getResourceAsStream("/" + filename);
    		Model model = null;
			try {
				model = Rio.parse(input, "", RDFFormat.RDFXML);
			} catch (RDFParseException e) {
				e.printStackTrace();
				System.out.println(" i am here" );
			} catch (UnsupportedRDFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Repository db = new SailRepository(new MemoryStore());
    		db.initialize();

    		RepositoryConnection conn = db.getConnection();
    		try {
    			conn.add(model);
    			RepositoryResult<Statement> result = conn.getStatements(null, null, null, true ); 
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	
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
    		
    	    try {
				while (result.hasNext()) {
				    BindingSet solution = result.next();
				    System.out.println("?subject = " + solution.getValue("subject"));
				}
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
			}
    	    
    	
	}*/	
	
	@SuppressWarnings("restriction")
	public static void main(String[] args) throws IOException {
		  int port = Integer.valueOf(System.getenv("PORT"));
		  HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		  HttpContext context1 = server.createContext("/");
	      HttpContext context2 = server.createContext("/example");
	      context1.setHandler(App::handleRequest);
	      context2.setHandler(App::handleRequest);
	      server.start();
	  }

	@SuppressWarnings("restriction")
	private static void handleRequest(HttpExchange exchange) throws IOException {
		  URI requestURI = exchange.getRequestURI();
	      printRequestInfo(exchange);
	      String response = "This is the response at " + requestURI;
	      exchange.sendResponseHeaders(200, response.getBytes().length);
	      OutputStream os = exchange.getResponseBody();
	      os.write(response.getBytes());
	      os.close();
	  }
	
	private static void printRequestInfo(HttpExchange exchange) {
	      System.out.println("-- headers --");
	      Headers requestHeaders = exchange.getRequestHeaders();
	      requestHeaders.entrySet().forEach(System.out::println);

	      System.out.println("-- principle --");
	      HttpPrincipal principal = exchange.getPrincipal();
	      System.out.println(principal);

	      System.out.println("-- HTTP method --");
	      String requestMethod = exchange.getRequestMethod();
	      System.out.println(requestMethod);

	      System.out.println("-- query --");
	      URI requestURI = exchange.getRequestURI();
	      String query = requestURI.getQuery();
	      System.out.println(query);
	  }
	
}
    
    

