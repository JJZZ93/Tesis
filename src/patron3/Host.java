/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patron3;

import java.util.*;
import java.io.*;
 
import jade.lang.acl.*;
import jade.content.*;
import jade.content.onto.basic.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.*;
import jade.domain.JADEAgentManagement.*;
import jade.domain.mobility.*;
 
public class Host extends Agent {
  private ArrayList containers = new ArrayList();
  private Location origen = null;
  private Location destino= null;
  private String partidaName= "", destinoName= "";
 
  //Metodo para actualizar la lista de containers disponibles
  protected void actualizarContainers(){
    containers.clear();
    ACLMessage request= new ACLMessage(ACLMessage.REQUEST);
    request.setLanguage(new SLCodec().getName());
    // Establecemos que MobilityOntology sea la ontologia de este mensaje.
    request.setOntology(MobilityOntology.getInstance().getName());
    // Le solicitamos al AMS una lista de los containers disponibles
    Action action= new Action(getAMS(), new QueryPlatformLocationsAction());
    try {
      getContentManager().fillContent(request, action);
      request.addReceiver(action.getActor());
      send(request);
 
      // Filtramos los mensajes INFORM que nos llegan desde el AMS
      MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(getAMS()), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
 
      ACLMessage resp= blockingReceive(mt);
      ContentElement ce= getContentManager().extractContent(resp);
      Result result=(Result) ce;
      jade.util.leap.Iterator it= result.getItems().iterator();
      // Almacena en el array los containers disponibles.
      while(it.hasNext()) {
    Location loc=(Location) it.next();
    containers.add(loc);
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
 
  //Metodo para visualizar los containers disponibles en la plataforma
  protected void verContainers(){
    //ACTUALIZAR
    //actualizarContainers();
    //VISUALIZAR
    System.out.println("******Containers disponibles: *******");
    for(int i=0; i<containers.size(); i++){
      System.out.println("["+ i + "] " + ((Location)containers.get(i)).getName());
    }
  }
 
  protected void setup(){
    // Registramos el lenguaje y la ontologia para la movilidad en el manejador del agente
    getContentManager().registerLanguage(new SLCodec());
    getContentManager().registerOntology(MobilityOntology.getInstance());
 
    actualizarContainers();
    addBehaviour(new MoverAgenteBehaviour(this));
  }
 
  // Metodo que contiene las operaciones que realiza el agente movil antes de desplazarse.
  protected void beforeMove(){
    System.out.println("[MSG] El agente se mueve al container: "+ destino.getName());
  }
 
  // Metodo que contiene las operaciones que realiza el agente movil despues de desplazarse.
  protected void afterMove() {
    getContentManager().registerLanguage(new SLCodec());
    getContentManager().registerOntology(MobilityOntology.getInstance());
    System.out.println("[MSG] El agente ha llegado desde: "+ origen.getName());
  }
 
  class MoverAgenteBehaviour extends SimpleBehaviour {
    private boolean parar= false;
 
    public MoverAgenteBehaviour(Agent a) {
      super(a);
    }
 
    public void action() {
      try {
    verContainers();
    System.out.println();
    System.out.print("Introduce el numero del container al que mover: ");
    BufferedReader lee= new BufferedReader(new InputStreamReader(System.in));
    String lectura = lee.readLine();
    int container=0;
    if(lectura.equalsIgnoreCase("Q")){
      doDelete();
      return;
    }
    else{
      container = Integer.parseInt(lectura);
    }
    System.out.print("Introduce el nombre del agente que quieres mover: ");
    String nombre = lee.readLine();
 
 
    try{
      origen = here();
      destino=(Location)containers.get(container);
      ///////////////////////////////////////////////
      AID aid = new AID(nombre, AID.ISLOCALNAME);      //"movil", AID.ISLOCALNAME);
      MobileAgentDescription mad = new MobileAgentDescription();
      mad.setName(aid);
      mad.setDestination(destino);
      MoveAction ma = new MoveAction();
      ma.setMobileAgentDescription(mad);
      sendRequest(new Action(getAMS(), ma));
    }catch(Exception ex){
      System.out.println("Problema al intentar mover el agente");
    }
      }catch(Exception io){
    System.out.println(io);
      }
    }
 
    public boolean done() {
      return parar;
    }
  }
 
  protected void takeDown(){
    System.out.println("[MSG] Agente finalizado");
  }
 
  void sendRequest(Action action) {
    // ---------------------------------
    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
    request.setLanguage(new SLCodec().getName());
    request.setOntology(MobilityOntology.getInstance().getName());
    try {
      getContentManager().fillContent(request, action);
      request.addReceiver(action.getActor());
      send(request);
    }catch (Exception ex) { ex.printStackTrace(); }
   }
 
}

