package edu.anadolu;

import java.util.*;
import java.util.stream.Collectors;

public class mTSP implements Cloneable {
    private int depots;
    private int salesmen;
    private int init;
    List<List<String>> routes;
    private int swapNodesInRoute;
    private int swapHubWithNodeInRoutes;
    private int swapNodesBetweenRoutes;
    private int insertNodeInRoute;
    private int insertNodeBetweenRoutes;
    private boolean fixedHub;
    private Random random = new Random();


    private List<String> fullRoute = Arrays.asList(TurkishNetwork.cities);

    public mTSP(int depots, int salesmen) {
        this.depots = depots;
        this.salesmen = salesmen;
    }

    public mTSP(int depots, int salesmen, int init) {
        this.depots = depots;
        this.salesmen = salesmen;
        this.init = init;
    }

    public mTSP(int depots, int salesmen, int init, boolean f_Hub) {
        this.depots = depots;
        this.salesmen = salesmen;
        this.init = init;
        this.fixedHub = f_Hub;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        mTSP clone = new mTSP(depots, salesmen);
        clone.routes = new ArrayList<>();

        for (int i = 0; i < this.routes.size(); i++) {
            List<String> temp = new ArrayList<>();
            temp.addAll(routes.get(i));
            clone.routes.add(temp);
        }
        return clone;
    }

    /**
     * Creates a random route by checking fixedHub value.
     */
    void randomSolution() {

        String[] shuffle = Arrays.copyOf(TurkishNetwork.cities, TurkishNetwork.cities.length);
        List<String> fullRouteShuffled = new ArrayList<>(Arrays.asList(shuffle));
        if (fixedHub) {
            String initialCity = fullRouteShuffled.remove(init);
            Collections.shuffle(fullRouteShuffled);
            fullRouteShuffled.add(0, initialCity);
        } else {
            Collections.shuffle(fullRouteShuffled);
        }

        chopRoute(fullRouteShuffled);


    }

    /**
     * Calculates the cost of given parameter with provided information
     * given from TurkishNetwork.java
     *
     * @param routes1 a route
     * @return returns the integer value of cost
     */
    private int cost(List<List<String>> routes1) {
        int init, init2, totalCost = 0;
        for (List<String> gezici : routes1) {
            for (int i = 0; i < gezici.size(); i++) {
                init = fullRoute.indexOf(gezici.get(i));
                if (i+1 > gezici.size()-1) {
                    init2 = fullRoute.indexOf(gezici.get(0));
                } else {
                    init2 = fullRoute.indexOf(gezici.get(i+1));
                }
                if (TurkishNetwork.distance[init][init2] == 0) {
                    return 0;
                }
                totalCost += TurkishNetwork.distance[init][init2];
            }
        }

        return totalCost;
    }

    /**
     * Calculates the cost of this objects routes element
     *
     * @return returns the integer value of cost
     */
    int cost() {
        int init, init2, totalCost = 0;
        for (List<String> gezici : routes) {
            for (int i = 0; i < gezici.size(); i++) {
                init = fullRoute.indexOf(gezici.get(i));
                if (i+1 > gezici.size()-1) {
                    init2 = fullRoute.indexOf(gezici.get(0));
                } else {
                    init2 = fullRoute.indexOf(gezici.get(i+1));
                }
                if (TurkishNetwork.distance[init][init2] == 0) {
                    return 0;
                }
                totalCost += TurkishNetwork.distance[init][init2];
            }
        }

        return totalCost;
    }

    /**
     * Prints the routes of this object respective to given verbose parameter.
     *
     * @param verbose Index numbers or String values used in printing.
     */
    void print(boolean verbose) {
        int depotNum = 1;
        int routeNum = salesmen;
        int routeCounter = 1;

        if (verbose) {

            for (int i = 0, k = 1; i < routes.size(); i++) {
                if (k == 1) {
                    System.out.println("Depot"+depotNum+": "+routes.get(i).get(0));
                    depotNum++;
                }

                for (int j = 1; j < routes.get(i).size(); j++) {
                    if (j == 1 && routeNum > 0) {

                        System.out.print("  Route "+(k)+": ");
                        k++;
                        routeNum--;
                        routeCounter++;

                        if (routeCounter > salesmen) {
                            k = 1;
                            routeCounter = 1;
                        }
                    }

                    System.out.print(routes.get(i).get(j)+" ");
                }
                routeNum = salesmen;
                System.out.println();


            }
        } else
            for (int i = 0, k = 1; i < routes.size(); i++) {

                if (k == 1) {
                    int index = fullRoute.indexOf(routes.get(i).get(0));
                    System.out.println("Depot"+depotNum+": "+index);
                    depotNum++;
                }

                for (int j = 1; j < routes.get(i).size(); j++) {
                    if (j == 1 && routeNum > 0) {

                        System.out.print("  Route "+(k)+": ");
                        k++;
                        routeNum--;
                        routeCounter++;

                        if (routeCounter > salesmen) {
                            k = 1;
                            routeCounter = 1;
                        }
                    }

                    int index = fullRoute.indexOf(routes.get(i).get(j));
                    System.out.print(index+" ");
                }
                routeNum = salesmen;
                System.out.println();

            }
    }

    /**
     * Applies Nearest Neighbour solution to routes element of mTSP.
     */
    void NNSolution() {

        List<String> list = new ArrayList<>();

        list.add(fullRoute.get(init));
        List<Integer> distanceList;

        //create big route
        for (int i = 0; i < 80; i++) {
            int lastElementOfList = fullRoute.indexOf(list.get(list.size()-1));

            distanceList = Arrays.stream(TurkishNetwork.distance[lastElementOfList]).boxed().collect(Collectors.toList());
            //eliminate cities that have already been visited
            for (String temp : list) {
                distanceList.set(fullRoute.indexOf(temp), 0);
            }
            // minimum distance to next city except itself and cities that have already been visited
            int NearestCity = distanceList.stream()
                    .filter(k -> k != 0).mapToInt(Integer::intValue).min().getAsInt();
            //find the city that has the distance
            int index = distanceList.indexOf(NearestCity);
            //add city to list
            list.add(fullRoute.get(index));
        }


        //chop the main route
        chopRoute(list);
    }


    /**
     * Chops the big routes according to given depots and salesmen values.
     *
     * @param list The Big route that is going to be chopped
     */
    private void chopRoute(List<String> list) {

        final int listSize = list.size();
        final int subNumber = (listSize-depots) / depots;


        List<List<String>> routesForDepot = new ArrayList<>();
        routes = new ArrayList<>();


        //first chop relative to depots

        for (int i = 0; i < listSize; i += subNumber) {

            routesForDepot.add(new ArrayList<>(list.subList(i, Math.min(listSize, i+subNumber))));

        }

        //remainder
        int sizeOfLastElement = routesForDepot.get(routesForDepot.size()-1).size();
        //add previous route if there is any remainder
        if (sizeOfLastElement < subNumber) {
            List<String> lastElement = routesForDepot.get(routesForDepot.size()-1);
            routesForDepot.get(routesForDepot.size()-2).addAll(lastElement);
            routesForDepot.remove(routesForDepot.size()-1);
        }


        //chop again the routes relative to salesmen/vehicle
        for (List<String> strings : routesForDepot) {
            int subRoutesforSalesmen = strings.size() / salesmen;
            List<String> tempList = strings;
            int increment = 0;
            for (int j = 0; j < tempList.size(); j += subRoutesforSalesmen) {
                if (j == 0) {
                    routes.add(new ArrayList<>(tempList.subList(j, Math.min(tempList.size(), j+subRoutesforSalesmen+1))));
                } else {
                    routes.add(new ArrayList<>(tempList.subList(j+1, Math.min(tempList.size(), j+subRoutesforSalesmen+1))));
                }
                increment = routes.size();
                //add previous route if there is any remainder
                if (j > 0) {
                    routes.get(increment-1).add(0, strings.get(0));
                }
            }

            int sizeOfLastElementOfRoutes = routes.get(routes.size()-1).size();
            if (sizeOfLastElementOfRoutes < subRoutesforSalesmen) {
                List<String> lastElement = routes.get(routes.size()-1);
                lastElement.remove(0);
                routes.get(routes.size()-2).addAll(lastElement);
                routes.remove(routes.size()-1);
            }
        }

    }

    /**
     * Applies Hill Climbing to routes of mTSP object. It randomly calls 5 method
     * (swapNodesBetweenRoutes(), swapHubWithNodeInRoutes(), swapNodesInRoute(), insertNodeInRoute() and insertNodeBetweenRoute())
     * and prints the applied changes of those method with counts.
     */
    void applyHillClimbing() {


        Random random = new Random();


        for (int i = 0; i < 5_000_000; i++) {

            int rnd = random.nextInt(5);

            if (rnd == 0 && swapNodesBetweenRoutes()) {
                swapNodesBetweenRoutes++;//doğru çalışıyor

            } else if (rnd == 1 && swapHubWithNodeInRoutes()) {
                swapHubWithNodeInRoutes++;//doğru çalışıyor

            } else if (rnd == 2 && swapNodesInRoute()) {
                swapNodesInRoute++;//doğru çalışıyor

            } else if (rnd == 3 && insertNodeInRoute()) {
                insertNodeInRoute++;//doğru çalışıyor

            } else if (rnd == 4 && insertNodeBetweenRoute()) {
                insertNodeBetweenRoutes++;//doğru çalışıyor
            }
        }

        System.out.println("\n"+"swapHubWithNodeInRoute: "+swapHubWithNodeInRoutes+"\n"+
                "insertNodeBetweenRoutes: "+insertNodeBetweenRoutes+"\n"+
                "swapNodesInRoute: "+swapNodesInRoute+"\n"+
                "swapNodesBetweenRoutes: "+swapNodesBetweenRoutes+"\n"+
                "insertNodeInRoute: "+insertNodeInRoute+"\n");
    }

    /**
     * Swaps two random nodes in a route
     *
     * @return if the applied change is decreased the total cost.
     */
    private boolean swapNodesInRoute() {
        int randomRoute = random.nextInt(routes.size());
        mTSP clone = null;
        try {
            clone = (mTSP) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        List<List<String>> routesCopy = clone.routes;//copy methodu parametre alabilir
        List<String> routeCopy = routesCopy.get(randomRoute);
        //a random route and a deep copy of that route
        if (routeCopy.size() >= 3) {
            int temp1 = random.nextInt(routeCopy.size()-1)+1;
            int temp2 = random.nextInt(routeCopy.size()-1)+1;
            while (temp1 == temp2) {
                temp2 = random.nextInt(routeCopy.size()-1)+1;
            }

            //there is a chance that two selected nodes may be the same node

            String node1 = routeCopy.get(temp1);
            String node2 = routeCopy.get(temp2);

            routeCopy.set(temp1, node1);
            routeCopy.set(temp2, node2);
            routesCopy.set(randomRoute, routeCopy);

            int costOfCopy = cost(routesCopy);
            if (costOfCopy < cost(routes)) {
                routes = routesCopy;
                return true;
            }
            //calculate the change of cost

        }
        return false;
    }

    /**
     * Swaps the depot with a random node in a route that has this depot.
     * Controls the other routes that has this routes also change.
     *
     * @return if the applied change is decreased the total cost.
     */
    private boolean swapHubWithNodeInRoutes() {
        int randomRoute;
        if (fixedHub&&routes.size()-salesmen>0) {
            randomRoute = random.nextInt(routes.size()-salesmen)+salesmen;
        } else {
            randomRoute = random.nextInt(routes.size());
        }

        //bir route a git ve initial state olarak kaydet

        mTSP clone = null;
        try {
            clone = (mTSP) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        List<List<String>> routesCopy = clone.routes;//copy methodu parametre alabilir
        List<String> routeCopy = routesCopy.get(randomRoute);

        String hub = routeCopy.get(0);

        int rnd = random.nextInt(routeCopy.size());

        String node = routeCopy.get(rnd);
        routeCopy.set(rnd, hub);

        for (int i = 0; i < routesCopy.size(); i++) {
            if (routesCopy.get(i).get(0).equals(hub)) {
                routesCopy.get(i).set(0, node);
            }
        }
        int costOfCopy = cost(routesCopy);
        if (costOfCopy < cost(routes)) {
            routes = routesCopy;
            return true;
        }
        //calculate the change of cost
        else {
            return false;
        }

        //
    }

    /**
     * Swaps the nodes from distinct two routes.
     *
     * @return if the applied change is decreased the total cost.
     */
    private boolean swapNodesBetweenRoutes() {
        if (routes.size() <= 1) {
            return false;
        }

        int randomRoute1 = random.nextInt(routes.size());
        int randomRoute2 = random.nextInt(routes.size());

        while (randomRoute1 == randomRoute2) {
            randomRoute2 = random.nextInt(routes.size());
        }

        mTSP clone = null;
        try {
            clone = (mTSP) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<List<String>> routesCopy = clone.routes;//copy methodu parametre alabilir
        List<String> routeCopy1 = routesCopy.get(randomRoute1);
        List<String> routeCopy2 = routesCopy.get(randomRoute2);

        int temp1 = random.nextInt(routeCopy1.size()-1)+1;
        int temp2 = random.nextInt(routeCopy2.size()-1)+1;

        String str1 = routeCopy1.get(temp1);
        String str2 = routeCopy2.get(temp2);

        routeCopy1.set(temp1, str2);
        routeCopy2.set(temp2, str1);
        routesCopy.set(randomRoute1, routeCopy1);
        routesCopy.set(randomRoute2, routeCopy2);

        int costOfCopy = cost(routesCopy);
        if (costOfCopy < cost(routes)) {
            routes = routesCopy;
            return true;
        }
        //calculate the change of cost
        else return false;

    }

    /**
     * Removes a node from a random route and inserts right of another distinct
     * random node which is in the same route
     *
     * @return if the applied change is decreased the total cost.
     */
    private boolean insertNodeInRoute() {

        int randomRoute = random.nextInt(routes.size());
        mTSP clone = null;
        try {
            clone = (mTSP) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        List<List<String>> routesCopy = clone.routes;
        List<String> routeCopy = routesCopy.get(randomRoute);
        if (routeCopy.size() <= 2) return false;

        int temp1 = random.nextInt(routeCopy.size()-1)+1;
        int temp2 = random.nextInt(routeCopy.size()-1)+1;
        while (temp1 == temp2) {
            temp2 = random.nextInt(routeCopy.size()-1)+1;
        }

        String node1 = routeCopy.get(temp1);

        routeCopy.remove(temp1);
        routeCopy.add(temp2, node1);

        int costOfCopy = cost(routesCopy);
        if (costOfCopy < cost(this.routes) && costOfCopy != 0) {
            routes = routesCopy;
            return true;
        }
        //calculate the change of cost
        else {
            return false;
        }


    }

    /**
     * Chooses two random routes and two random nodes from this routes seperatively,
     * and then removes the first node and inserts it to the second node.
     *
     * @return if the applied change is decreased the total cost.
     */
    private boolean insertNodeBetweenRoute() {


        if (routes.size() == 1) {
            return false;
        }

        int randomRoute1 = random.nextInt(routes.size());
        int randomRoute2 = random.nextInt(routes.size());

        while (randomRoute1 == randomRoute2) {
            randomRoute2 = (int) (Math.random() * this.routes.size());
        }

        mTSP clone = null;
        try {
            clone = (mTSP) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        List<List<String>> routesCopy = clone.routes;//copy methodu parametre alabilir
        List<String> routeCopy1 = routesCopy.get(randomRoute1);
        List<String> routeCopy2 = routesCopy.get(randomRoute2);

        int temp1 = random.nextInt(routeCopy1.size()-1)+1;
        int temp2 = random.nextInt(routeCopy2.size()-1)+1;

        String str2 = routeCopy2.remove(temp2);


        routeCopy1.add(temp1, str2);

        int costOfCopy = cost(routesCopy);
        if (costOfCopy < cost(routes) && costOfCopy != 0) {
            routes = routesCopy;
            return true;
        } else return false;
    }

    /**
     * Checks if there is any empty route and any node that repeated in any of the routes.
     *
     * @return if the applied change is decreased the total cost.
     */
    boolean validate() {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < this.routes.size(); i++) {
            if (routes.get(i).size() == 0) return false;
            for (int j = 0; j < this.routes.get(i).size(); j++) {
                if (j == 0) {
                    if (i == 0) {
                        list.add(fullRoute.indexOf(routes.get(i).get(j)));
                    } else if (!(routes.get(i).get(0).equals(routes.get(i-1).get(0)))) {
                        list.add(fullRoute.indexOf(routes.get(i).get(0)));
                    }
                } else {
                    list.add(fullRoute.indexOf(routes.get(i).get(j)));
                }
            }
        }
        list.sort(Integer::compareTo);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != i) {
                System.out.println(i+"th element is "+list.get(i)+"\n"+list);
                return false;
            }


        }

        return true;
    }

}