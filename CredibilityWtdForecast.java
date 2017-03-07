package net.teamc.aegis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CredibilityWtdForecast {

    public CredibilityWtdForecast(LatLng shape) {
        CrimeJSONParse crime = new CrimeJSONParse(CrimeJSONParse.getAPIshape(shape.longitude, shape.latitude)); //downloads API data
        List<JSONObject> CrimeData = crime.returnJSON;      //declares an arraylist of json objects
    }

    public double CrimeLevelCalc(List<JSONObject> CrimeData, CrimeJSONParse crime) throws ParseException {      //method that determines the crime level of a given area
        int calc [] = new int[4];
        int trend [] = new int[4];
        int arr[] = new int[calc.length]; //creates array of 1 to n
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i + 1;
            }
        calc = crimeCalc(CrimeData, crime);         //array of number of crime from past 28 days
        trend = crimeTrend(CrimeData, crime);       //array of crime from past 4 years
        double crimeLevel = (WeightedForecast(trend, 5)+expWtd(calc, arr))/2;       //averages the CWF and exp trends
        return crimeLevel;
    }
    //@param array of integers in chronological order, and an x value to predict
    public static double WeightedForecast(int[] arr, int pred) {    //Credibility Weighted Forecast
        int arr2[] = new int[arr.length]; //creates array of 1 to n
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = i + 1;
        }
        double z = Correlation(arr) * Correlation(arr);   //gets Z of array
        double trend = LinearRegression(pred, arr, arr2);   //performs least squares regression on array
        double exp = expWtd(arr, arr2);     //exponential weighted forecast
        double lin = linWtd(arr, arr2);     //linear weighted forecast
        double avg = (exp + lin) / 2;           //average forecast
        double med = median(arr);           //finds median
        double wtdFcst = (z * trend) + (.5 * (1 - z) * med) + (.5 * (1 - z) * avg);       //calculates CWF
        return wtdFcst;
    }

    public static double Correlation(int[] xs) {
        int ys[] = new int[xs.length];
        for (int i = 0; i < ys.length; i++) {
            ys[i] = i + 1;
        }
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;
        int n = xs.length;
        for (int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = ys[i];
            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
        }
        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n - sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n - sy * sy / n / n);
        // correlation is just a normalized covariation
        return cov / sigmax / sigmay;
    }

    public static double median(int[] arr) {
        Arrays.sort(arr);       //sorts arrays in nondrecreasing order
        double median = 0;
        if (arr.length % 2 == 0) { //if length is even
            median = (double) (arr[arr.length / 2] + arr[(arr.length / 2) - 1]) / 2;
        } else median = arr[(arr.length / 2)];
        return median;
    }

    public static double LinearRegression(int pred, int[] arr2, int[] arr1) {//Least squares regression
        int xsum = sum(arr1);   //sum orginal array
        int ysum = sum(arr2);   //sum <1,...,n>
        int xysum = productsum(arr1, arr2);     //dot product of two arrays
        int xxsum = squaresum(arr1);
        int n = arr1.length;
        double m = (double) ((n * xysum) - (xsum * ysum)) / ((n * xxsum) - (xsum * xsum));
        double b = (ysum - (m * xsum)) / (n);
        System.out.println("y = " + m + "x + " + b);
        return m * pred + b;
    }

    public static int productsum(int[] arr1, int[] arr2) { //method that multiplies each array value with another array value, then sums
        int productsum = 0;
        for (int i = 0; i < arr1.length; i++) {
            productsum += arr1[i] * arr2[i];
        }
        return productsum;
    }

    public static int squaresum(int[] arr) {    //method that squares each array value, then sums it
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i] * arr[i];
        }
        return sum;
    }

    public static int sum(int[] arr) {  //method that sums the values in array
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    //inputs are original array and array of <1,...,n>
    public static double expWtd(int[] arr1, int[] arr2) {  //method that does (sum(<array>*<1^2...n^2)/sum(<1^2...n^2)
        int sum1 = 0;           //initializes sums
        int sum2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            sum1 += arr2[i] * arr2[i];             //gets sum(<1^2...n^2)
            sum2 += arr1[i] * arr2[i] * arr2[i];    //gets sum(<array>*<1^2...n^2)
        }
        return (double) sum2 / sum1;
    }

    //inputs are original array and array of <1,...,n>
    public static double linWtd(int[] arr1, int[] arr2) {  //method that does (sum(<array>*<1...n)/sum(<1...n)
        int sum1 = 0;           //initializes sums
        int sum2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            sum1 += arr2[i];                        //gets sum(<1...n)
            sum2 += arr1[i] * arr2[i];            //gets sum(<array>*<1^2...n^2)
        }
        return (double) sum2 / sum1;
    }
    public  int [] crimeCalc (List<JSONObject> CrimeData, CrimeJSONParse crime) throws ParseException {     //method that calculates the crime level in past 28 days
        Date d1 = StringToDate(getCurrentTimeStamp());      //sets current date
        Date d2 =null;
        int count1=0;
        int count2 =0;
        int count3 = 0;
        int count4 = 0;
        long diff;
        int crime1[]=new int[4];
        for(int i = 0;i<CrimeData.size();i++){              //traverses through API data
            d2 = StringToDate(crime.getDispatch_date(CrimeData,i));     //gets time of a crime
            diff = (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);  //gets the difference of current and crime date in days
            if (diff<=7) {                          //if crime happened in week 1
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count1++;
            }
            else if (diff<=14) {                          //if crime happened in week 2
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count2++;
            }
            else if (diff<=21) {                          //if crime happened in week 3
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count3++;
            }
            else if (diff<=28) {                          //if crime happened in week 4
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count4++;
            }
        }
        //assigns count of crimes to respective spot in array
        crime1 [0]= count4;
        crime1 [1]= count3;
        crime1 [2]= count2;
        crime1 [3]= count1;
        return crime1;
    }
    public  int [] crimeTrend(List<JSONObject> CrimeData, CrimeJSONParse crime) throws ParseException { //method that calculates the crime level in past 4 years
        Date d1 = StringToDate(getCurrentTimeStamp());
        Date d2 =null;
        int count1=0;
        int count2 =0;
        int count3 = 0;
        int count4 = 0;
        long diff;
        int crime1[]=new int[4];
        for(int i = 0;i<CrimeData.size();i++){
            d2 = StringToDate(crime.getDispatch_date(CrimeData,i));
            diff = (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);
            if (diff <= 380 && diff >=350) {        //if within 15 days before or after current date last year
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count1++;
            }else if (diff <= 745 && diff>=715) {   //if within 15 days before or after current date two years ago
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count2++;
            }else if (diff <= 1110 && diff>=1080) { //if within 15 days before or after current date three years ago
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count3++;
            }else if (diff <= 1475 && diff>=1445) { //if within 15 days before or after current date four years ago
                if (crime.getUCR(CrimeData, i) <= 100)  //if Type I offense
                    count4++;
            }
        }
        //assigns count to repective index
        crime1 [0]= count4;
        crime1 [1]= count3;
        crime1 [2]= count2;
        crime1 [3]= count1;
        return crime1;
    }
    //gets current date
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    //parses a string format into a date
    public static Date StringToDate(String str) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
        return date;
    }
}
