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
 
public class Guest extends Agent {
  private ArrayList containers = new ArrayList();
  private Location origen = null;
  private Location destino= null;
  private String partidaName= "", destinoName= "";
 
  protected void setup(){
    System.out.println("El agente " + (getAID()).getName() + " esta preparado");
    addBehaviour(new MoverAgenteBehaviour(this));
  }
 
  // Metodo que contiene las operaciones que realiza el agente movil antes de desplazarse.
  protected void beforeMove(){
    System.out.println("El agente " + (getAID()).getName() + " se mueve al nuevo container");
  }
 
  // Metodo que contiene las operaciones que realiza el agente movil despues de desplazarse.
  protected void afterMove() {
    //getContentManager().registerLanguage(new SLCodec());
    //getContentManager().registerOntology(MobilityOntology.getInstance());
    System.out.println("El agente " + (getAID()).getName() + " ha llegado");
  }
 
  class MoverAgenteBehaviour extends SimpleBehaviour {
    private boolean parar= false;
 
    public MoverAgenteBehaviour(Agent a) {
      super(a);
    }
 
    public void action() { }
 
    protected void takeDown(){
      System.out.println("El agente " + (getAID()).getName() + " termina");
    }
 
    public boolean done() {
      return parar;
    }
  }
}

