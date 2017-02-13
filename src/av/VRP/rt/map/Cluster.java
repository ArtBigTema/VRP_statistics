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
    private Map<String, ArrayList<Vehicle>> clusterMap;

    private int countPoints;
    private int pres;

    public Cluster() {
        clusters = new ArrayList<>();
        clusterMap = new HashMap<>();
    }

    public void add(Point point, int v) {
        for (PointWithMessage p : clusters) {
            if (p.getHash(v).equals(point.getHash(v))) {
                p.incLatLng(point);
                p.incClust();
                return;
            }
        }
        clusterMap.put(point.getHash(v), new ArrayList<>());
        clusters.add(new PointWithMessage(point, point.getHashFull()));
    }

    public List<PointWithMessage> getClusters() {
        return clusters;
    }

    public void addVehicle(Vehicle vehicle, int indexCluster) {
        clusters.get(indexCluster).incCount();

        String hash = clusters.get(indexCluster).getHash(pres);

        ArrayList<Vehicle> vehicles = clusterMap.get(hash);
        vehicles.add(vehicle);
        clusterMap.put(clusters.get(indexCluster).getHash(pres), vehicles);

        // clusterMap = sortMapCluster();
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
        clusterMap = sortMapCluster();
    }

    public Map sortMapCluster() {//бугагашенька
        return clusterMap.entrySet()
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

    public void decClusterSize(Vehicle vehicle) {
        clusters.get(vehicle.getDepoIndex()).decCount();

        if (vehicle.getDepoIndex() == 197) {
            PointWithMessage point = clusters.get(197);
            //  Log.p(vehicle.getDepoIndex());
        }
        // String hash = vehicle.getDepo().getHash(pres);
        // ArrayList<Vehicle> vehicles = clusterMap.get(hash);if(vehicles.size()<1){Log.e("errro");}
        // vehicles.remove(vehicle);
        // clusterMap.put(hash, vehicles);
    }

    public void incComing(Vehicle vehicle) {
        clusters.get(vehicle.getDepoIndex()).incComing();
    }

    public void incClusterSize(Vehicle vehicle) {
        clusters.get(vehicle.getDepoIndex()).incCount();

        if (vehicle.getDepoIndex() == 197) {
            PointWithMessage point = clusters.get(197);
            //  Log.p(vehicle.getDepoIndex());
        }
        // String hash = vehicle.getDepo().getHash(pres);
        // ArrayList<Vehicle> vehicles = clusterMap.get(hash);
        // vehicles.add(vehicle);
        // clusterMap.put(hash, vehicles);
    }

    public void shuffle() {
        for (int i = clusters.size() - 1; i >= 0; i--) {
            if (i == 197) {
                //  Log.p(i);
            }
            PointWithMessage point = get(i);
            if (point.needShuffle()) {
                String hash = point.getHash(pres);
                ArrayList<Vehicle> vehicles = clusterMap.get(hash);
                for (Vehicle vehicle : vehicles) {
                    if (!vehicle.isBusy()) {
                        if (!vehicle.goToDepo()) {
                            // vehicle.resetDepo(get(0), 0);
                            for (int k = 0; k < i; k++) {//find nearest
                                if (i == 197) {
                                    // Log.p(i);
                                }
                                PointWithMessage p = get(k);
                                if (p.getComingMore()) {
                                    p.incComing();//fixme dec if turnoff
                                    vehicle.resetDepo(p, k);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int getNearestCluster(Vehicle vehicle) {
        double tmp, distance = Double.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < size(); i++) {
            PointWithMessage point = clusters.get(i);

            tmp = MapUtils.getDistance(point, vehicle.getCurrPoint());

            if (point.getComingMore()) {

                if (tmp < distance) {
                    distance = tmp;
                    index = i;
                }
            }
        }
        if (index < 0) {
            index = 0;
        }
        if (index == 197) {
            PointWithMessage point = clusters.get(197);
            //   Log.p(index);
        }
        // incClusterSize(index);
        // addVehicle(vehicle, index);
        return index;
    }

    public void constructClusters(Trips trips) {
        clear();
        constructClusters(trips, getClusterZoom(trips.getSubAll().size()));
    }

    public int getClusterZoom(int tripSize) {
        if (tripSize >= 2000) {
            return 5;
        }
        if (tripSize >= 1000) {
            return 6;
        }
        if (tripSize >= 500) {
            return 7;
        }
        if (tripSize >= 100) {
            return 8;
        }
        return Constant.CLUSTERS;
    }

    public void clear() {
        clusters.clear();
        clusterMap.clear();
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