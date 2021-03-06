/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vacuumCleaner.experiments.generatetraces;

import java.io.File;
import vacuumCleaner.agents.Agent;
import vacuumCleaner.agents.discrete.FixedSequenceAgent;
import vacuumCleaner.agents.discrete.PauseEveryN;
import vacuumCleaner.agents.discrete.RandomAgent;
import vacuumCleaner.agents.discrete.RandomExplorerAgent;
import vacuumCleaner.agents.discrete.SmartRandomAgent;
import vacuumCleaner.agents.discrete.SmartRandomExplorerAgent;
import vacuumCleaner.agents.discrete.SmartStraightLineAgent;
import vacuumCleaner.agents.discrete.StraightLineAgent;
import vacuumCleaner.agents.discrete.ZigZagAgent;
import vacuumCleaner.simulator.Action;
import vacuumCleaner.simulator.State;
import vacuumCleaner.simulator.Trace;
import vacuumCleaner.simulator.TraceEntry;
import vacuumCleaner.simulator.objects.VacuumCleaner;
import vacuumCleaner.simulator.perception.FourRayDistancePerception;
import vacuumCleaner.simulator.perception.Perception;
import util.XMLWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import org.jdom.input.SAXBuilder;
import vacuumCleaner.agents.discrete.SmartWallFollowerAgent;
import vacuumCleaner.agents.discrete.WallFollowerAgent;
import vacuumCleaner.agents.turnable.TurnableSmartStraightLineAgent;
import vacuumCleaner.agents.turnable.TurnableStraightLineAgent;
import vacuumCleaner.simulator.perception.TurnableFourRayDistancePerception;

/**
 *
 * @author santi
 */
public class GenerateTurnableTraces {
    public static void main(String []args) throws Exception {
        int traceLength = 2000;
        int mapN = 0;
        List<State> maps = new LinkedList<>();
        maps.add(new State(new SAXBuilder().build("simulator/maps/turnable-8x8.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("simulator/maps/turnable-8x8-2.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("simulator/maps/turnable-8x8-3.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("simulator/maps/turnable-8x8-4.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("simulator/maps/turnable-8x8-5.xml").getRootElement()));

        for(State map:maps) {
            List<Agent> agents = new LinkedList<>();
            // Level 1:
            // ...

            // Level 2:

            // Level 3 
            agents.add(new TurnableStraightLineAgent());
            agents.add(new TurnableSmartStraightLineAgent());
            
            Perception perception = new TurnableFourRayDistancePerception(0.1, 0.1);

            // create folder if needed:
            File dir = new File("data/traces-"+perception.getClass().getSimpleName()); 
            if (!dir.exists()) dir.mkdir(); 
            
            for(Agent agent:agents) {
                String fileName = "data/traces-"+perception.getClass().getSimpleName()+"/trace-m" + mapN + "-" + agent.name();

                // This generates a trace, while saving it to Matlab format:
                Trace t = generateContinuousTrace(map,agent,traceLength,0.1,perception,1);
                FileWriter fw = new FileWriter(fileName + ".xml");
                t.toxml(new XMLWriter(fw));
                fw.close();
                
//                JFrame w = TraceVisualizer.newWindow(agent.getClass().getSimpleName(), 800, 600, t);
//                w.setVisible(true);
            }
            mapN++;
        }
    }

    public static Trace generateContinuousTrace(State s, Agent agent, int maxCycles, double step, Perception perception, int vacuumID) throws Exception {
        State state = (State)s.clone();
        Trace t = new Trace(state,vacuumID);
        double time = 0;

        boolean first = true;

        for(int cycle = 0;cycle<maxCycles;cycle++) {
            Perception p = perception.perceive(state, state.get(vacuumID));
            Action a = agent.cycle(vacuumID, p, step);
            if (a!=null) a.setObjectID(vacuumID);
            t.addEntry(new TraceEntry(time, (State)state.clone(), vacuumID, a));
            state.cycle(a, step);
            time += step;
//            System.out.println("cycle " + time);
        }

        return t;
    }

    
}
