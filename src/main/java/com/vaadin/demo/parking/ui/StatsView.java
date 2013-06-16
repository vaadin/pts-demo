package com.vaadin.demo.parking.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class StatsView extends NavigationView {

    private static final String STYLE_NAME = "stats";
    private static final String STYLE_NAME_CHART = "statschart";

    private static Color[] colors = new VaadinTheme().getColors();

    private final BeanItemContainer<Ticket> ticketContainer = ParkingUI
            .getTicketContainer();

    private DataSeries zoneSeries;
    private DataSeries regionSeries;

    @Override
    public void attach() {
        super.attach();
        buildUi();
    }

    public final void buildUi() {
        setStyleName(STYLE_NAME);
        setCaption("Stats");
        setSizeFull();

        CssLayout layout = new CssLayout();
        setContent(layout);

        layout.addComponent(new Label("Tickets / area: C1:2, C2: 2, ..."));
        // layout.addComponent(buildChart());
    }

    public final Component buildChart() {
        Chart chart = new Chart(ChartType.PIE);
        chart.addStyleName(STYLE_NAME_CHART);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Tickets / area");

        PlotOptionsPie pie = new PlotOptionsPie();
        pie.setShadow(false);
        conf.setPlotOptions(pie);

        zoneSeries = new DataSeries();
        zoneSeries.setName("Zone");
        PlotOptionsPie innerPieOptions = new PlotOptionsPie();
        zoneSeries.setPlotOptions(innerPieOptions);
        innerPieOptions.setSize("60%");
        innerPieOptions.setDataLabels(new Labels());
        innerPieOptions.getDataLabels().setFormatter(
                "this.y > 5 ? this.point.name : null");
        innerPieOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerPieOptions.getDataLabels().setDistance(-30);

        regionSeries = new DataSeries();
        regionSeries.setName("Area");
        PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
        regionSeries.setPlotOptions(outerSeriesOptions);
        outerSeriesOptions.setInnerSize("60%");
        outerSeriesOptions.setDataLabels(new Labels());
        outerSeriesOptions.getDataLabels().setFormatter(
                "this.y > 1 ? '<b>'+ this.point.name +':</b> '+ this.y : null");

        conf.setSeries(zoneSeries, regionSeries);
        chart.drawChart(conf);

        final Credits credits = conf.getCredits();
        credits.setText("");
        credits.setHref("");

        updateChartData();

        return chart;
    }

    public final void updateChartData() {

        Map<String, Integer> areaTickets = Maps.newHashMap();

        for (Ticket ticket : ticketContainer.getItemIds()) {
            if (ticket.getArea() != null) {
                Integer count = areaTickets.get(ticket.getArea());
                if (count == null) {
                    areaTickets.put(ticket.getArea(), 1);
                } else {
                    areaTickets.put(ticket.getArea(), count + 1);
                }
            }
        }

        List<String> order = Lists.newArrayList(areaTickets.keySet());
        Collections.sort(order);

        List<DataSeriesItem> outerItemList = Lists.newArrayList();
        List<DataSeriesItem> innerItemList = Lists.newArrayList();

        Character zone = null;
        int zoneTickets = 0;
        int color = 0;
        for (String area : order) {
            if (zone == null) {
                zone = area.charAt(0);
            }

            if (area.charAt(0) != zone) {
                innerItemList.add(new DataSeriesItem(String.valueOf(zone),
                        (double) zoneTickets, colors[color]));
                color++;
                zone = area.charAt(0);
                zoneTickets = 0;
            }
            int thisAreaTickets = areaTickets.get(area);
            zoneTickets += thisAreaTickets;

            outerItemList.add(new DataSeriesItem(area,
                    (double) thisAreaTickets, colors[color]));

        }
        innerItemList.add(new DataSeriesItem(String.valueOf(zone),
                (double) zoneTickets, colors[color]));

        if (regionSeries != null) {
            regionSeries.setData(outerItemList);
        }
        if (zoneSeries != null) {
            zoneSeries.setData(innerItemList);
        }
    }
}
