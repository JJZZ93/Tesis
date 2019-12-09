/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patron1;

import jade.core.AID;
import javax.swing.JOptionPane;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.ProposeResponder;
/**
 *
 * @author josezamora
 */
public class singleHuman extends Agent{
     protected void setup() {
 
        //Creamos la plantilla a emplear, para solo recibir mensajes con el protocolo FIPA_PROPOSE y la performativa PROPOSE
        MessageTemplate plantilla = ProposeResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_PROPOSE);
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            System.out.println(this.getLocalName()+": Agente necesito que realices la tarea asignada...");
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            for (int i = 0; i < args.length; ++i)
                msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setContent("tarea a 5 kms");
 
            addBehaviour(new ManejadorInitiator(this,msg));
 
        }
        else System.out.println("Debe ingresar como parámetro el nombre del agente.");
          
           
        
        //Añadimos el comportamiento "responderSalirClase()"
        this.addBehaviour(new ResponderSalirClase(this, plantilla));
        
            
    }
 
    //Método que permite al usuario decidir si acepta la propuesta o si la rechaza.
    private boolean checkContent(String agente, String propuesta) {
        if (JOptionPane.showConfirmDialog(null, propuesta, agente + " dice:", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
 
    private class ResponderSalirClase extends ProposeResponder {
 
        public ResponderSalirClase(Agent agente, MessageTemplate plantilla) {
            super(agente, plantilla);
        }
 
        //Preparación de la respuesta. Recibe un mensaje PROPOSE y, según su contenido, acepta o no.
 
        protected ACLMessage prepareResponse(ACLMessage propuesta)
                throws NotUnderstoodException {
            System.out.printf("%s: Proposicion recibida de %s.\n", this.myAgent.getLocalName(), propuesta.getSender().getLocalName());
            System.out.printf("%s: La propuesta es: %s.\n", this.myAgent.getLocalName(), propuesta.getContent());
 
            //Comprueba los datos de la propuesta
            if (singleHuman.this.checkContent(propuesta.getSender().getLocalName(), propuesta.getContent())) {
                //Aceptación de la propuesta.
                System.out.printf("%s: Dando permiso para ejecutar tarea.\n", this.myAgent.getLocalName());
 
                //Se crea la respuesta al mensaje con la performativa ACCEPT_PROPOSAL, pues se acepta.
                ACLMessage agree = propuesta.createReply();
                agree.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
 
                return agree;
            } else {
 
                //Rechazo de la propuesta.
                System.out.printf("%s: Prohibiendo que se ejecute tarea.\n", this.myAgent.getLocalName());
 
                //Se crea la respuesta al mensaje con la performativa REJECT_PROPOSAL, pues se rechaza.
                ACLMessage refuse = propuesta.createReply();
                refuse.setPerformative(ACLMessage.REJECT_PROPOSAL);
 
                return refuse;
            }
        }
    }
    
    
    class ManejadorInitiator extends AchieveREInitiator
    {
        public ManejadorInitiator(Agent a,ACLMessage msg) {
            super(a,msg);
        }
 
        protected void handleAgree(ACLMessage agree)
        {
            System.out.println("Agente: " + agree.getSender().getName()
                    + " informa que está atendiendo tu solicitud.");
        }
 
        protected void handleRefuse(ACLMessage refuse)
        {
            System.out.println("Agente: " + refuse.getSender().getName()
                    + " responde que la Información se fuera del radio de accion. No se completará la tarea!!!");
        }
 
        protected void handleNotUnderstood(ACLMessage notUnderstood)
        {
            System.out.println("Agente: " +notUnderstood.getSender().getName()
                    + " no puede entender el mensaje.");
        }
 
        protected void handleInform(ACLMessage inform)
        {
            System.out.println("Agente:  " + inform.getSender().getName()
                    + " informa que han atendido el accidente.");
        }
 
        protected void handleFailure(ACLMessage fallo)
        {
            if (fallo.getSender().equals(myAgent.getAMS())) {
                System.out.println("Agente no existe");
            }
            else
            {
                System.out.println("Fallo en el agente " + fallo.getSender().getName()
                        + ": " + fallo.getContent().substring(1, fallo.getContent().length()-1));
            }
        }

    }
}