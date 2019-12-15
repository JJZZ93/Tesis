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
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.proto.SubscriptionResponder.SubscriptionManager;
import java.util.HashSet;
import java.util.Set;

public class singleAgent extends Agent{
    
    private Set<Subscription> suscripciones = new HashSet<Subscription>();
 
    protected void setup() {
        System.out.println(this.getLocalName() + ": Esperando suscripciones...");
 
        //Se crea una plantilla para que sólo se admitan mensajes del protocolo FIPA-Subscribe
        MessageTemplate template = SubscriptionResponder.createMessageTemplate(ACLMessage.SUBSCRIBE);
 
        //Se añade un comportamiento que cada 5 segundos envía un mensaje a todos los suscriptores.
        this.addBehaviour(new EnviarSemanal(this, (long) 5000));
 
        //Se añade un comportamiento que maneja los mensajes recibidos para suscribirse.
        //Habrá que crear primero el SubscriptionManager que registrará y eliminará las suscripciones.
        SubscriptionManager gestor = new SubscriptionManager() {
 
            public boolean register(Subscription suscripcion) {
                suscripciones.add(suscripcion);
                return true;
            }
 
            public boolean deregister(Subscription suscripcion) {
                suscripciones.remove(suscripcion);
                return true;
            }
        };
        this.addBehaviour(new HacerSuscripcion(this, template, gestor));
    }
 
    //Comprueba la propuesta. En este caso si el contenido del mensaje tiene una longitud mayor que 2, devuelve true
    private boolean compruebaMensaje(String propuesta) {
        return (propuesta.length() > 2);
    }
 
    private class EnviarSemanal extends TickerBehaviour {
        public EnviarSemanal(Agent agente, long tiempo) {
            super(agente, tiempo);
        }
 
        public void onTick() {
            //Se crea y rellena el mensaje con la información que desea enviar.
            ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
            mensaje.setContent(String.valueOf(getTickCount()));
 
            //Se envía un mensaje a cada suscriptor
            for (Subscription suscripcion:singleAgent.this.suscripciones) {
                suscripcion.notify(mensaje);
            }
        }
    }
 
    private class HacerSuscripcion extends SubscriptionResponder {
        private Subscription suscripcion;
 
        public HacerSuscripcion(Agent agente, MessageTemplate plantilla, SubscriptionManager gestor) {
            super(agente, plantilla, gestor);
        }
 
        //Método que maneja la suscripcion
 
        protected ACLMessage handleSubscription(ACLMessage propuesta)
                throws NotUnderstoodException {
            System.out.printf("%s: SUSCRIBE recibido de %s.\n",
                singleAgent.this.getLocalName(), propuesta.getSender().getLocalName());
            System.out.printf("%s: La propuesta es: %s.\n",
                singleAgent.this.getLocalName(), propuesta.getContent());
 
            //Comprueba los datos de la propuesta
            if (singleAgent.this.compruebaMensaje(propuesta.getContent())) {
 
                //Crea la suscripcion
                this.suscripcion = this.createSubscription(propuesta);
 
                try {
                    //El SubscriptionManager registra la suscripcion
                    this.mySubscriptionManager.register(suscripcion);
                } catch (Exception e) {
                    System.out.println(singleAgent.this.getLocalName() + ": Error en el registro de la suscripción.");
                }
 
                //Acepta la propuesta y la envía
                ACLMessage agree = propuesta.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            } else {
                //Rechaza la propuesta y la envía
                ACLMessage refuse = propuesta.createReply();
                refuse.setPerformative(ACLMessage.REFUSE);
                return refuse;
            }
        }
 
        //Maneja la cancelación de la suscripcion
 
        protected ACLMessage handleCancel(ACLMessage cancelacion) {
            System.out.printf("%s: CANCEL recibido de %s.\n",
                singleAgent.this.getLocalName(), cancelacion.getSender().getLocalName());
 
            try {
                //El SubscriptionManager elimina del registro la suscripcion
                this.mySubscriptionManager.deregister(this.suscripcion);
            } catch (Exception e) {
                System.out.println(singleAgent.this.getLocalName() + ": Error en el desregistro de la suscripción.");
            }
 
            //Acepta la cancelación y responde
            ACLMessage cancela = cancelacion.createReply();
            cancela.setPerformative(ACLMessage.INFORM);
            return cancela;
        }
    }
}
