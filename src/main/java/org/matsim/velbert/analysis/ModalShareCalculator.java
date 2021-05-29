package org.matsim.velbert.analysis;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.TransitDriverStartsEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.TransitDriverStartsEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.locationtech.jts.geom.Geometry;

import java.util.*;

public class ModalShareCalculator implements TransitDriverStartsEventHandler, PersonDepartureEventHandler, ActivityEndEventHandler {


    private static final String shapefile = "D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\OSM_PLZ_072019.shp";
    private static final CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation("EPSG:25832", "EPSG:3857");

    private static final List<String> modes = List.of(TransportMode.walk, TransportMode.bike, TransportMode.ride, TransportMode.car, TransportMode.pt, TransportMode.airplane);
    private final Set<Id<Person>> transitDrivers = new HashSet<>();
    private final Map<Id<Person>, List<String>> personTrips = new HashMap<>();
    private final Set<Id<Person>> nonVelbertBoyz = new HashSet<>();

    public Map<Id<Person>, List<String>> getPersonTrips() {
        return personTrips;
    }

    @Override
    public void handleEvent(ActivityEndEvent e) {
        if (transitDrivers.contains(e.getPersonId()) || isInteraction(e.getActType())) return;
        if (nonVelbertBoyz.contains(e.getPersonId())) return;
        var endCoord = e.getCoord();
        if (isInVelbert(transformation.transform(endCoord))== false) {
            nonVelbertBoyz.add(e.getPersonId());
            return;
        }
        personTrips.computeIfAbsent(e.getPersonId(), id -> new ArrayList<>()).add("");
    }

    @Override
    public void handleEvent(PersonDepartureEvent e) {
        if (transitDrivers.contains(e.getPersonId())) return;
        if (nonVelbertBoyz.contains(e.getPersonId())) return;
        var trips = personTrips.get(e.getPersonId());
        var mainMode = getMainMode(getLast(trips), e.getLegMode());
        setLast(trips, mainMode);
    }

    @Override
    public void handleEvent(TransitDriverStartsEvent transitDriverStartsEvent) {
        transitDrivers.add(transitDriverStartsEvent.getDriverId());
    }

    private boolean isInteraction(String type) {
        return type.endsWith(" interaction");
    }

    private String getMainMode(String current, String newMode) {
        var currentIndex = modes.indexOf(current);
        var newIndex = modes.indexOf(newMode);
        return currentIndex > newIndex ? current : newMode;
    }

    private String getLast(List<String> from) {
        return from.get(from.size() - 1);
    }

    private void setLast(List<String> to, String value) {
        to.set(to.size() - 1, value);
    }

    private boolean isInVelbert(Coord coord){
        var features = ShapeFileReader.getAllFeatures(shapefile);
            var geometry1 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42551")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();
            var geometry2 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42549")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();
            var geometry3 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42555")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();
            var geometry4 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42553")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();

        if (geometry1.covers(MGC.coord2Point(coord)) || geometry2.covers(MGC.coord2Point(coord)) || geometry3.covers(MGC.coord2Point(coord)) || geometry4.covers(MGC.coord2Point(coord))){
            return true;
        }  else return false;
    }

}
