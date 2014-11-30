package br.ufrj.dcc.ad.simulador;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;


public class EventQueue{
	
	Comparator<Event> comparator = new EventComparator();
    PriorityQueue<Event> timeline = new PriorityQueue<Event>(16, comparator);

	public void add(Event ev){
		timeline.add(ev);
	}
	
	public Event pop(){
		return timeline.poll();
	}
	
	public Double getLastTime(){
		return timeline.element().getTime();
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
