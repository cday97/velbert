package org.matsim.velbert.analysis;

import org.apache.commons.csv.CSVFormat;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.handler.EventHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

public class RunAnalysis {

    public static void main(String[] args) {

        var manager = EventsUtils.createEventsManager();
        var handler = new ModalShareCalculator();
        manager.addHandler(handler);
        EventsUtils.readEvents(manager, "D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\cluster-outputs\\velbert-v1.0-1pct.output_events.xml.gz");

        var personTrips = handler.getPersonTrips();
        var modes = personTrips.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(mode -> mode, mode -> 1, Integer::sum));

        var totalTrips = modes.values().stream()
                .mapToDouble(d -> d)
                .sum();

        try (var writer = Files.newBufferedWriter(Paths.get("D:\\Users\\chris\\Documents\\CSDSchool\\MatSimClass\\projects\\velbert\\scenarios\\velbert-v1.0-1pct\\modes-events.csv")); var printer = CSVFormat.DEFAULT.withDelimiter(',').withHeader("Mode", "Count", "Share").print(writer)) {

            for (var entry : modes.entrySet()) {
                printer.printRecord(entry.getKey(), entry.getValue(), entry.getValue() / totalTrips);
            }

            printer.printRecord("total", totalTrips, 1.0);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
