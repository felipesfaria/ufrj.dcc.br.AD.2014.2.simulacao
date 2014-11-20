package br.ufrj.dcc.ad.simulador;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event x, Event y)
    {
    	if (x.time < y.time) return -1;
		if (x.time > y.time) return 1;
		return 0;
    }	
}
