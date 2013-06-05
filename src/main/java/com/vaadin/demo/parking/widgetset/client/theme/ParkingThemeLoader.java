package com.vaadin.demo.parking.widgetset.client.theme;

import com.vaadin.addon.touchkit.gwt.client.ThemeLoader;

public class ParkingThemeLoader extends ThemeLoader {

    @Override
    public void load() {
        // Load default TouchKit theme...
        super.load();
        // ... and Parking specific additions from own client bundle
        ParkingBundle.INSTANCE.css().ensureInjected();
        ParkingBundle.INSTANCE.ticketsCss().ensureInjected();
    }

}
