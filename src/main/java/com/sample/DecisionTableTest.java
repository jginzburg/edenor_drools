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
    
    public static  int diferenciaDias(Date fechaPrimera, Date fechaSegunda) {
		int dias=(int) ((fechaSegunda.getTime()-fechaPrimera.getTime())/86400000);
		return dias;
	} 
    
	public static int diferenciaDias(String fechaPrimera, String fechaSegunda) {
		

		try {
			Date datePrimera = format.parse(fechaPrimera);
			Date dateSegunda = format.parse(fechaSegunda);
			return diferenciaDias(datePrimera, dateSegunda);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}

	public static class Consumo_106 {
    	
		Date fecha_inicial;
    	int consumo;

    	String id_usuario;
    	Date fecha_lect;
    	
    	public String getId_usuario() {
			return id_usuario;
		}
		public void setId_usuario(String id_usuario) {
			this.id_usuario = id_usuario;
		}
		public int getConsumo() {
			return consumo;
		}
		public void setConsumo(int consumo) {
			this.consumo = consumo;
		}
    	public Date getFecha_lect() {
			return fecha_lect;
		}
		public void setFecha_lect(Date fecha_lect) {
			this.fecha_lect = fecha_lect;
		}
		public Date getFecha_inicial() {
			return fecha_inicial;
		}
		public void setFecha_inicial(Date fecha_inicial) {
			this.fecha_inicial = fecha_inicial;
		}
		
		public int getDias() {
			return diferenciaDias(fecha_inicial, fecha_lect);
		}
    }
    
    public static class PeriodoLiquidacion {
    	
    	SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

		String id_usuario;
    	String ct;
    	double consumoProrrateado;
    	int diasProrreatos;
    	double cf;
    	double cv;
    	Date inicioPeriodo, finPeriodo;
    	
    
		public Date getInicioPeriodo() {
			return inicioPeriodo;
		}

		public void setInicioPeriodo(Date inicioPeriodo) {
			this.inicioPeriodo = inicioPeriodo;
		}

		public void setInicioPeriodo(String inicioPeriodo) {
			try {
				setInicioPeriodo(format.parse(inicioPeriodo));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		public Date getFinPeriodo() {
			return finPeriodo;
		} 

		public void setFinPeriodo(Date finPeriodo) {
			this.finPeriodo = finPeriodo;
			actualizarProrrateo();
		}
		
		public void setFinPeriodo(String finPeriodo) {
			try {
				setFinPeriodo(format.parse(finPeriodo));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void actualizarProrrateo() {
			long diff = finPeriodo.getTime() - inicioPeriodo.getTime();
			float days = (diff / (1000*60*60*24));
			System.out.println("dias prorrateados:"+ days);
			diasProrreatos = Math.round(days);
		}

		public PeriodoLiquidacion(String ct) {
			super();
			this.ct = ct;
		}
    	
		public String getId_usuario() {
			return id_usuario;
		}
		public void setId_usuario(String id_usuario) {
			this.id_usuario = id_usuario;
		}
		public String getCt() {
			return ct;
		}
		public void setCt(String ct) {
			this.ct = ct;
		}
		public double getConsumoProrrateado() {
			return consumoProrrateado;
		}
		public void setConsumoProrrateado(double consumoProrrateado) {
			this.consumoProrrateado = consumoProrrateado;
		}
		public int getDiasProrrateados() {
			return diasProrreatos;
		}
		public void setDiasProrreateados(int diasProrreatos) {
			this.diasProrreatos = diasProrreatos;
		}
		public double getCf() {
			return cf;
		}
		public void setCf(double cf) {
			this.cf = cf;
		}
		public double getCv() {
			return cv;
		}
		public void setCv(double cv) {
			this.cv = cv;
		}

		
    	
		@Override
		public String toString() {
			return "PeriodoLiquidacion [id_usuario=" + id_usuario + ", ct=" + ct + ", consumoProrrateado="
					+ consumoProrrateado + ", diasProrreatos=" + diasProrreatos + ", cf=" + cf + ", cv=" + cv
					+ ", inicioPeriodo=" + inicioPeriodo + ", finPeriodo=" + finPeriodo + "]\n";
		}

		
		
    	
		

    	
    }
    
    public static class Liquidacion {
    	String tarifa;
		String id_usuario;
    	Date fecha_lect;
    	Date fecha_inicial;
    	int consumo;
    	double cf;
    	double cv;
    	List<PeriodoLiquidacion> periodos = new ArrayList<>();

    	public void addPeriodo(PeriodoLiquidacion p) {
    		periodos.add(p);
    	}
  
		public String getId_usuario() {
			return id_usuario;
		}
		public void setId_usuario(String id_usuario) {
			this.id_usuario = id_usuario;
		}
		public Date getFecha_lect() {
			return fecha_lect;
		}
		public void setFecha_lect(Date fecha_lect) {
			this.fecha_lect = fecha_lect;
		}
		public Date getFecha_inicial() {
			return fecha_inicial;
		}
		public void setFecha_inicial(Date fecha_inicial) {
			this.fecha_inicial = fecha_inicial;
		}
		public int getConsumo() {
			return consumo;
		}
		public void setConsumo(int consumo) {
			this.consumo = consumo;
		}
		public double getCf() {
			return cf;
		}
		public void setCf(double cf) {
			this.cf = cf;
		}
		public double getCv() {
			return cv;
		}
		public void setCv(double cv) {
			this.cv = cv;
		}

		public String getTarifa() {
			return tarifa;
		}
		public void setTarifa(String tarifa) {
			this.tarifa = tarifa;
		}
		
		public String sumarPeriodos() {
			double cf=0;
			double cv=0;
			for (PeriodoLiquidacion p : periodos ) {
				cf += p.getCf();
				cv += p.getCv();
			}
			return "Sum CF=" + cf + " Sum CV=" + cv;
		}
    	

    	
    	
	
		@Override
		public String toString() {
			return "Liquidacion [tarifa=" + tarifa + ", id_usuario=" + id_usuario + ", fecha_lect=" + fecha_lect
					+ ", fecha_inicial=" + fecha_inicial + ", consumo=" + consumo + ", cf=" + cf + ", cv=" + cv
					+ ", \nperiodos=" + periodos + ", format="  + "]"+ sumarPeriodos();
		}


		
	
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
