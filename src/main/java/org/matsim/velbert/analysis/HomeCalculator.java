package org.matsim.velbert.analysis;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.util.*;

public class HomeCalculator implements ActivityStartEventHandler, ActivityEndEventHandler {


    private static final String shapefile = "D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\OSM_PLZ_072019.shp";
    private static final CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation("EPSG:25832", "EPSG:3857");

    private static Map<Id<Person>, Coord> homeCoords = new HashMap<>();
    private static Set<Id<Person>> velbertPersons = new HashSet<>();

    public Map<Id<Person>, Coord> getHomeCoords(){ return homeCoords; }
    public Set<Id<Person>> getVelbertPersons(){ return velbertPersons;}


    @Override
    public void handleEvent(ActivityStartEvent e){
        if (e.getActType().contains("home_")){
            if (isInVelbert(transformation.transform(e.getCoord()))==true){
                homeCoords.put(e.getPersonId(),e.getCoord());
                velbertPersons.add(e.getPersonId());
            }
            else return;
        }
        else return;
    }

    @Override
    public void handleEvent(ActivityEndEvent e) {
        if (e.getActType().contains("home_")){
            if (isInVelbert(transformation.transform(e.getCoord()))==true){
                homeCoords.put(e.getPersonId(),e.getCoord());
                velbertPersons.add(e.getPersonId());
            }
            else return;
        }
        else return;
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

