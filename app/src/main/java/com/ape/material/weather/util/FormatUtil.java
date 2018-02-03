package com.ape.material.weather.util;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.HeWeather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    private static final String TAG = "FormatUtil";

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188、178
	    联通：130、131、132、152、155、156、185、186、176
	    电信：133、153、180、189、（1349卫通）、177
	    总结起来就是第一位必定为1，第二位必定为3或5或8或7，其他位置的可以为0-9
		 */
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[3458]"代表第二位可以为3、4、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 判断email格式是否正确
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 判断是否全是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断身份证格式
     */
    public static boolean isIdCardNo(String idNum) {
        //定义判别用户身份证号的正则表达式（要么是15位或18位，最后一位可以为字母）
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
        //通过Pattern获得Matcher
        Matcher idNumMatcher = idNumPattern.matcher(idNum);
        if (!idNumMatcher.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判定输入汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 检测String是否全是中文
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 判断是否是银行卡号
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     */
    public static boolean IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
            return false;
        }
        // =======================(end)========================

        // ================ 数字 除最后一位都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
            return false;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (!isDataFormat(strYear + "-" + strMonth + "-" + strDay)) {
            errorInfo = "身份证生日无效。";
            Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围。";
                Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
                return false;
            }
        } catch (NumberFormatException | ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
            return false;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
            return false;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (!Ai.equals(IDStr)) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                Log.e(TAG, "ID:" + "errorInfo=" + errorInfo);
                return false;
            }
        } else {
            return true;
        }
        // =====================(end)=====================

        return true;
    }


    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 验证日期字符串是否是YYYY-MM-DD格式
     */
    private static boolean isDataFormat(String str) {
        boolean flag = false;
        // String
        // regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1 = Pattern.compile(regxStr);
        Matcher isNo = pattern1.matcher(str);
        if (isNo.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 空值null返回"",防止脏数据奔溃
     */
    public static String checkValue(String str) {
        return TextUtils.isEmpty(str) ? "" : str;
    }

    /**
     * 是否是今天2015-11-05 04:00 合法data格式： 2015-11-05 04:00 或者2015-11-05
     */
    public static boolean isToday(String date) {
        if (TextUtils.isEmpty(date) || date.length() < 10) {// 2015-11-05
            // length=10
            return false;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String today = format.format(new Date());
            if (TextUtils.equals(today, date.substring(0, 10))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 转换日期2015-11-05为今天、明天、昨天，或者是星期几
     */
    public static String prettyDate(String date) {
        try {
            final String[] strs = date.split("-");
            final int year = Integer.valueOf(strs[0]);
            final int month = Integer.valueOf(strs[1]);
            final int day = Integer.valueOf(strs[2]);
            Calendar c = Calendar.getInstance();
            int curYear = c.get(Calendar.YEAR);
            int curMonth = c.get(Calendar.MONTH) + 1;// Java月份从0月开始
            int curDay = c.get(Calendar.DAY_OF_MONTH);
            if (curYear == year && curMonth == month) {
                if (curDay == day) {
                    return "今天";
                } else if ((curDay + 1) == day) {
                    return "明天";
                } else if ((curDay - 1) == day) {
                    return "昨天";
                }
            }
            c.set(year, month - 1, day);
            // http://www.tuicool.com/articles/Avqauq
            // 一周第一天是否为星期天
            boolean isFirstSunday = (c.getFirstDayOfWeek() == Calendar.SUNDAY);
            // 获取周几
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            // 若一周第一天为星期天，则-1
            if (isFirstSunday) {
                dayOfWeek = dayOfWeek - 1;
                if (dayOfWeek == 0) {
                    dayOfWeek = 7;
                }
            }
            // 打印周几
            // System.out.println(weekDay);

            // 若当天为2014年10月13日（星期一），则打印输出：1
            // 若当天为2014年10月17日（星期五），则打印输出：5
            // 若当天为2014年10月19日（星期日），则打印输出：7
            switch (dayOfWeek) {
                case 1:
                    return "周一";
                case 2:
                    return "周二";
                case 3:
                    return "周三";
                case 4:
                    return "周四";
                case 5:
                    return "周五";
                case 6:
                    return "周六";
                case 7:
                    return "周日";
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 把Weather转换为对应的BaseDrawer.Type
     */
//    public static BaseDrawer.Type convertWeatherType(HeWeather weather) {
//        if (weather == null || !weather.isOK()) {
//            return BaseDrawer.Type.DEFAULT;
//        }
//        final boolean isNight = isNight(weather);
//        try {
//            final int w = Integer.valueOf(weather.getWeather().getNow().getCond().getCode());
//            switch (w) {
//                case 100:
//                    return isNight ? BaseDrawer.Type.CLEAR_N : BaseDrawer.Type.CLEAR_D;
//                case 101:// 多云
//                case 102:// 少云
//                case 103:// 晴间多云
//                    return isNight ? BaseDrawer.Type.CLOUDY_N : BaseDrawer.Type.CLOUDY_D;
//                case 104:// 阴
//                    return isNight ? BaseDrawer.Type.OVERCAST_N : BaseDrawer.Type.OVERCAST_D;
//                // 200 - 213是风
//                case 200:
//                case 201:
//                case 202:
//                case 203:
//                case 204:
//                case 205:
//                case 206:
//                case 207:
//                case 208:
//                case 209:
//                case 210:
//                case 211:
//                case 212:
//                case 213:
//                    return isNight ? BaseDrawer.Type.WIND_N : BaseDrawer.Type.WIND_D;
//                case 300:// 阵雨Shower Rain
//                case 301:// 强阵雨 Heavy Shower Rain
//                case 302:// 雷阵雨 Thundershower
//                case 303:// 强雷阵雨 Heavy Thunderstorm
//                case 304:// 雷阵雨伴有冰雹 Hail
//                case 305:// 小雨 Light Rain
//                case 306:// 中雨 Moderate Rain
//                case 307:// 大雨 Heavy Rain
//                case 308:// 极端降雨 Extreme Rain
//                case 309:// 毛毛雨/细雨 Drizzle Rain
//                case 310:// 暴雨 Storm
//                case 311:// 大暴雨 Heavy Storm
//                case 312:// 特大暴雨 Severe Storm
//                case 313:// 冻雨 Freezing Rain
//                    return isNight ? BaseDrawer.Type.RAIN_N : BaseDrawer.Type.RAIN_D;
//                case 400:// 小雪 Light Snow
//                case 401:// 中雪 Moderate Snow
//                case 402:// 大雪 Heavy Snow
//                case 403:// 暴雪 Snowstorm
//                case 407:// 阵雪 Snow Flurry
//                    return isNight ? BaseDrawer.Type.SNOW_N : BaseDrawer.Type.SNOW_D;
//                case 404:// 雨夹雪 Sleet
//                case 405:// 雨雪天气 Rain And Snow
//                case 406:// 阵雨夹雪 Shower Snow
//                    return isNight ? BaseDrawer.Type.RAIN_SNOW_N : BaseDrawer.Type.RAIN_SNOW_D;
//                case 500:// 薄雾
//                case 501:// 雾
//                    return isNight ? BaseDrawer.Type.FOG_N : BaseDrawer.Type.FOG_D;
//                case 502:// 霾
//                case 504:// 浮尘
//                    return isNight ? BaseDrawer.Type.HAZE_N : BaseDrawer.Type.HAZE_D;
//                case 503:// 扬沙
//                case 506:// 火山灰
//                case 507:// 沙尘暴
//                case 508:// 强沙尘暴
//                    return isNight ? BaseDrawer.Type.SAND_N : BaseDrawer.Type.SAND_D;
//                default:
//                    return isNight ? BaseDrawer.Type.UNKNOWN_N : BaseDrawer.Type.UNKNOWN_D;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isNight ? BaseDrawer.Type.UNKNOWN_N : BaseDrawer.Type.UNKNOWN_D;
//    }
    public static boolean isNight(HeWeather weather) {
        if (weather == null || !weather.isOK()) {
            return false;
        }
        // SimpleDateFormat time=new SimpleDateFormat("yyyy MM dd HH mm ss");
        try {
            final Date date = new Date();
            String todaydate = (new SimpleDateFormat("yyyy-MM-dd")).format(date);
            String todaydate1 = (new SimpleDateFormat("yyyy-M-d")).format(date);
            HeWeather.HeWeather5Bean.DailyForecastBean todayForecast = null;
            for (HeWeather.HeWeather5Bean.DailyForecastBean forecast : weather.getWeather().getDaily_forecast()) {
                if (TextUtils.equals(todaydate, forecast.getDate()) || TextUtils.equals(todaydate1, forecast.getDate())) {
                    todayForecast = forecast;
                    break;
                }
            }
            if (todayForecast != null) {
                final int curTime = Integer.valueOf((new SimpleDateFormat("HHmm").format(date)));
                final int srTime = Integer.valueOf(todayForecast.getAstro().getSr().replaceAll(":", ""));// 日出
                final int ssTime = Integer.valueOf(todayForecast.getAstro().getSs().replaceAll(":", ""));// 日落
                if (curTime > srTime && curTime <= ssTime) {// 是白天
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 把Weather转换为对应的BaseDrawer.Type
     */
    public static @DrawableRes
    int convertWeatherIcon(String weatherCode) {
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final boolean isNotNight = hourOfDay >= 7 && hourOfDay <= 18;
        final int w = Integer.valueOf(weatherCode);
        switch (w) {
            case 100:
                return isNotNight ? R.drawable.ic_stat_icon_sun : R.drawable.ic_stat_icon_sun_night;
            case 101:// 多云
            case 102:// 少云
            case 103:// 晴间多云
                return isNotNight ? R.drawable.ic_stat_icon_cloudy : R.drawable.ic_stat_icon_cloudy_night;
            case 104:// 阴
                return R.drawable.ic_stat_icon_overcast;
            // 200 - 213是风
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
                return R.drawable.ic_stat_icon_sun;
            case 300:// 阵雨Shower Rain
            case 305:// 小雨 Light Rain
            case 308:// 极端降雨 Extreme Rain
            case 309:// 毛毛雨/细雨 Drizzle Rain
                return R.drawable.ic_stat_icon_lightrain;
            case 301:// 强阵雨 Heavy Shower Rain
            case 302:// 雷阵雨 Thundershower
            case 303:// 强雷阵雨 Heavy Thunderstorm
            case 304:// 雷阵雨伴有冰雹 Hail
                return R.drawable.ic_stat_icon_thundershower;
            case 306:// 中雨 Moderate Rain
            case 307:// 大雨 Heavy Rain
                return R.drawable.ic_stat_icon_moderaterain;
            case 310:// 暴雨 Storm
            case 311:// 大暴雨 Heavy Storm
            case 312:// 特大暴雨 Severe Storm
                return R.drawable.ic_stat_icon_heavyrain;
            case 313:// 冻雨 Freezing Rain
                return R.drawable.ic_stat_icon_icerain;
            case 400:// 小雪 Light Snow
            case 401:// 中雪 Moderate Snow
            case 407:// 阵雪 Snow Flurry
                return R.drawable.ic_stat_icon_lightsnow;
            case 402:// 大雪 Heavy Snow
            case 403:// 暴雪 Snowstorm
                return R.drawable.ic_stat_icon_snowstorm;
            case 404:// 雨夹雪 Sleet
            case 405:// 雨雪天气 Rain And Snow
            case 406:// 阵雨夹雪 Shower Snow
                return R.drawable.ic_stat_icon_sleet;
            case 500:// 薄雾
            case 501:// 雾
                return R.drawable.ic_stat_icon_foggy;
            case 502:// 霾
            case 504:// 浮尘
                return R.drawable.ic_stat_icon_haze;
            case 503:// 扬沙
            case 506:// 火山灰
            case 507:// 沙尘暴
            case 508:// 强沙尘暴
                return R.drawable.ic_stat_icon_sand;
            default:
                return R.drawable.ic_stat_icon_na;
        }

    }
}

