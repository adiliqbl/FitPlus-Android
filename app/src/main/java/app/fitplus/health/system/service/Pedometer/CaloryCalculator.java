package app.fitplus.health.system.service.Pedometer;

/**
 * Created by Ahmad Waleed on 3/10/2018.
 */

public class CaloryCalculator {

    public double steptocal(int steps, double weight)
    {
        double calories=0;
//        double wq=0;
//        if(weight<=45)
//            wq=0.025;
//
//        else if(weight<=55)
//            wq=0.030;
//
//        else if(weight<=64)
//            wq=0.035;
//        else if(weight<=73)
//            wq=0.040;
//        else if(weight<=82)
//            wq=0.045;
//        else if(weight<=91)
//            wq=0.050;
//        else if(weight<=100)
//            wq=0.055;
//
//        else if(weight<=114)
//            wq=0.062;
//
//        else if(weight<=125)
//            wq=0.068;
//
//        else
//            wq=0.075;

        calories=0.05*steps;


        return calories;
    }
}
