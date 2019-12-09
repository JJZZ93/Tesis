/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patron1;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ProposeInitiator;
import java.util.StringTokenizer;
/**
 *
 * @author josezamora
 */
public class singleAgent extends Agent{
    public double DISTANCIA_MAX;
    
    protected void setup() {
        //Se crea un mensaje PROPOSE. Se quiere pedir permiso para salir de clase.
        ACLMessage mensaje = new ACLMessage(ACLMessage.PROPOSE);
        mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
        mensaje.setContent("¿Estás seguro de realizar la tarea?");
 
        //Se añade el destinatario.
        AID id = new AID();
        id.setLocalName("humano");
        mensaje.addReceiver(id);
        
         DISTANCIA_MAX=(Math.random()*10);

        MessageTemplate protocolo = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        MessageTemplate performativa = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        MessageTemplate plantilla = MessageTemplate.and(protocolo, performativa);
 
        addBehaviour(new ManejadorResponder(this, plantilla));
 
        //Añadir el comportamiento
        this.addBehaviour(new SalirClase(this, mensaje));
                
    }
 
    private class SalirClase extends ProposeInitiator {
        public SalirClase(Agent agente, ACLMessage mensaje) {
            super(agente, mensaje);
        }
 
        //Manejar la respuesta en caso que acepte: ACCEPT_PROPOSAL
 
        protected void handleAcceptProposal(ACLMessage aceptacion) {
            System.out.printf("%s: Solicitud aceptada.\n", this.myAgent.getLocalName());
            System.out.printf("%s: Ejecutando tarea...\n", this.myAgent.getLocalName());
        }
 
        //Manejar la respuesta en caso que rechace: REJECT_PROPOSAL
 
        protected void handleRejectProposal(ACLMessage rechazo) {
            System.out.printf("%s: Solicitud denegada.\n", this.myAgent.getLocalName());
            System.out.printf("%s: Tarea cancelada.\n", this.myAgent.getLocalName());
        }
    }
    
    class ManejadorResponder extends AchieveREResponder
    {
        public ManejadorResponder(Agent a,MessageTemplate mt) {
            super(a,mt);
        }
 
        protected ACLMessage handleRequest(ACLMessage request)throws NotUnderstoodException, RefuseException
        {
            System.out.println(getLocalName()+": Se asigno la tarea de " + request.getSender().getName());
            StringTokenizer st=new StringTokenizer(request.getContent());
            String contenido=st.nextToken();
            if(contenido.equalsIgnoreCase("tarea"))
            {
                st.nextToken();
                int distancia=Integer.parseInt(st.nextToken());
                if (distancia<DISTANCIA_MAX)
                {
                    System.out.println(getLocalName()+": Vamos echando chispas!!!");
                    ACLMessage agree = request.createReply();
                    agree.setPerformative(ACLMessage.AGREE);
                    return agree;
                }
                else
                {
                    System.out.println(getLocalName()+": Información fuera del radio de accion. No se completará la tarea!!!");
                    throw new RefuseException("Dato difícil de encontrar");
                }
            }
            else throw new NotUnderstoodException("Agente manda un mensaje que no puedo entender.");
        }
 
        protected ACLMessage prepareResultNotification(ACLMessage request,ACLMessage response) throws FailureException
        {
            if (Math.random() > 0.2) {
                System.out.println(getLocalName()+": Ha vuelto ha realizar la tarea.");
                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
            else
            {
                System.out.println(getLocalName()+": Ha hecho todo lo posible, lo sentimos.");
                throw new FailureException("Han hecho todo lo posible");
            }
        }
    }
}
