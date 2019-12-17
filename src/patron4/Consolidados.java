/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patron4;

/**
 *
 * @author josezamora
 */
import jade.core.Agent;
import jade.BootProfileImpl;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.leap.Properties;
import jade.wrapper.*;

public class Consolidados {
    
    public static void main(String args[]) throws StaleProxyException 
     {
        Runtime rt = Runtime.instance();

	// Exit the JVM when there are no more containers around

	rt.setCloseVM(true);

	// Create a default profile

	Profile profile = new ProfileImpl("localhost", 1099, "main");

	AgentContainer mainContainer = jade.core.Runtime.instance().createMainContainer(profile);

        Object[] hum1=new Object[20];
        hum1[0]="humano1";
        
        Object[] hum2=new Object[20];
        hum2[0]="humano2";
        
        
        //Object[] ag=new Object[20];
        //ag[0]="agente";
       Object agen[] = {"agente"};

	AgentController rma = null;
        AgentController rna = null;
        AgentController mma = null;

	try {

		rma = mainContainer.createNewAgent("agente", "patron4.singleAgent", hum1);
		rma.start();
                rna = mainContainer.createNewAgent("humano1", "patron4.singleHuman1", hum1);
		rna.start();
                mma = mainContainer.createNewAgent("humano2", "patron4.singleHuman2", agen);
                mma.start();

	} catch (StaleProxyException e) {
            
		e.printStackTrace();
	}

     }
}
