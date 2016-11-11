package com.ape.material.weather.bean;

import android.text.TextUtils;

import com.ape.material.weather.util.FormatUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by way on 2016/11/9.
 * https://free-api.heweather.com/v5/weather?key=808caad621854628b1b3273555ae1632&city=shenzhen&lang=zh-cn
 */

public class HeWeather implements Serializable {

    private static final long serialVersionUID = 8718402607048772317L;

    private List<HeWeather5Bean> HeWeather5;

    public List<HeWeather5Bean> getHeWeather5() {
        return HeWeather5;
    }

    public HeWeather5Bean get() {
        if (getHeWeather5().size() > 0) {
            return getHeWeather5().get(0);
        }
        return null;
    }

    public boolean isOK() {
        if (this.getHeWeather5().size() > 0) {
            final HeWeather5Bean weather5Bean = get();
            return TextUtils.equals(weather5Bean.status, "ok");
        }
        return false;
    }

    public boolean hasAqi() {
        if (isOK()) {
            final HeWeather5Bean h = get();
            return h.aqi != null && h.aqi.city != null;
        }
        return true;
    }

    /**
     * 出错返回-1
     *
     * @returnTodayDailyForecastIndex
     */
    public int getTodayDailyForecastIndex() {
        int todayIndex = -1;
        if (!isOK()) {
            return todayIndex;
        }
        final HeWeather5Bean w = get();
        for (int i = 0; i < w.getDaily_forecast().size(); i++) {
            if (FormatUtil.isToday(w.getDaily_forecast().get(i).date)) {
                todayIndex = i;
                break;
            }
        }
        return todayIndex;
    }

    /**
     * 出错返回null
     *
     * @return Today DailyForecast
     */
    public HeWeather5Bean.DailyForecastBean getTodayDailyForecast() {
        final int todayIndex = getTodayDailyForecastIndex();
        if (todayIndex != -1) {
            HeWeather5Bean.DailyForecastBean forecast = get().getDaily_forecast().get(todayIndex);
            return forecast;
        }
        return null;
    }

    /**
     * 今天的气温 6~16°
     *
     * @return
     */
    public String getTodayTempDescription() {
        final int todayIndex = getTodayDailyForecastIndex();
        if (todayIndex != -1) {
            HeWeather5Bean.DailyForecastBean forecast = get().getDaily_forecast().get(todayIndex);
            return forecast.tmp.min + "~" + forecast.tmp.max + "°";
        }
        return "";
    }

    public static class HeWeather5Bean implements Serializable{
        private static final long serialVersionUID = 4802643270981893098L;
        /**
         * alarms : [{"level":"蓝色","stat":"预警中","title":"山东省青岛市气象台发布大风蓝色预警","txt":"青岛市气象台2016年08月29日15时24分继续发布大风蓝色预警信号：预计今天下午到明天，我市北风风力海上6到7级阵风9级，陆地4到5阵风7级，请注意防范。","type":"大风"}]
         * aqi : {"city":{"aqi":"54","co":"1","no2":"36","o3":"28","pm10":"55","pm25":"32","qlty":"良","so2":"5"}}
         * basic : {"city":"深圳","cnty":"中国","id":"CN101280601","lat":"22.544000","lon":"114.109000","update":{"loc":"2016-11-09 21:56","utc":"2016-11-09 13:56"}}
         * daily_forecast : [{"astro":{"mr":"13:49","ms":"00:47","sr":"06:32","ss":"17:42"},"cond":{"code_d":"101","code_n":"305","txt_d":"多云","txt_n":"小雨"},"date":"2016-11-09","hum":"73","pcpn":"0.1","pop":"0","pres":"1021","tmp":{"max":"20","min":"15"},"uv":"6","vis":"10","wind":{"deg":"32","dir":"无持续风向","sc":"微风","spd":"3"}},{"astro":{"mr":"14:33","ms":"01:44","sr":"06:33","ss":"17:41"},"cond":{"code_d":"104","code_n":"101","txt_d":"阴","txt_n":"多云"},"date":"2016-11-10","hum":"75","pcpn":"1.6","pop":"100","pres":"1021","tmp":{"max":"19","min":"15"},"uv":"6","vis":"10","wind":{"deg":"10","dir":"无持续风向","sc":"微风","spd":"8"}},{"astro":{"mr":"15:17","ms":"02:43","sr":"06:33","ss":"17:41"},"cond":{"code_d":"101","code_n":"101","txt_d":"多云","txt_n":"多云"},"date":"2016-11-11","hum":"74","pcpn":"0.0","pop":"0","pres":"1019","tmp":{"max":"22","min":"17"},"uv":"7","vis":"10","wind":{"deg":"94","dir":"无持续风向","sc":"微风","spd":"6"}}]
         * hourly_forecast : [{"cond":{"code":"305","txt":"小雨"},"date":"2016-11-09 22:00","hum":"75","pop":"0","pres":"1022","tmp":"20","wind":{"deg":"21","dir":"无持续风向","sc":"微风","spd":"13"}}]
         * now : {"cond":{"code":"104","txt":"阴"},"fl":"16","hum":"85","pcpn":"0","pres":"1022","tmp":"17","vis":"3","wind":{"deg":"20","dir":"北风","sc":"4-5","spd":"24"}}
         * status : ok
         * suggestion : {"air":{"brf":"中","txt":"气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。"},"comf":{"brf":"舒适","txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"},"cw":{"brf":"不宜","txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"},"drsg":{"brf":"较舒适","txt":"建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。"},"flu":{"brf":"较易发","txt":"天气较凉，较易发生感冒，请适当增加衣服。体质较弱的朋友尤其应该注意防护。"},"sport":{"brf":"较适宜","txt":"阴天，较适宜进行各种户内外运动。"},"trav":{"brf":"适宜","txt":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"},"uv":{"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}}
         */

        private AqiBean aqi;
        private BasicBean basic;
        private NowBean now;
        private String status;
        private SuggestionBean suggestion;
        private List<AlarmsBean> alarms;
        private List<DailyForecastBean> daily_forecast;
        private List<HourlyForecastBean> hourly_forecast;

        public AqiBean getAqi() {
            return aqi;
        }

        public void setAqi(AqiBean aqi) {
            this.aqi = aqi;
        }

        public BasicBean getBasic() {
            return basic;
        }

        public void setBasic(BasicBean basic) {
            this.basic = basic;
        }

        public NowBean getNow() {
            return now;
        }

        public void setNow(NowBean now) {
            this.now = now;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public SuggestionBean getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(SuggestionBean suggestion) {
            this.suggestion = suggestion;
        }

        public List<AlarmsBean> getAlarms() {
            return alarms;
        }

        public void setAlarms(List<AlarmsBean> alarms) {
            this.alarms = alarms;
        }

        public List<DailyForecastBean> getDaily_forecast() {
            return daily_forecast;
        }

        public void setDaily_forecast(List<DailyForecastBean> daily_forecast) {
            this.daily_forecast = daily_forecast;
        }

        public List<HourlyForecastBean> getHourly_forecast() {
            return hourly_forecast;
        }

        public void setHourly_forecast(List<HourlyForecastBean> hourly_forecast) {
            this.hourly_forecast = hourly_forecast;
        }

        public static class AqiBean implements Serializable{
            private static final long serialVersionUID = -5964836004805124606L;
            /**
             * city : {"aqi":"54","co":"1","no2":"36","o3":"28","pm10":"55","pm25":"32","qlty":"良","so2":"5"}
             */

            private CityBean city;

            public CityBean getCity() {
                return city;
            }

            public void setCity(CityBean city) {
                this.city = city;
            }

            public static class CityBean implements Serializable{
                private static final long serialVersionUID = -2811143428761620271L;
                /**
                 * aqi : 54
                 * co : 1
                 * no2 : 36
                 * o3 : 28
                 * pm10 : 55
                 * pm25 : 32
                 * qlty : 良
                 * so2 : 5
                 */

                private String aqi;
                private String co;
                private String no2;
                private String o3;
                private String pm10;
                private String pm25;
                private String qlty;
                private String so2;

                public String getAqi() {
                    return aqi;
                }

                public void setAqi(String aqi) {
                    this.aqi = aqi;
                }

                public String getCo() {
                    return co;
                }

                public void setCo(String co) {
                    this.co = co;
                }

                public String getNo2() {
                    return no2;
                }

                public void setNo2(String no2) {
                    this.no2 = no2;
                }

                public String getO3() {
                    return o3;
                }

                public void setO3(String o3) {
                    this.o3 = o3;
                }

                public String getPm10() {
                    return pm10;
                }

                public void setPm10(String pm10) {
                    this.pm10 = pm10;
                }

                public String getPm25() {
                    return pm25;
                }

                public void setPm25(String pm25) {
                    this.pm25 = pm25;
                }

                public String getQlty() {
                    return qlty;
                }

                public void setQlty(String qlty) {
                    this.qlty = qlty;
                }

                public String getSo2() {
                    return so2;
                }

                public void setSo2(String so2) {
                    this.so2 = so2;
                }
            }
        }

        public static class BasicBean implements Serializable{
            private static final long serialVersionUID = -5703567936732275283L;
            /**
             * city : 深圳
             * cnty : 中国
             * id : CN101280601
             * lat : 22.544000
             * lon : 114.109000
             * update : {"loc":"2016-11-09 21:56","utc":"2016-11-09 13:56"}
             */

            private String city;
            private String cnty;
            private String id;
            private String lat;
            private String lon;
            private UpdateBean update;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCnty() {
                return cnty;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public UpdateBean getUpdate() {
                return update;
            }

            public void setUpdate(UpdateBean update) {
                this.update = update;
            }

            public static class UpdateBean implements Serializable{
                private static final long serialVersionUID = -7867087729972388060L;
                /**
                 * loc : 2016-11-09 21:56
                 * utc : 2016-11-09 13:56
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
        }

        public static class NowBean implements Serializable{
            private static final long serialVersionUID = 1537448213382014178L;
            /**
             * cond : {"code":"104","txt":"阴"}
             * fl : 16
             * hum : 85
             * pcpn : 0
             * pres : 1022
             * tmp : 17
             * vis : 3
             * wind : {"deg":"20","dir":"北风","sc":"4-5","spd":"24"}
             */

            private CondBean cond;
            private String fl;
            private String hum;
            private String pcpn;
            private String pres;
            private String tmp;
            private String vis;
            private WindBean wind;

            public CondBean getCond() {
                return cond;
            }

            public void setCond(CondBean cond) {
                this.cond = cond;
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

            public WindBean getWind() {
                return wind;
            }

            public void setWind(WindBean wind) {
                this.wind = wind;
            }

            public static class CondBean implements Serializable{
                private static final long serialVersionUID = 4048084230537287476L;
                /**
                 * code : 104
                 * txt : 阴
                 */

                private String code;
                private String txt;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class WindBean implements Serializable{
                private static final long serialVersionUID = 999547904951255849L;
                /**
                 * deg : 20
                 * dir : 北风
                 * sc : 4-5
                 * spd : 24
                 */

                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }

        public static class SuggestionBean implements Serializable{
            private static final long serialVersionUID = 7109042187891099867L;
            /**
             * air : {"brf":"中","txt":"气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。"}
             * comf : {"brf":"舒适","txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"}
             * cw : {"brf":"不宜","txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"}
             * drsg : {"brf":"较舒适","txt":"建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。"}
             * flu : {"brf":"较易发","txt":"天气较凉，较易发生感冒，请适当增加衣服。体质较弱的朋友尤其应该注意防护。"}
             * sport : {"brf":"较适宜","txt":"阴天，较适宜进行各种户内外运动。"}
             * trav : {"brf":"适宜","txt":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"}
             * uv : {"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}
             */

            private AirBean air;
            private ComfBean comf;
            private CwBean cw;
            private DrsgBean drsg;
            private FluBean flu;
            private SportBean sport;
            private TravBean trav;
            private UvBean uv;

            public AirBean getAir() {
                return air;
            }

            public void setAir(AirBean air) {
                this.air = air;
            }

            public ComfBean getComf() {
                return comf;
            }

            public void setComf(ComfBean comf) {
                this.comf = comf;
            }

            public CwBean getCw() {
                return cw;
            }

            public void setCw(CwBean cw) {
                this.cw = cw;
            }

            public DrsgBean getDrsg() {
                return drsg;
            }

            public void setDrsg(DrsgBean drsg) {
                this.drsg = drsg;
            }

            public FluBean getFlu() {
                return flu;
            }

            public void setFlu(FluBean flu) {
                this.flu = flu;
            }

            public SportBean getSport() {
                return sport;
            }

            public void setSport(SportBean sport) {
                this.sport = sport;
            }

            public TravBean getTrav() {
                return trav;
            }

            public void setTrav(TravBean trav) {
                this.trav = trav;
            }

            public UvBean getUv() {
                return uv;
            }

            public void setUv(UvBean uv) {
                this.uv = uv;
            }

            public static class AirBean implements Serializable{
                private static final long serialVersionUID = -5949291669372902632L;
                /**
                 * brf : 中
                 * txt : 气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。
                 */

                private String brf;
                private String txt;

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
            }

            public static class ComfBean implements Serializable{
                private static final long serialVersionUID = 7392161898818639676L;
                /**
                 * brf : 舒适
                 * txt : 白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。
                 */

                private String brf;
                private String txt;

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
            }

            public static class CwBean implements Serializable{
                private static final long serialVersionUID = 5821065975275891165L;
                /**
                 * brf : 不宜
                 * txt : 不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。
                 */

                private String brf;
                private String txt;

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
            }

            public static class DrsgBean implements Serializable{
                private static final long serialVersionUID = 8274317947560570867L;
                /**
                 * brf : 较舒适
                 * txt : 建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。
                 */

                private String brf;
                private String txt;

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
            }

            public static class FluBean implements Serializable{
                private static final long serialVersionUID = -5098212090166554875L;
                /**
                 * brf : 较易发
                 * txt : 天气较凉，较易发生感冒，请适当增加衣服。体质较弱的朋友尤其应该注意防护。
                 */

                private String brf;
                private String txt;

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
            }

            public static class SportBean implements Serializable{
                private static final long serialVersionUID = -6959848390197920565L;
                /**
                 * brf : 较适宜
                 * txt : 阴天，较适宜进行各种户内外运动。
                 */

                private String brf;
                private String txt;

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
            }

            public static class TravBean implements Serializable{
                private static final long serialVersionUID = -6921325207578532910L;
                /**
                 * brf : 适宜
                 * txt : 天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。
                 */

                private String brf;
                private String txt;

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
            }

            public static class UvBean implements Serializable{
                private static final long serialVersionUID = 1805156332079370824L;
                /**
                 * brf : 最弱
                 * txt : 属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。
                 */

                private String brf;
                private String txt;

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
            }
        }

        public static class AlarmsBean implements Serializable{
            private static final long serialVersionUID = 9165771083960176140L;
            /**
             * level : 蓝色
             * stat : 预警中
             * title : 山东省青岛市气象台发布大风蓝色预警
             * txt : 青岛市气象台2016年08月29日15时24分继续发布大风蓝色预警信号：预计今天下午到明天，我市北风风力海上6到7级阵风9级，陆地4到5阵风7级，请注意防范。
             * type : 大风
             */

            private String level;
            private String stat;
            private String title;
            private String txt;
            private String type;

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getStat() {
                return stat;
            }

            public void setStat(String stat) {
                this.stat = stat;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
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

        public static class DailyForecastBean implements Serializable{
            private static final long serialVersionUID = -7405274118143553755L;
            /**
             * astro : {"mr":"13:49","ms":"00:47","sr":"06:32","ss":"17:42"}
             * cond : {"code_d":"101","code_n":"305","txt_d":"多云","txt_n":"小雨"}
             * date : 2016-11-09
             * hum : 73
             * pcpn : 0.1
             * pop : 0
             * pres : 1021
             * tmp : {"max":"20","min":"15"}
             * uv : 6
             * vis : 10
             * wind : {"deg":"32","dir":"无持续风向","sc":"微风","spd":"3"}
             */

            private AstroBean astro;
            private CondBeanX cond;
            private String date;
            private String hum;
            private String pcpn;
            private String pop;
            private String pres;
            private TmpBean tmp;
            private String uv;
            private String vis;
            private WindBeanX wind;

            public AstroBean getAstro() {
                return astro;
            }

            public void setAstro(AstroBean astro) {
                this.astro = astro;
            }

            public CondBeanX getCond() {
                return cond;
            }

            public void setCond(CondBeanX cond) {
                this.cond = cond;
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

            public TmpBean getTmp() {
                return tmp;
            }

            public void setTmp(TmpBean tmp) {
                this.tmp = tmp;
            }

            public String getUv() {
                return uv;
            }

            public void setUv(String uv) {
                this.uv = uv;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public WindBeanX getWind() {
                return wind;
            }

            public void setWind(WindBeanX wind) {
                this.wind = wind;
            }

            public static class AstroBean implements Serializable{
                private static final long serialVersionUID = 4612235552830521062L;
                /**
                 * mr : 13:49
                 * ms : 00:47
                 * sr : 06:32
                 * ss : 17:42
                 */

                private String mr;
                private String ms;
                private String sr;
                private String ss;

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
            }

            public static class CondBeanX implements Serializable{
                private static final long serialVersionUID = -8102920287943124036L;
                /**
                 * code_d : 101
                 * code_n : 305
                 * txt_d : 多云
                 * txt_n : 小雨
                 */

                private String code_d;
                private String code_n;
                private String txt_d;
                private String txt_n;

                public String getCode_d() {
                    return code_d;
                }

                public void setCode_d(String code_d) {
                    this.code_d = code_d;
                }

                public String getCode_n() {
                    return code_n;
                }

                public void setCode_n(String code_n) {
                    this.code_n = code_n;
                }

                public String getTxt_d() {
                    return txt_d;
                }

                public void setTxt_d(String txt_d) {
                    this.txt_d = txt_d;
                }

                public String getTxt_n() {
                    return txt_n;
                }

                public void setTxt_n(String txt_n) {
                    this.txt_n = txt_n;
                }
            }

            public static class TmpBean implements Serializable{
                private static final long serialVersionUID = 5875029753830019814L;
                /**
                 * max : 20
                 * min : 15
                 */

                private String max;
                private String min;

                public String getMax() {
                    return max;
                }

                public void setMax(String max) {
                    this.max = max;
                }

                public String getMin() {
                    return min;
                }

                public void setMin(String min) {
                    this.min = min;
                }
            }

            public static class WindBeanX implements Serializable{
                private static final long serialVersionUID = -1476702449825412620L;
                /**
                 * deg : 32
                 * dir : 无持续风向
                 * sc : 微风
                 * spd : 3
                 */

                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }

        public static class HourlyForecastBean implements Serializable{
            private static final long serialVersionUID = 6090339610498200124L;
            /**
             * cond : {"code":"305","txt":"小雨"}
             * date : 2016-11-09 22:00
             * hum : 75
             * pop : 0
             * pres : 1022
             * tmp : 20
             * wind : {"deg":"21","dir":"无持续风向","sc":"微风","spd":"13"}
             */

            private CondBeanXX cond;
            private String date;
            private String hum;
            private String pop;
            private String pres;
            private String tmp;
            private WindBeanXX wind;

            public CondBeanXX getCond() {
                return cond;
            }

            public void setCond(CondBeanXX cond) {
                this.cond = cond;
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

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public WindBeanXX getWind() {
                return wind;
            }

            public void setWind(WindBeanXX wind) {
                this.wind = wind;
            }

            public static class CondBeanXX implements Serializable{
                private static final long serialVersionUID = -7270642462900165772L;
                /**
                 * code : 305
                 * txt : 小雨
                 */

                private String code;
                private String txt;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class WindBeanXX implements Serializable{
                private static final long serialVersionUID = -6184267326059460941L;
                /**
                 * deg : 21
                 * dir : 无持续风向
                 * sc : 微风
                 * spd : 13
                 */

                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }
    }
}
