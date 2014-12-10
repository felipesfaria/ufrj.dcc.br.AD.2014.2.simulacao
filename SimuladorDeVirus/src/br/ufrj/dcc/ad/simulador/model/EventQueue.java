package br.ufrj.dcc.ad.simulador.model;

import java.util.Iterator;
import java.util.PriorityQueue;

import br.ufrj.dcc.ad.simulador.model.comparator.EventComparator;


public class EventQueue{
	
    PriorityQueue<Event> timeline = new PriorityQueue<Event>(16, new EventComparator());

	public void add(Event ev){
		timeline.add(ev);
	}
	
	public Event pop(){
		return timeline.poll();
	}
	
	public Double getLastTime(){
		return timeline.element().getTime();
	}
	
	public void printQueue(){
		Iterator<Event> iterator = timeline.iterator();
		while (iterator.hasNext()){
			Event e = iterator.next();
			System.out.println(e);
		}
	}
	
	public Event get(int index){
		return timeline.peek();
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

}
