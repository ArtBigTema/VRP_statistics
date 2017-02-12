package av.VRP.rt.map;

import av.VRP.rt.Utils.Constant;
import av.VRP.rt.Utils.Log;
import av.VRP.rt.substance.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Artem on 08.02.2017.
 */
public class Cluster {
    private List<PointWithMessage> clusters;
    private Map<String, ArrayList<Vehicle>> vehicleCluster;

    private int count;
    private int countPoints;
    private int pres;

    public Cluster() {
        count = Constant.CLUSTERS;
        clusters = new ArrayList<>();
        vehicleCluster = new HashMap<>();
    }

    public void add(Point point, int v) {
        for (PointWithMessage p : clusters) {
            if (p.getHash(v).equals(point.getHash(v))) {
                p.incLatLng(point);
                p.incClust();
                return;
            }
        }
        vehicleCluster.put(point.getHash(v), new ArrayList<>());
        clusters.add(new PointWithMessage(point, point.getHashFull()));
    }

    public List<PointWithMessage> getClusters() {
        return clusters;
    }

    public void initDepo(Vehicle vehicle, int indexCluster) {
        clusters.get(indexCluster).incCount();

        String hash = clusters.get(indexCluster).getHash(pres);

        ArrayList<Vehicle> vehicles = vehicleCluster.get(hash);
        vehicles.add(vehicle);
        vehicleCluster.put(clusters.get(indexCluster).getHash(pres), vehicles);

        // vehicleCluster = sortMapCluster();
    }

    public void constructClusters(Trips trips, int v) {
        Log.p("start constructCluster");
        pres = v;

        List<Trip> points = trips.getSubAll();
        countPoints = points.size();

        for (Trip point : points) {
            add(point.getStartPoint(), v);
        }
        Collections.sort(clusters);

        Log.p("end constructCluster");
    }

    public void sortMap() {
        vehicleCluster = sortMapCluster();
    }

    public Map sortMapCluster() {//бугагашенька
        return vehicleCluster.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue((o1, o2) ->
                        Integer.compare(o2.size(), o1.size())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void decClusterSize(int index) {
        clusters.get(index).decCount();
        // map list remove
    }

    public void incClusterSize(int index) {
        clusters.get(index).incCount();
        // map list remove
    }

    public void checkStack() {
        for (PointWithMessage p : clusters) {
            if (p.needShuffle()) {

            }
        }
    }

    public int getNearestCluster(Vehicle vehicle) {
        double tmp, distance = Double.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < size(); i++) {
            PointWithMessage point = clusters.get(i);

            tmp = MapUtils.getDistance(point, vehicle.getCurrPoint());

            if (tmp < distance) {
                distance = tmp;
                index = i;
            }
        }

        // incClusterSize(index);
        initDepo(vehicle, index);
        return index;
    }

    public void constructClusters(Trips trips) {
        constructClusters(trips, Constant.CLUSTERS);
    }

    public List<PointWithMessage> getPoints() {
        return clusters;
    }

    public void clear() {
        clusters.clear();
    }

    public PointWithMessage get(int i) {
        return clusters.get(i);
    }

    public int size() {
        return clusters.size();
    }

    public int getTripSize() {
        return countPoints;
    }
}