/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patron2;

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

public class Supervision {
    
    public static void main(String args[]) throws StaleProxyException 
     {
        Runtime rt = Runtime.instance();

	// Exit the JVM when there are no more containers around

	rt.setCloseVM(true);

	// Create a default profile

	Profile profile = new ProfileImpl("localhost", 1099, "main");

	AgentContainer mainContainer = jade.core.Runtime.instance().createMainContainer(profile);

        Object[] hum=new Object[20];
        hum[0]="humano";
        
        
        //Object[] ag=new Object[20];
        //ag[0]="agente";
       Object agen[] = {"agente"};

	AgentController rma = null;
        AgentController mma = null;

	try {

		rma = mainContainer.createNewAgent("agente", "patron2.singleAgent", hum);
		rma.start();
                mma = mainContainer.createNewAgent("humano", "patron2.singleHuman", agen);
                mma.start();

	} catch (StaleProxyException e) {
            
		e.printStackTrace();
	}

     }
}
