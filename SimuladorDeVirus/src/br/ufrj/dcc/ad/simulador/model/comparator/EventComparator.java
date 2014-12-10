package br.ufrj.dcc.ad.simulador.model.comparator;

import java.util.Comparator;

import br.ufrj.dcc.ad.simulador.model.Event;

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event x, Event y)
    {
    	if (x.getTime() < y.getTime()) return -1;
		if (x.getTime() > y.getTime()) return 1;
		return 0;
    }	
}
