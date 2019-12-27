/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patron3;

//import Transporte.*;
import jade.core.Profile;

import jade.core.ProfileImpl;

import jade.wrapper.AgentController;

import jade.wrapper.ContainerController;

import jade.wrapper.StaleProxyException;


//-gui a1:Movile.Guest;a2:Movile.Host
/**

 *

 * @author pablopico

 */

public class AddContainer {

    public static void main(String args[])

    {

        //Get the JADE runtime interface (singleton)

jade.core.Runtime runtime = jade.core.Runtime.instance();

//Create a Profile, where the launch arguments are stored

Profile profile = new ProfileImpl();

profile.setParameter(Profile.CONTAINER_NAME, "Sistemas");

profile.setParameter(Profile.MAIN_HOST, "172.29.13.100");

//create a non-main agent container

ContainerController container = runtime.createAgentContainer(profile);

try {

        AgentController ag = container.createNewAgent("pepito", 

                                      "Movile.BasiAgent", 

                                      new Object[] {});//arguments

        ag.start();
        

} catch (StaleProxyException e) {

    e.printStackTrace();

}

    }

}

