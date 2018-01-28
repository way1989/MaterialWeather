package com.ape.material.weather.bean;

import java.util.List;

/**
 * Created by way on 2018/1/28.
 */

public class HeWeatherS6 {

    private List<HeWeather6Bean> HeWeather6;

    public List<HeWeather6Bean> getHeWeather6() {
        return HeWeather6;
    }

    public void setHeWeather6(List<HeWeather6Bean> HeWeather6) {
        this.HeWeather6 = HeWeather6;
    }

    public static class HeWeather6Bean {
        /**
         * basic : {"cid":"CN101280601","location":"深圳","parent_city":"深圳","admin_area":"广东","cnty":"中国","lat":"22.54700089","lon":"114.08594513","tz":"+8.0"}
         * update : {"loc":"2018-01-28 11:53","utc":"2018-01-28 03:53"}
         * status : ok
         * now : {"cloud":"100","cond_code":"104","cond_txt":"阴","fl":"13","hum":"75","pcpn":"0.0","pres":"1016","tmp":"17","vis":"8","wind_deg":"35","wind_dir":"东北风","wind_sc":"微风","wind_spd":"8"}
         * daily_forecast : [{"cond_code_d":"305","cond_code_n":"305","cond_txt_d":"小雨","cond_txt_n":"小雨","date":"2018-01-28","hum":"64","mr":"14:50","ms":"03:25","pcpn":"0.5","pop":"86","pres":"1016","sr":"07:03","ss":"18:09","tmp_max":"18","tmp_min":"8","uv_index":"6","vis":"18","wind_deg":"1","wind_dir":"北风","wind_sc":"4-5","wind_spd":"24"},{"cond_code_d":"305","cond_code_n":"305","cond_txt_d":"小雨","cond_txt_n":"小雨","date":"2018-01-29","hum":"63","mr":"15:50","ms":"04:29","pcpn":"1.1","pop":"74","pres":"1021","sr":"07:03","ss":"18:10","tmp_max":"13","tmp_min":"8","uv_index":"4","vis":"18","wind_deg":"9","wind_dir":"北风","wind_sc":"3-4","wind_spd":"12"},{"cond_code_d":"305","cond_code_n":"305","cond_txt_d":"小雨","cond_txt_n":"小雨","date":"2018-01-30","hum":"66","mr":"16:53","ms":"05:32","pcpn":"1.4","pop":"85","pres":"1021","sr":"07:03","ss":"18:11","tmp_max":"10","tmp_min":"7","uv_index":"2","vis":"18","wind_deg":"54","wind_dir":"东北风","wind_sc":"3-4","wind_spd":"14"},{"cond_code_d":"305","cond_code_n":"101","cond_txt_d":"小雨","cond_txt_n":"多云","date":"2018-01-31","hum":"71","mr":"17:59","ms":"06:31","pcpn":"0.5","pop":"74","pres":"1023","sr":"07:02","ss":"18:11","tmp_max":"9","tmp_min":"6","uv_index":"4","vis":"17","wind_deg":"0","wind_dir":"北风","wind_sc":"3-4","wind_spd":"17"},{"cond_code_d":"101","cond_code_n":"100","cond_txt_d":"多云","cond_txt_n":"晴","date":"2018-02-01","hum":"54","mr":"19:04","ms":"07:27","pcpn":"0.3","pop":"88","pres":"1023","sr":"07:02","ss":"18:12","tmp_max":"12","tmp_min":"7","uv_index":"8","vis":"19","wind_deg":"0","wind_dir":"无持续风向","wind_sc":"微风","wind_spd":"8"},{"cond_code_d":"100","cond_code_n":"100","cond_txt_d":"晴","cond_txt_n":"晴","date":"2018-02-02","hum":"45","mr":"20:08","ms":"08:17","pcpn":"0.0","pop":"0","pres":"1024","sr":"07:02","ss":"18:13","tmp_max":"13","tmp_min":"8","uv_index":"5","vis":"20","wind_deg":"0","wind_dir":"无持续风向","wind_sc":"微风","wind_spd":"7"},{"cond_code_d":"100","cond_code_n":"101","cond_txt_d":"晴","cond_txt_n":"多云","date":"2018-02-03","hum":"39","mr":"21:08","ms":"09:03","pcpn":"0.0","pop":"0","pres":"1026","sr":"07:01","ss":"18:13","tmp_max":"14","tmp_min":"7","uv_index":"4","vis":"20","wind_deg":"0","wind_dir":"无持续风向","wind_sc":"微风","wind_spd":"8"}]
         * hourly : [{"cloud":"47","cond_code":"103","cond_txt":"晴间多云","dew":"17","hum":"65","pop":"56","pres":"1013","time":"2018-01-28 13:00","tmp":"14","wind_deg":"123","wind_dir":"东南风","wind_sc":"微风","wind_spd":"12"},{"cloud":"73","cond_code":"305","cond_txt":"小雨","dew":"18","hum":"68","pop":"75","pres":"1011","time":"2018-01-28 16:00","tmp":"15","wind_deg":"122","wind_dir":"东南风","wind_sc":"微风","wind_spd":"10"},{"cloud":"100","cond_code":"305","cond_txt":"小雨","dew":"18","hum":"77","pop":"65","pres":"1013","time":"2018-01-28 19:00","tmp":"14","wind_deg":"69","wind_dir":"东北风","wind_sc":"微风","wind_spd":"6"},{"cloud":"100","cond_code":"309","cond_txt":"毛毛雨/细雨","dew":"18","hum":"83","pop":"66","pres":"1015","time":"2018-01-28 22:00","tmp":"13","wind_deg":"48","wind_dir":"东北风","wind_sc":"微风","wind_spd":"8"},{"cloud":"100","cond_code":"309","cond_txt":"毛毛雨/细雨","dew":"13","hum":"73","pop":"24","pres":"1016","time":"2018-01-29 01:00","tmp":"11","wind_deg":"29","wind_dir":"东北风","wind_sc":"微风","wind_spd":"14"},{"cloud":"100","cond_code":"104","cond_txt":"阴","dew":"10","hum":"69","pop":"47","pres":"1016","time":"2018-01-29 04:00","tmp":"9","wind_deg":"15","wind_dir":"东北风","wind_sc":"微风","wind_spd":"15"},{"cloud":"99","cond_code":"305","cond_txt":"小雨","dew":"8","hum":"67","pop":"71","pres":"1018","time":"2018-01-29 07:00","tmp":"7","wind_deg":"22","wind_dir":"东北风","wind_sc":"3-4","wind_spd":"17"},{"cloud":"100","cond_code":"305","cond_txt":"小雨","dew":"7","hum":"67","pop":"70","pres":"1021","time":"2018-01-29 10:00","tmp":"7","wind_deg":"30","wind_dir":"东北风","wind_sc":"3-4","wind_spd":"19"}]
         * lifestyle : [{"brf":"舒适","txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。","type":"comf"},{"brf":"较冷","txt":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。","type":"drsg"},{"brf":"极易发","txt":"昼夜温差极大，且风力较强，极易发生感冒，请特别注意增减衣服保暖防寒。","type":"flu"},{"brf":"较不宜","txt":"有降水，且风力较强，气压较低，推荐您在室内进行低强度运动；若坚持户外运动，须注意避雨防风。","type":"sport"},{"brf":"适宜","txt":"有降水，虽然风稍大，但温度适宜，适宜旅游，可不要错过机会呦！","type":"trav"},{"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。","type":"uv"},{"brf":"不宜","txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。","type":"cw"},{"brf":"中","txt":"气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。","type":"air"}]
         * sunrise_sunset : [{"$ref":"$[0].daily_forecast[0]"},{"$ref":"$[0].daily_forecast[1]"},{"$ref":"$[0].daily_forecast[2]"},{"$ref":"$[0].daily_forecast[3]"},{"$ref":"$[0].daily_forecast[4]"},{"$ref":"$[0].daily_forecast[5]"},{"$ref":"$[0].daily_forecast[6]"}]
         */

        private BasicBean basic;
        private UpdateBean update;
        private String status;
        private NowBean now;
        private List<DailyForecastBean> daily_forecast;
        private List<HourlyBean> hourly;
        private List<LifestyleBean> lifestyle;
        private List<SunriseSunsetBean> sunrise_sunset;

        public BasicBean getBasic() {
            return basic;
        }

        public void setBasic(BasicBean basic) {
            this.basic = basic;
        }

        public UpdateBean getUpdate() {
            return update;
        }

        public void setUpdate(UpdateBean update) {
            this.update = update;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public NowBean getNow() {
            return now;
        }

        public void setNow(NowBean now) {
            this.now = now;
        }

        public List<DailyForecastBean> getDaily_forecast() {
            return daily_forecast;
        }

        public void setDaily_forecast(List<DailyForecastBean> daily_forecast) {
            this.daily_forecast = daily_forecast;
        }

        public List<HourlyBean> getHourly() {
            return hourly;
        }

        public void setHourly(List<HourlyBean> hourly) {
            this.hourly = hourly;
        }

        public List<LifestyleBean> getLifestyle() {
            return lifestyle;
        }

        public void setLifestyle(List<LifestyleBean> lifestyle) {
            this.lifestyle = lifestyle;
        }

        public List<SunriseSunsetBean> getSunrise_sunset() {
            return sunrise_sunset;
        }

        public void setSunrise_sunset(List<SunriseSunsetBean> sunrise_sunset) {
            this.sunrise_sunset = sunrise_sunset;
        }

        public static class BasicBean {
            /**
             * cid : CN101280601
             * location : 深圳
             * parent_city : 深圳
             * admin_area : 广东
             * cnty : 中国
             * lat : 22.54700089
             * lon : 114.08594513
             * tz : +8.0
             */

            private String cid;
            private String location;
            private String parent_city;
            private String admin_area;
            private String cnty;
            private String lat;
            private String lon;
            private String tz;

            public String getCid() {
                return cid;
            }

            public void setCid(String cid) {
                this.cid = cid;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getParent_city() {
                return parent_city;
            }

            public void setParent_city(String parent_city) {
                this.parent_city = parent_city;
            }

            public String getAdmin_area() {
                return admin_area;
            }

            public void setAdmin_area(String admin_area) {
                this.admin_area = admin_area;
            }

            public String getCnty() {
                return cnty;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getTz() {
                return tz;
            }

            public void setTz(String tz) {
                this.tz = tz;
            }
        }

        public static class UpdateBean {
            /**
             * loc : 2018-01-28 11:53
             * utc : 2018-01-28 03:53
             */

            private String loc;
            private String utc;

            public String getLoc() {
                return loc;
            }

            public void setLoc(String loc) {
                this.loc = loc;
            }

            public String getUtc() {
                return utc;
            }

            public void setUtc(String utc) {
                this.utc = utc;
            }
        }

        public static class NowBean {
            /**
             * cloud : 100
             * cond_code : 104
             * cond_txt : 阴
             * fl : 13
             * hum : 75
             * pcpn : 0.0
             * pres : 1016
             * tmp : 17
             * vis : 8
             * wind_deg : 35
             * wind_dir : 东北风
             * wind_sc : 微风
             * wind_spd : 8
             */

            private String cloud;
            private String cond_code;
            private String cond_txt;
            private String fl;
            private String hum;
            private String pcpn;
            private String pres;
            private String tmp;
            private String vis;
            private String wind_deg;
            private String wind_dir;
            private String wind_sc;
            private String wind_spd;

            public String getCloud() {
                return cloud;
            }

            public void setCloud(String cloud) {
                this.cloud = cloud;
            }

            public String getCond_code() {
                return cond_code;
            }

            public void setCond_code(String cond_code) {
                this.cond_code = cond_code;
            }

            public String getCond_txt() {
                return cond_txt;
            }

            public void setCond_txt(String cond_txt) {
                this.cond_txt = cond_txt;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public String getWind_deg() {
                return wind_deg;
            }

            public void setWind_deg(String wind_deg) {
                this.wind_deg = wind_deg;
            }

            public String getWind_dir() {
                return wind_dir;
            }

            public void setWind_dir(String wind_dir) {
                this.wind_dir = wind_dir;
            }

            public String getWind_sc() {
                return wind_sc;
            }

            public void setWind_sc(String wind_sc) {
                this.wind_sc = wind_sc;
            }

            public String getWind_spd() {
                return wind_spd;
            }

            public void setWind_spd(String wind_spd) {
                this.wind_spd = wind_spd;
            }
        }

        public static class DailyForecastBean {
            /**
             * cond_code_d : 305
             * cond_code_n : 305
             * cond_txt_d : 小雨
             * cond_txt_n : 小雨
             * date : 2018-01-28
             * hum : 64
             * mr : 14:50
             * ms : 03:25
             * pcpn : 0.5
             * pop : 86
             * pres : 1016
             * sr : 07:03
             * ss : 18:09
             * tmp_max : 18
             * tmp_min : 8
             * uv_index : 6
             * vis : 18
             * wind_deg : 1
             * wind_dir : 北风
             * wind_sc : 4-5
             * wind_spd : 24
             */

            private String cond_code_d;
            private String cond_code_n;
            private String cond_txt_d;
            private String cond_txt_n;
            private String date;
            private String hum;
            private String mr;
            private String ms;
            private String pcpn;
            private String pop;
            private String pres;
            private String sr;
            private String ss;
            private String tmp_max;
            private String tmp_min;
            private String uv_index;
            private String vis;
            private String wind_deg;
            private String wind_dir;
            private String wind_sc;
            private String wind_spd;

            public String getCond_code_d() {
                return cond_code_d;
            }

            public void setCond_code_d(String cond_code_d) {
                this.cond_code_d = cond_code_d;
            }

            public String getCond_code_n() {
                return cond_code_n;
            }

            public void setCond_code_n(String cond_code_n) {
                this.cond_code_n = cond_code_n;
            }

            public String getCond_txt_d() {
                return cond_txt_d;
            }

            public void setCond_txt_d(String cond_txt_d) {
                this.cond_txt_d = cond_txt_d;
            }

            public String getCond_txt_n() {
                return cond_txt_n;
            }

            public void setCond_txt_n(String cond_txt_n) {
                this.cond_txt_n = cond_txt_n;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getMr() {
                return mr;
            }

            public void setMr(String mr) {
                this.mr = mr;
            }

            public String getMs() {
                return ms;
            }

            public void setMs(String ms) {
                this.ms = ms;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getSr() {
                return sr;
            }

            public void setSr(String sr) {
                this.sr = sr;
            }

            public String getSs() {
                return ss;
            }

            public void setSs(String ss) {
                this.ss = ss;
            }

            public String getTmp_max() {
                return tmp_max;
            }

            public void setTmp_max(String tmp_max) {
                this.tmp_max = tmp_max;
            }

            public String getTmp_min() {
                return tmp_min;
            }

            public void setTmp_min(String tmp_min) {
                this.tmp_min = tmp_min;
            }

            public String getUv_index() {
                return uv_index;
            }

            public void setUv_index(String uv_index) {
                this.uv_index = uv_index;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public String getWind_deg() {
                return wind_deg;
            }

            public void setWind_deg(String wind_deg) {
                this.wind_deg = wind_deg;
            }

            public String getWind_dir() {
                return wind_dir;
            }

            public void setWind_dir(String wind_dir) {
                this.wind_dir = wind_dir;
            }

            public String getWind_sc() {
                return wind_sc;
            }

            public void setWind_sc(String wind_sc) {
                this.wind_sc = wind_sc;
            }

            public String getWind_spd() {
                return wind_spd;
            }

            public void setWind_spd(String wind_spd) {
                this.wind_spd = wind_spd;
            }
        }

        public static class HourlyBean {
            /**
             * cloud : 47
             * cond_code : 103
             * cond_txt : 晴间多云
             * dew : 17
             * hum : 65
             * pop : 56
             * pres : 1013
             * time : 2018-01-28 13:00
             * tmp : 14
             * wind_deg : 123
             * wind_dir : 东南风
             * wind_sc : 微风
             * wind_spd : 12
             */

            private String cloud;
            private String cond_code;
            private String cond_txt;
            private String dew;
            private String hum;
            private String pop;
            private String pres;
            private String time;
            private String tmp;
            private String wind_deg;
            private String wind_dir;
            private String wind_sc;
            private String wind_spd;

            public String getCloud() {
                return cloud;
            }

            public void setCloud(String cloud) {
                this.cloud = cloud;
            }

            public String getCond_code() {
                return cond_code;
            }

            public void setCond_code(String cond_code) {
                this.cond_code = cond_code;
            }

            public String getCond_txt() {
                return cond_txt;
            }

            public void setCond_txt(String cond_txt) {
                this.cond_txt = cond_txt;
            }

            public String getDew() {
                return dew;
            }

            public void setDew(String dew) {
                this.dew = dew;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public String getWind_deg() {
                return wind_deg;
            }

            public void setWind_deg(String wind_deg) {
                this.wind_deg = wind_deg;
            }

            public String getWind_dir() {
                return wind_dir;
            }

            public void setWind_dir(String wind_dir) {
                this.wind_dir = wind_dir;
            }

            public String getWind_sc() {
                return wind_sc;
            }

            public void setWind_sc(String wind_sc) {
                this.wind_sc = wind_sc;
            }

            public String getWind_spd() {
                return wind_spd;
            }

            public void setWind_spd(String wind_spd) {
                this.wind_spd = wind_spd;
            }
        }

        public static class LifestyleBean {
            /**
             * brf : 舒适
             * txt : 白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。
             * type : comf
             */

            private String brf;
            private String txt;
            private String type;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

        public static class SunriseSunsetBean {
            /**
             * $ref : $[0].daily_forecast[0]
             */

            private String $ref;

            public String get$ref() {
                return $ref;
            }

            public void set$ref(String $ref) {
                this.$ref = $ref;
            }
        }
    }
}
