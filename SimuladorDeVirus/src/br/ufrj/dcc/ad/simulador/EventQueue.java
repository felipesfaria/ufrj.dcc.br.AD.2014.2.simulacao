package br.ufrj.dcc.ad.simulador;

import java.util.Comparator;
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

}
