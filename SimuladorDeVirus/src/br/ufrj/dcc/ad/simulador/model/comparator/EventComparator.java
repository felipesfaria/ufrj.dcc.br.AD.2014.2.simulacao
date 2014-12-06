package br.ufrj.dcc.ad.simulador.model.comparator;

import br.ufrj.dcc.ad.simulador.model.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event x, Event y)
    {
    	if (x.getTime() < y.getTime()) return -1;
		if (x.getTime() > y.getTime()) return 1;
		return 0;
    }	
}
