package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static int n; //liczba wag
    static int k; //dopuszczalna liczba bledow
    static double m; //stala uczenia
    static double [] w; //wagi
    static double teta = 3; //na poczatku przyjmijmy ze teta jest rowna 3
    static Map<Integer, String> classes = new HashMap<>(); //klasy i odpowiadajace im liczby

    public static void main(String[] args) {

        List<Iris> data = getData(new File("perceptron.data.txt"));
        List<Iris> test = getData(new File("perceptron.data.test.txt"));

        doTraining(data);

        System.out.println(doSingleTest(enterValues()));
        //System.out.println(doMultipleTest(test));

    }

    public static String doSingleTest(Iris test){
        int y = getResult(test);
        String result = classes.get(y);
        return result;
    }

    public static String doMultipleTest(List<Iris> test){
        int counter = 0;
        for (Iris iris: test) {
            String result = doSingleTest(iris);
            System.out.println(result);
            if(result.equals(iris.getAtrybutDecyzyjny())){
                counter++;
            }
        }

        return "matches: " + counter+ "/" + test.size();
    }


    public static Iris enterValues(){
        List<Double> dataTest = new ArrayList<>();
        System.out.println("Enter attribute values: ");
        for (int i = 1; i < n+1; i++) {
            Scanner in = new Scanner(System.in);
            try {
                System.out.print(i+": ");
                dataTest.add(in.nextDouble());
            }catch (InputMismatchException e){
                System.err.println("enter double value");
                i--;
            }
        }
        return new Iris(dataTest);
    }

    public static double enterM(){
        m = 0;
        while (m <= 0 || m>=1 ) {
            try {
                System.out.print("Enter m (m should be between 0 and 1): ");
                Scanner in = new Scanner(System.in);
                m = in.nextDouble();
            }catch (InputMismatchException e){
                System.out.println("Enter a double value between 0 and 1");
            }
        }

        w = new double[n];
        for (int i = 0; i < w.length; i++) {
            w[i] = 1; //na poczatku wszystkie wartosci wektora wag ustawiamy na 1
        }

        return m;
    }

    public static int enterK(){
        k = -1;

        while(k<0) {
            try {
                System.out.print("Enter k: ");
                Scanner in = new Scanner(System.in);
                k = in.nextInt();
                if(k<0)
                    throw new Exception();
            } catch (InputMismatchException e) {
                System.out.println("Enter an integer");
            } catch (Exception e) {
                System.out.println("k should be greater than 0");
            }
        }

        return k;
    }

    public static void doTraining(List<Iris> data){
        enterM();
        enterK();

        //1 -- iris-setosa
        //0 -- iris-versicolor

        int counter = k+1;
        int epoki = 0;

        while (counter > k) {

            counter = 0;

            for (int i = 0; i < data.size(); i++) {
                String atrybutDecyzyjny = data.get(i).getAtrybutDecyzyjny();

                int d = 0;
                if (atrybutDecyzyjny.equals(data.get(0).getAtrybutDecyzyjny())) {
                    d = 1;
                    classes.put(1, data.get(0).getAtrybutDecyzyjny());
                }else
                    classes.put(0, atrybutDecyzyjny);


                int y = getResult(data.get(i));
                if (Math.abs(d - y) > 0) {
                    counter++;
                    update(d, y, data.get(i).getAtrybuty());
                }
            }
            System.out.println("Wagi:");
            for (int j = 0; j < w.length; j++) {
                System.out.println(w[j]+" ");
            }
            System.out.println(counter);
            epoki++;
        }
        System.out.println("epoki = "+epoki);
    }

    public static int getResult(Iris test){
        List<Double> x = test.getAtrybuty();
        double WX = 0;
        for (int i = 0; i < w.length; i++) {
            WX+= w[i]*x.get(i);
        }

        double net = WX - teta;

        if(net >= 0) {
            return 1;
        }else
            return 0;
    }

    public static void update(int d, int y, List<Double> x){
        for (int i = 0; i < w.length; i++) {
            w[i] = w[i] + m*(d - y)*x.get(i);
        }

        teta = teta - m*(d - y);
    }


    public static List<Iris> getData(File file) {
        List<Iris> data = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int f;
            while ((f = fis.read()) != -1)
                sb.append((char) f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("([\\d.,]+)([\\w-]+)");
        Matcher m = p.matcher(sb);

        List<Double> atrybuty = null;
        while (m.find()) {
            atrybuty = new ArrayList<>();
            String [] tmp = m.group(1).split(",");
            for (int i = 0; i < tmp.length; i++) {
                atrybuty.add(Double.parseDouble(tmp[i]));
            }

            data.add(new Iris(atrybuty, m.group(2)));
        }
        n = atrybuty.size();

        return data;
    }
}
