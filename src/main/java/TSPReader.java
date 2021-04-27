import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class TSPReader {
    ArrayList<String[]> storing;
    HashMap<String, HashMap<Double, Double>> massa;

    public TSPReader(String pathname) throws Exception {
        File file = new File(pathname);
        Scanner sc = new Scanner(file);
        storing = new ArrayList<String[]>();
        String nextValue = null;
        massa = new HashMap<String, HashMap<Double, Double>>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if ("DISPLAY_DATA_SECTION".equals(line)) {
                nextValue = sc.nextLine();
                while (!nextValue.equals("EOF")) {
                    storing.add(nextValue.trim().split("  "));
                    HashMap teste = new HashMap<Double, Double>();
                    teste.put(Double.valueOf(nextValue.trim().split("  ")[1]), Double.valueOf(nextValue.trim().split("  ")[2]));
                    massa.put(nextValue.trim().split("  ")[0], teste);
                    nextValue = sc.nextLine();
                }
            }
        }
        sc.close();
    }

    public static HashMap<String, HashMap<Double, Double>> returnScanner(String pathname) throws Exception {
        TSPReader TSPReader = new TSPReader(pathname);
        return TSPReader.massa;
    }

//    public static void main(String[] args) throws Exception {
//        HashMap<String, HashMap<Double, Double>> storedValues = returnScanner("C:/Users/dvdua/Downloads/Test/Assimetrico/dantzig42.tsp.txt");
//        for (Map.Entry<String, HashMap<Double, Double>> dado : storedValues.entrySet()) {
//            System.out.println(dado.getKey());
//            System.out.println(dado.getValue());
//        }
//    }
}

