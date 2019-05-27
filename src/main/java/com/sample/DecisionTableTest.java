package com.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.edenor.model.*;

/**
 * This is a sample class to launch a rule.
 */
public class DecisionTableTest {

	static SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
	
    public static final void main(String[] args) {
    	KieSession kSession=null;
    	
        try {
        	
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	kSession = kContainer.newKieSession("ksession-dtables");

            
            Consumo_106 consumo = new Consumo_106();
            consumo.setFecha_inicial(format.parse("25-Feb-2019"));
            consumo.setFecha_lect(format.parse("25-Mar-2019"));

            consumo.setConsumo(84); 
            consumo.setId_usuario("0000040279");

            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId_usuario(consumo.getId_usuario());
            
            kSession.insert(consumo);                        
            kSession.setGlobal("resultado", liquidacion);
            
            
            kSession.fireAllRules();
         
            //imprimirDrl();
            
            System.out.println("Corrida 1: Resultado="+liquidacion);
            kSession = kContainer.newKieSession("ksession-dtables");
            
            Consumo_106 consumo2 = new Consumo_106();
            consumo2.setFecha_inicial(format.parse("23-Feb-2019"));
            consumo2.setFecha_lect(format.parse("25-Mar-2019"));

            consumo2.setConsumo(276); 
            consumo2.setId_usuario("0000233641");

            Liquidacion liquidacion2 = new Liquidacion();
            liquidacion2.setId_usuario(consumo2.getId_usuario());
            
            kSession.insert(consumo2);                        
            kSession.setGlobal("resultado", liquidacion2);
            
            
            kSession.fireAllRules();
  
            
            System.out.println("Corrida 2: Resultado="+liquidacion2);
             
        } catch (Throwable t) {
            t.printStackTrace();
            imprimirDrl();
        
           	
        
        }
        

    }

    private static void imprimirDrl() {
        InputStream is =null;
        try {
        	// assign the excel to the input stream
        	// mention the local directory path where your excel is kept
        	// you can take any decision table (excel sheet) for testing
        	is= new FileInputStream("/home/jginzbur/Preventa/AR/Edenor/Workspace/edenor_drools/src/main/resources/com/sample/dtables/tarifas.xls");
        	} catch (FileNotFoundException e) {
        	e.printStackTrace();
        	}
        	// create compiler class instance
        	SpreadsheetCompiler sc = new SpreadsheetCompiler();
        	// compile the excel to generate the (.drl) file
        	String drl=sc.compile(is, InputType.XLS);
        	// check the generated (.drl) file
        	System.out.println("Generate DRL file is –: ");
        	System.out.println(drl);
		
	}
    
 
    

    
    
    
    public static class Message {

        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }

}
