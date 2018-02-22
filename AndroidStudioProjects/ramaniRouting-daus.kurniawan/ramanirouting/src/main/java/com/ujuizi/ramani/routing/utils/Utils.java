package com.ujuizi.ramani.routing.utils;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.graphhopper.util.Instruction.CONTINUE_ON_STREET;
import static com.graphhopper.util.Instruction.FINISH;
import static com.graphhopper.util.Instruction.LEAVE_ROUNDABOUT;
import static com.graphhopper.util.Instruction.REACHED_VIA;
import static com.graphhopper.util.Instruction.TURN_LEFT;
import static com.graphhopper.util.Instruction.TURN_RIGHT;
import static com.graphhopper.util.Instruction.TURN_SHARP_LEFT;
import static com.graphhopper.util.Instruction.TURN_SHARP_RIGHT;
import static com.graphhopper.util.Instruction.TURN_SLIGHT_LEFT;
import static com.graphhopper.util.Instruction.TURN_SLIGHT_RIGHT;
import static com.graphhopper.util.Instruction.USE_ROUNDABOUT;

/**
 * Created by ujuizi on 1/9/17.
 */

public class Utils {


    public static String getManuverName(int direction) {
        String directionName = "";
        switch (direction) {
            case LEAVE_ROUNDABOUT://-6
                directionName = "Leave Round about";
                break;
            case TURN_SHARP_LEFT: //-3
                directionName = "Turn Sharp Left";
                break;
            case TURN_LEFT: //-2
                directionName = "Turn Left";
                break;
            case TURN_SLIGHT_LEFT: //-1
                directionName = "Turn Slight Left";
                break;
            case CONTINUE_ON_STREET: //0
                directionName = "Continue";
                break;
            case TURN_SLIGHT_RIGHT: //1
                directionName = "Turn Slight Right";
                break;
            case TURN_RIGHT: //2
                directionName = "Turn Right";
                break;
            case TURN_SHARP_RIGHT: //3
                directionName = "Turn Sharp Right";
                break;
            case FINISH: //4
                directionName = "Finish";
                break;
            case REACHED_VIA: //5
                directionName = "Reached Via";
                break;

            case USE_ROUNDABOUT: //6
                directionName = "Use Round about";
                break;
        }
        return directionName;
    }

    public static int getIconManuver(int direction) {
        switch (direction) {
            case LEAVE_ROUNDABOUT://-6
//                directionName = "Leave Round about";
                return R.drawable.icon_u_turn;
            case TURN_SHARP_LEFT: //-3
//                directionName = "Turn Sharp Left";
                return R.drawable.icon_sharp_left;
            case TURN_LEFT: //-2
//                directionName = "Turn Left";
                return R.drawable.icon_turn_left;
            case TURN_SLIGHT_LEFT: //-1
//                directionName = "Turn Slight Left";
                return R.drawable.icon_slight_left;
            case CONTINUE_ON_STREET: //0
//                directionName = "Continue on Street";
                return R.drawable.icon_continue;
            case TURN_SLIGHT_RIGHT: //1
//                directionName = "Turn Slight Right";
                return R.drawable.icon_slight_right;
            case TURN_RIGHT: //2
//                directionName = "Turn Right";
                return R.drawable.icon_turn_right;
            case TURN_SHARP_RIGHT: //3
//                directionName = "Turn Sharp Right";
                return R.drawable.icon_sharp_right;
            case FINISH: //4
//                directionName = "Finish";
                return R.drawable.icon_arrived;
            case REACHED_VIA: //5
//                directionName = "Reached Via";

            case USE_ROUNDABOUT: //6
//                directionName = "Use Round about";
                return R.drawable.icon_continue;
        }
        return R.drawable.icon_continue;
    }

    public static String distanceConverter(double aDistance) {
        String distance = "";
        if (aDistance > 1000) {
            //double km = aDistance * 0.001;
            double km = aDistance / 1000f;
            km = Double.valueOf(new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US)).format(km));
            distance = km + " km";
        } else {
            int meter = (int) Math.round(aDistance);
            distance = meter + " m";
        }
        return distance;
    }

    public static String durationConverter(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);


        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(" d ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(" hr");
        }
        if (days == 0 && minutes > 0) {
            sb.append(minutes);
            sb.append(" min");
        }
//        sb.append(seconds);
//        sb.append("s");

        return (sb.toString());
        // it will be return with format like = 12_5_4_49
    }

    public static float getBearing(GeoPoint p1, GeoPoint p2) {

        double longDiff = p2.getLongitude() - p1.getLongitude();
        double y = Math.sin(longDiff) * Math.cos(p2.getLongitude());
        double x = Math.cos(p1.getLatitude()) * Math.sin(p2.getLongitude()) - Math.sin(p1.getLatitude()) * Math.cos(p2.getLatitude()) * Math.cos(longDiff);
        float bearing = (float) (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
        return bearing;


//        double dLon = (p2.getLongitude() - p1.getLongitude());
//        double y = Math.sin(dLon) * Math.cos(p2.getLatitude());
//        double x = Math.cos(p1.getLatitude()) * Math.sin(p2.getLatitude()) - Math.sin(p2.getLatitude()) * Math.cos(p2.getLongitude()) * Math.cos(dLon);
//        double brng = Math.toDegrees((Math.atan2(y, x)));
//        brng = (360 - ((brng + 360) % 360));
//        return (float) brng;
    }
}
