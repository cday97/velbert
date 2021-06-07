package org.matsim.velbert.analysis;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HomeAnalysis {

    private static final CoordinateTransformation transformation = TransformationFactory.getCoordinateTransformation("EPSG:25832", "EPSG:3857");
    private static final String shapefile = "D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\OSM_PLZ_072019.shp";
    private static final String populationFile = "D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\matsim-velbert-v1.0-1pct.plans.xml";
    private static final String networkFile = "D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\matsim-velbert-v1.0.network.xml";
    private static Set<Id<Person>> velbertPersons = new HashSet<>();

    public static void main(String[] args) {

        var features = ShapeFileReader.getAllFeatures(shapefile);
        var population = PopulationUtils.readPopulation(populationFile);
        var network = NetworkUtils.readNetwork(networkFile);

        var geometry1 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42551")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();
        var geometry2 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42549")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();
        var geometry3 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42555")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();
        var geometry4 = features.stream().filter(feature -> feature.getAttribute("plz").equals("42553")).map(feature -> (Geometry)feature.getDefaultGeometry()).findAny().orElseThrow();

        for (var person : population.getPersons().values()) {
            var plan = person.getSelectedPlan();
            var activities = TripStructureUtils.getActivities(plan, TripStructureUtils.StageActivityHandling.ExcludeStageActivities);

            for (var activity : activities) {
                var coord = transformation.transform(getCoord(activity, network));
                if (geometry1.covers(MGC.coord2Point(coord)) || geometry2.covers(MGC.coord2Point(coord)) || geometry3.covers(MGC.coord2Point(coord)) || geometry4.covers(MGC.coord2Point(coord))){
                    velbertPersons.add(person.getId());
                }
            }
        }
        System.out.println(velbertPersons);
    }

    private static Coord getCoord(Activity activity, Network network) {
        if (activity.getCoord() != null) {
            return activity.getCoord();
        }
        return network.getLinks().get(activity.getLinkId()).getCoord();
    }

    public Set<Id<Person>> getVelbertPersons(){
        return velbertPersons;
    }

}