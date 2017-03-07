package net.teamc.aegis.mapservice;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import net.teamc.aegis.model.ColorCode;
import net.teamc.aegis.model.Crime;
import net.teamc.aegis.json.CrimeJSONParser;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * A service that provides crime chance forecast
 */
class CredibilityWtdForecast {
    /**
     * The way points from Google Map API
     */
    private List<LatLng> wayPoints;
    /**
     * Way points in an array
     */
    private LatLng[] wpArray;
    private Context context;

    /**
     * Create an instance of class {@code CredibilityWtdForecast}
     *
     * @param wayPoints The way points
     * @throws IllegalArgumentException When {@code wayPoints} is either null or empty
     */
    public CredibilityWtdForecast(List<LatLng> wayPoints, Context context) {
        if (wayPoints == null || wayPoints.isEmpty())
            throw new IllegalArgumentException("null or empty way points");
        this.wayPoints = wayPoints;
        this.context = context;
    }

    /**
     * Create an instance of class {@code CredibilityWtdForecast}
     *
     * @param wayPoints The way points
     * @throws IllegalArgumentException When {@code wayPoints} is either null or empty
     */
    public CredibilityWtdForecast(LatLng[] wayPoints, Context context) {
        if (wayPoints == null || wayPoints.length == 0)
            throw new IllegalArgumentException("null or empty way points");
        wpArray = wayPoints;
        this.context = context;
    }

    /**
     * Calculate and forcast the risk on top of the path (from way points)
     *
     * @return A color code: [Green Gray Yellow Orange Red 'Dark Red']
     */
    ColorCode getCrimeForecastColorCode() {
        double WPCrime = wayPointCalc();
        if (WPCrime < .1) return ColorCode.GREEN; // Green
        else if (WPCrime < .2) return ColorCode.GRAY;
        else if (WPCrime < .3) return ColorCode.YELLOW;
        else if (WPCrime < .4) return ColorCode.ORANGE;
        else if (WPCrime < .5) return ColorCode.RED;
        else return ColorCode.DARK_RED;
    }

    //method that determines the crime level of a given area
    private double expectedCrimeCalc(Crime[] crimeData) {
        List<double[]> trends = calculateCrimeTrend(crimeData);
        //array of number of crime from past 28 days
        double[] recentCrime = trends.get(0);
        //array of crime from past 4 years
        double[] crimeTrend = trends.get(1);
        //averages the cwf and exp trends
        return (cwf(crimeTrend, 11) + expWtd(recentCrime)) / 2;
    }

    //@param array of integers in chronological order, and an x value to predict
    private static double cwf(double[] arr, int pred) {
        //gets the correlation squared of the array
        double z = correlation(arr) * correlation(arr);
        //performs least squares regression on array
        double trend = linearRegression(pred, arr);
        //exponential weighted forecast
        double exp = expWtd(arr);
        //linear weighted forecast
        double lin = linWtd(arr);
        double avg = (exp + lin) / 2;
        double med = median(arr);
        return z * trend + .5 * (1 - z) * med + .5 * (1 - z) * avg;
    }

    private static double correlation(double[] xs) {
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;
        int n = xs.length;
        for (int i = 0; i < n; ++i) {
            double x = xs[i];
            double y = i + 1;
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
        if (sigmax == 0 || sigmay == 0)
            return 0;
        else return cov / sigmax / sigmay;
    }

    private static double median(double[] arr) {
        //sorts arrays in non-decreasing order
        Arrays.sort(arr);
        int mIndex = arr.length / 2;
        return (arr.length % 2 == 0) ? (arr[mIndex] + arr[mIndex - 1]) / 2 : arr[mIndex];
    }

    private static double linearRegression(int pred, double[] arr2) {//Least squares regression
        double[] arr1 = new double[arr2.length];
        for (int i = 0; i < arr1.length; i++)
            arr1[i] = i + 1;
        double xsum = sum(arr1);   //sum <1,...,n>
        double ysum = sum(arr2);   //sum original array
        double xysum = productSum(arr2);     //dot product of two arrays
        double xxsum = squareSum(arr1);
        int n = arr1.length;
        double m = (double)((n * xysum) - (xsum * ysum)) / ((n * xxsum) - (xsum * xsum));
        double b = (ysum - (m * xsum)) / (n);
        return m * pred + b;
    }

    //method that multiplies each array value with another array value, then sums
    private static double productSum(double[] arr1) {
        double productSum = 0;
        for (int i = 0; i < arr1.length; i++) {
            productSum += arr1[i] * (i + 1);
        }
        return productSum;
    }

    //method that squares each array value, then sums it
    private static double squareSum(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i] * arr[i];
        }
        return sum;
    }

    //method that sums the values in array
    private static double sum(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    //method that does (sum(<array>*<1^2...n^2)/sum(<1^2...n^2)
    private static double expWtd(double[] arr1) {
        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            //gets sum(<1^2...n^2)
            sum1 += (i + 1) * (i + 1);
            //gets sum(<array>*<1^2...n^2>)
            sum2 += arr1[i] * (i + 1) * (i + 1);
        }
        return sum2 / sum1;
    }

    //method that does (sum(<array>*<1...n)/sum(<1...n)
    private static double linWtd(double[] arr1) {
        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            //gets sum(<1...n>)
            sum1 += i + 1;
            //gets sum(<array>*<1^2...n^2)
            sum2 += arr1[i] * (i + 1);
        }
        return sum2 / sum1;
    }

    private double wayPointCalc() {
        double wpCrime = 0;
        List<LatLng> points = filterPoints(wayPoints);

        for (LatLng wp : points) {
            CrimeJSONParser crime = new CrimeJSONParser(
                    ServiceUtils.getEncodedURLYears(context, wp.latitude, wp.longitude, 10));
            Crime[] crimes = crime.parseCrimes();
            if (crimes == null || crimes.length == 0) continue;
            wpCrime += expectedCrimeCalc(crimes);
        }
        return wpCrime / wayPoints.size();
    }

    private List<LatLng> filterPoints(List<LatLng> wps) {
        List<LatLng> out = new ArrayList<>(wps.size());
        //LatLng[] wpsArray = wps.toArray(new LatLng[wps.size()]);
        filter((ArrayList<LatLng>)wps, 0, wps.size() - 1, out);
        Log.d(CLAZZ, "wp size=" + wps.size() + ", out size=" + out.size());
        return out;
    }

    private void filter(ArrayList<LatLng> source, int startIndex, int endIndex, List<LatLng> out) {
        if (startIndex >= endIndex) return;
        int med = (startIndex + endIndex) >> 1;
        out.add(source.get(med));
        filter(source, startIndex, med - 1, out);
        filter(source, med + 1, endIndex, out);
    }

    //method that calculates the crime level in past 28 days
    private List<double[]> calculateCrimeTrend(List<Crime> crimeData) {
        //sets current date
        LocalDateTime today = LocalDateTime.now();
        double recentCrime[] = new double[]{0, 0, 0, 0};
        double crimeTrend[] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (Crime crime : crimeData) {
            LocalDateTime crimeDate = crime.getDispatchDateTime();
            long diff = Duration.between(crimeDate, today).toMillis() / (1000 * 3600 * 24);
            if (crime.getUcr() <= 900 && crime.getUcr() != 800) {
                if (diff <= 7)
                    recentCrime[3]++;
                else if (diff <= 14)
                    recentCrime[2]++;
                else if (diff <= 21)
                    recentCrime[1]++;
                else if (diff <= 28)
                    recentCrime[0]++;
                else if (diff <= 380 && diff >= 350)
                    crimeTrend[9] += 1 / 30 * 7;
                else if (diff <= 745 && diff >= 715)
                    crimeTrend[8] += 1 / 30 * 7;
                else if (diff <= 1110 && diff >= 1080)
                    crimeTrend[7] += 1 / 30 * 7;
                else if (diff <= 1475 && diff >= 1445)
                    crimeTrend[6] += 1 / 30 * 7;
                else if (diff <= 1840 && diff >= 1810)
                    crimeTrend[5] += 1 / 30 * 7;
                else if (diff <= 2205 && diff >= 2175)
                    crimeTrend[4] += 1 / 30 * 7;
                else if (diff <= 2570 && diff >= 2540)
                    crimeTrend[3] += 1 / 30 * 7;
                else if (diff <= 2935 && diff >= 2905)
                    crimeTrend[2] += 1 / 30 * 7;
                else if (diff <= 3300 && diff >= 3270)
                    crimeTrend[1] += 1 / 30 * 7;
                else if (diff <= 3665 && diff >= 3635)
                    crimeTrend[0] += 1 / 30 * 7;
            }
        }
        List<double[]> trends = new ArrayList<>(2);
        trends.add(recentCrime);
        trends.add(crimeTrend);
        return trends;
    }

    //method that calculates the crime level in past 28 days
    private List<double[]> calculateCrimeTrend(Crime[] crimeData) {
        //sets current date
        LocalDateTime today = LocalDateTime.now();
        double recentCrime[] = new double[]{0, 0, 0, 0};
        double crimeTrend[] = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // debug
        if (crimeData == null || crimeData.length == 0)
            Log.d(CLAZZ, "The crime data array is null or empty");

        crimeTrendCalc(crimeData, 0, crimeData.length - 1, recentCrime, crimeTrend, today);

        List<double[]> trends = new ArrayList<>(2);
        trends.add(recentCrime);
        trends.add(crimeTrend);
        return trends;
    }

    /**
     * A binary search algorithm to calculate trends.
     *
     * @param data
     * @param start
     * @param end
     * @param recent
     * @param trend
     * @param today
     */
    private void crimeTrendCalc(Crime[] data, int start, int end, double[] recent, double[] trend,
                                LocalDateTime today) {
        if (start == end) {
            int ucr = data[start].getUcr();
            long diff = Duration.between(data[start].getDispatchDateTime(), today).toMillis() / (1000 * 3600 * 24);
            double inc = 7 / 30; // 1 / 30 * 7
            if (ucr <= 900 && ucr != 800) {
                if (diff <= 7)
                    recent[3]++;
                else if (diff <= 14)
                    recent[2]++;
                else if (diff <= 21)
                    recent[1]++;
                else if (diff <= 28)
                    recent[0]++;
                else if (diff <= 380 && diff >= 350)
                    trend[9] += inc;
                else if (diff <= 745 && diff >= 715)
                    trend[8] += inc;
                else if (diff <= 1110 && diff >= 1080)
                    trend[7] += inc;
                else if (diff <= 1475 && diff >= 1445)
                    trend[6] += inc;
                else if (diff <= 1840 && diff >= 1810)
                    trend[5] += inc;
                else if (diff <= 2205 && diff >= 2175)
                    trend[4] += inc;
                else if (diff <= 2570 && diff >= 2540)
                    trend[3] += inc;
                else if (diff <= 2935 && diff >= 2905)
                    trend[2] += inc;
                else if (diff <= 3300 && diff >= 3270)
                    trend[1] += inc;
                else if (diff <= 3665 && diff >= 3635)
                    trend[0] += inc;
            }
            return;
        }
        int med = (start + end) >> 1;
        crimeTrendCalc(data, start, med, recent, trend, today);
        crimeTrendCalc(data, med + 1, end, recent, trend, today);
    }

    private static final String CLAZZ = CredibilityWtdForecast.class.getSimpleName();
}
