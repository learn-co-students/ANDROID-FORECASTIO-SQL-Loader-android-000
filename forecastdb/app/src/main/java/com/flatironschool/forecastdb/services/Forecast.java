package com.flatironschool.forecastdb.services;

import java.util.List;

/**
 * Created by altyus on 8/6/14.
 */
public class Forecast {
    public HourlyForecast hourly;

    public class HourlyForecast {
        public List<HourData> data;
    }

    public class HourData {
        public double temperature;
        public double precipProbability;
        public double humidity;
        public double windSpeed;
        public double visibility;
        public double pressure;
        public double ozone;
        public double time;
        public String icon;
        public String summary;
    }

}
