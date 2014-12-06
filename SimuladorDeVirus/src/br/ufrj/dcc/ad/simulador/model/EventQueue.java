package br.ufrj.dcc.ad.simulador.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import br.ufrj.dcc.ad.simulador.model.comparator.EventComparator;


public class EventQueue{
	
    //PriorityQueue<Event> timeline = new PriorityQueue<Event>(16, new EventComparator());
    List<Event> timeline = new ArrayList<Event>();

	public void add(Event event){
		for(Event e : timeline){
			if(event.getTime()<e.getTime()){
				int index = timeline.indexOf(e);
				timeline.add(index, event);
				return;
			}
		}
		timeline.add(event);
	}
	
	public Event pop(){
		Event e =timeline.get(0);
		timeline.remove(0);
		return e;
	}
	
	public Double getLastTime(){
		return timeline.get(timeline.size()-1).getTime();
	}
	
	public void printQueue(){
		Iterator<Event> iterator = timeline.iterator();
		while (iterator.hasNext()){
			Event e = iterator.next();
			System.out.println(e);
		}
	}
	
	public Event get(int index){
		return timeline.get(index);
	}

	public int getSize() {
		return timeline.size();
	}
	
	public Iterator<Event> getIterator(){
		return	timeline.iterator();
	}
	
	public void removeEvent(Event evt){
		timeline.remove(evt);
	}

	public boolean isEmpty() {
		return timeline.isEmpty();
	}

	public boolean hasScheduledInfection(Node infectado, Node infector) {
		for(Event e :timeline){
			if(e.getNextState()==State.P&&e.getCurrentNd()==infectado&&e.getInfectionAgentNd()==infector)
				return true;
		}
		return false;
	}
	
	public boolean nextEventIsValid(){
		Event nEvent = timeline.get(0);
		State cState = nEvent.getCurrentNd().getState();
		State nState = nEvent.getNextState();
		if(nState==State.P&&cState!=State.O){
			System.out.println("Discareded nextEvent: "+nEvent);
			timeline.remove(0);
			return false;
		}else{
			return true;
		}
	}

	public void removeInfectionsFrom(Node cNode) {
		int index = 0;
		while(index<timeline.size()){
			Event e = timeline.get(index);
			if(e.getInfectionAgentNd()==cNode){
				timeline.remove(index);
			}else{
				index++;
			}
		}
	}
	
	public String toString(){
		String ret = "";
		for(Event e:timeline){
			ret+=e+"\n";
		}
		return ret;
	}

}
