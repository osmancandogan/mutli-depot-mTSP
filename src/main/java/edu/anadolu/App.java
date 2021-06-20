package edu.anadolu;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;



public class App {

    public static void main(String[] args) {

        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }


        mTSP best = null;
        int minCost = Integer.MAX_VALUE;
        int cost = Integer.MAX_VALUE;


        if (params.getNNChoise()) {

            mTSP nn = new mTSP(params.getNumDepots(), params.getNumSalesmen(), params.getInitialValue(), params.getFixedHub());
            nn.NNSolution();
            System.out.println("**Total cost is "+nn.cost()+"\n");
            cost = nn.cost();
            best = nn;
        } else {
            for (int i = 0; i < 500_000; i++) {

                mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen(), params.getInitialValue(), params.getFixedHub());

                mTSP.randomSolution();

                cost = mTSP.cost();

                if (cost < minCost) {
                    best = mTSP;
                    minCost = cost;
                }

            }
        }

        if (best != null && best.validate()) {

            best.print(params.getVerbose());
            System.out.println("**Total cost is "+best.cost());
            if (best.validate()) {
                System.out.println("The route is: "+best.validate());
                System.out.println("Hill Climbing Algorithm starting...");
                best.applyHillClimbing();
            }
            System.out.println("The route is: "+best.validate()+" after Hill Climbing Algorithm");

            best.print(params.getVerbose());
            System.out.println("**Total cost is "+best.cost());
            writeJSON(best, params);
        } else {
            System.out.println("The route is: "+best.validate());
        }
    }


    public static void writeJSON(mTSP best, Params params) {
        JSONObject mainObject = new JSONObject();
        JSONArray solutions = new JSONArray();

        JSONObject solutionsObject = new JSONObject();
        JSONArray routes = new JSONArray();

        List<String> fullRoute = Arrays.asList(TurkishNetwork.cities);

        for (int i = 0; i < best.routes.size(); i++) {
            StringBuilder writer = new StringBuilder();

            for (int j = 1; j < best.routes.get(i).size(); j++) {
                writer.append(fullRoute.indexOf(best.routes.get(i).get(j))+" ");
            }
            routes.put(writer.substring(0, writer.length()-1));

            if (routes.length() == params.getNumSalesmen()) {
                solutionsObject.put("depot", String.valueOf(fullRoute.indexOf(best.routes.get(i).get(0))));
                solutionsObject.put("routes", routes);
                solutions.put(solutionsObject);

                solutionsObject = new JSONObject();
                routes = new JSONArray();
            }
        }
        mainObject.put("solution", solutions);
        try {
            Files.write(Paths.get("solution_d"+params.getNumDepots()+"s"+params.getNumSalesmen()+".json"), mainObject.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
