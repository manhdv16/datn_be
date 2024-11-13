package com.ptit.datn.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class DataUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);
    private static final String COMMON_NOT_NULL = "common.file.notNull";
    private static final String PHONE_PATTERN = "^[0-9]*$";
    public static final char DEFAULT_ESCAPE_CHAR = '&';
    static private String saltSHA256 = "1";
    static private String AES = "AES";
    static private String DES = "DES";
    private static final String MISS_ENVIREMENT_SETTING = "{0} must be set in environment variable";
    static final String YYYY_PT = "yyyy";
    static final String YYYYmm_PT = "yyyyMM";
    public static final String REGEX_NUMBER = "^[0-9]*$";
    /**
     * Copy du lieu tu bean sang bean moi
     * Luu y chi copy duoc cac doi tuong o ngoai cung, list se duoc copy theo tham chieu
     * <p>
     * Chi dung duoc cho cac bean java, khong dung duoc voi cac doi tuong dang nhu String, Integer, Long...
     *
     * @param source
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneBean(T source) {
        try {
            if (source == null) {
                return null;
            }
            T dto = (T) source.getClass().getConstructor().newInstance();
            BeanUtils.copyProperties(source, dto);
            return dto;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public static boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    public static boolean isNullOrZero(Integer value) {
        return (value == null || value.equals(0));
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */


    /**
     * Upper first character
     *
     * @param input
     * @return
     */
    public static String upperFirstChar(String input) {
        if (DataUtils.isNullOrEmpty(input)) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String joiningFieldObject(Object object) {
        StringBuilder result = new StringBuilder();
        Field[] arrField = object.getClass().getDeclaredFields();
        for (int i = 0; i < arrField.length; ++i) {
            try {
                arrField[i].setAccessible(true);
                Object value = arrField[i].get(object);
                if (i == arrField.length - 1) {
                    result.append(safeToString(value));
                } else {
                    result.append(safeToString(value)).append("|");
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
        return result.toString();
    }


    public static Long safeToLong(Object obj1, Long defaultValue) {
        Long result = defaultValue;
        if (obj1 != null) {
            if (obj1 instanceof BigDecimal) {
                return ((BigDecimal) obj1).longValue();
            }
            if (obj1 instanceof BigInteger) {
                return ((BigInteger) obj1).longValue();
            }
            try {
                result = Long.parseLong(obj1.toString());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return Long
     */
    public static Long safeToLong(Object obj1) {
        return safeToLong(obj1, null);
    }

    public static Double safeToDouble(Object obj1, Double defaultValue) {
        Double result = defaultValue;
        if (obj1 != null) {
            try {
                result = Double.parseDouble(obj1.toString());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    public static Double safeToDouble(Object obj1) {
        return safeToDouble(obj1, 0.0);
    }


    public static Short safeToShort(Object obj1, Short defaultValue) {
        Short result = defaultValue;
        if (obj1 != null) {
            try {
                result = Short.parseShort(obj1.toString());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    /**
     * @param obj1
     * @param defaultValue
     * @return
     * @author phuvk
     */
    public static int safeToInt(Object obj1, int defaultValue) {
        int result = defaultValue;
        if (obj1 != null) {
            try {
                result = Integer.parseInt(obj1.toString());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return int
     */
    public static int safeToInt(Object obj1) {
        return safeToInt(obj1, 0);
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1, String defaultValue) {
        if (obj1 == null || obj1.toString().isEmpty()) {
            return defaultValue;
        }

        return obj1.toString();
    }

    public static Boolean safeToBoolean(Object obj1) {
        if (obj1 == null || obj1 instanceof Boolean) {
            return (Boolean) obj1;
        }
        return false;
    }


    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1) {
        return safeToString(obj1, "");
    }


    /**
     * safe equal
     *
     * @param obj1 String
     * @param obj2 String
     * @return boolean
     */
    public static boolean safeEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        return ((obj1 != null) && (obj2 != null) && obj1.toString().equals(obj2.toString()));
    }
    public static boolean safeEqualOne(Object obj1, Object obj2, Object... objects) {
        Boolean b = false;
        for (Object o : objects){
            b = Objects.equals(obj1,obj2) || Objects.equals(obj2,o) || Objects.equals(obj1,o);
        }
        return b;
    }
    public static boolean safeEqualsAll(Object obj1, Object obj2, Object... objects) {
        return Objects.equals(obj1,obj2) && Objects.equals(obj2,objects) && Objects.equals(obj1,objects);
    }
    /**
     * check null or empty
     * Su dung ma nguon cua thu vien StringUtils trong apache common lang
     *
     * @param cs String
     * @return boolean
     */
    public static boolean isNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean notNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        if(collection == null || collection.isEmpty()) return true;
        if(collection.containsAll(Collections.singleton(""))) return true;
        return false;
    }

    public static boolean isNullOrEmpty(final Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    public static boolean isNullOrEmpty(final Object[] collection) {
        return collection == null || collection.length == 0;
    }

    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static String emptyIfNull(Object obj){ // Use for cast excel
        if(obj == null) return  "";
        else return obj.toString();
    }

    public static String getStringFromDate(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getStringFromDateHaveTimeZone(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static Date getDateFromString(String strDate, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(strDate);
    }


    /**
     * Ham nay mac du nhan tham so truyen vao la object nhung gan nhu chi hoat dong cho doi tuong la string
     * Chuyen sang dung isNullOrEmpty thay the
     *
     * @param obj1
     * @return
     */
    @Deprecated
    public static boolean isStringNullOrEmpty(Object obj1) {
        return obj1 == null || "".equals(obj1.toString().trim());
    }

    public static BigInteger length(BigInteger from, BigInteger to) {
        return to.subtract(from).add(BigInteger.ONE);
    }


    /**
     * add
     *
     * @param obj1 BigDecimal
     * @param obj2 BigDecimal
     * @return BigDecimal
     */
    public static BigInteger add(BigInteger obj1, BigInteger obj2) {
        if (obj1 == null) {
            return obj2;
        } else if (obj2 == null) {
            return obj1;
        }

        return obj1.add(obj2);
    }


    public static boolean isNullObject(Object obj1) {
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return isNullOrEmpty(obj1.toString());
        }
        return false;
    }


    public static boolean isCollection(Object ob) {
        return ob instanceof Collection || ob instanceof Map;
    }


    /**
     * @param date
     * @param format yyyyMMdd, yyyyMMddhhmmss,yyyyMMddHHmmssSSS only
     * @return
     */
    public static Integer getDateInt(Date date, String format) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return Integer.parseInt(dateStr);
    }

    /**
     * @param date
     * @param format yyyyMMdd, yyyyMMddhhmmss,yyyyMMddHHmmssSSS only
     * @return
     */
    public static Long getDateLong(Date date, String format) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return Long.parseLong(dateStr);
    }


    private static void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
    }

    public static Date getFirstDateOfMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        resetTime(cal);
        return cal.getTime();
    }

    public static Date getFirstDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        resetTime(cal);
        return cal.getTime();
    }

    public static Date getFirstDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //Thang 1 thi calendar.MONTH = 0
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        resetTime(cal);
        return cal.getTime();
    }


    public static Date getDatePattern(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(date);
    }

    public static String formatDatePattern(Integer prdId, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = sdf.parse(prdId.toString());
        SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
        return sdf2.format(date);

    }

    public static String formatQuarterPattern(Integer prdId) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(prdId.toString());

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            String result2 = sdf2.format(date);

            result = (date.getMonth() / 3 + 1) + "/" + result2;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }

    public static String formatDatePattern(Long prdId, String pattern) throws ParseException {
        return formatDatePattern(prdId.intValue(), pattern);
    }

    public static String formatQuarterPattern(Long prdId) {
        return formatQuarterPattern(prdId.intValue());
    }

    public static Date add(Date fromDate, int num, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(type, num);
        return cal.getTime();
    }


    public static String dateToStringHaveTimeZone(Date fromDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(fromDate);
    }

    public static String dateToString(Date fromDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(fromDate);
    }

    public static String dateToStringQuater(Date fromDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(fromDate);
        return (fromDate.getMonth() / 3 + 1) + "/" + year;
    }


    public static String getExternalUrl(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
            logger.error(ignored.getMessage(), ignored);
        }
        return String.format("%s://%s:%s%s", protocol, hostAddress, serverPort, contextPath);
    }

    public static Instant toInstant(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof java.sql.Date) {
            return Instant.ofEpochMilli(((java.sql.Date) object).getTime());
        } else if (object instanceof Timestamp) {
            return Instant.ofEpochMilli(((Timestamp) object).getTime());
        }
        return null;
    }

    public static String trimZeros(String value) {
        try {
            if(isInteger(value)) {
                Integer num = Integer.parseInt(value);
                return num.toString();
            }

            if(isFloat(value)) {
                Double num = Double.parseDouble(value);
                return num.toString();
            }
        }catch (NumberFormatException e){
            logger.error(e.getMessage(), e);
        }
        return value;
    }

    public static boolean isInteger(Object obj) {
        if (obj == null) return false;
        String value = obj.toString();
        return Pattern.compile(INTEGER.PATTERN).matcher(value).find() || Pattern.compile(FLOAT.TRAILING_ZEROS).matcher(value).find();
    }

    private static boolean isFloat(Object obj) {
        if (obj == null) return false;
        String value = obj.toString();
        return Pattern.compile(FLOAT.PATTERN).matcher(value).find() || Pattern.compile(INTEGER.PATTERN).matcher(value).find();
    }



    interface FLOAT {
        String PATTERN = "^\\s*-?\\d+\\.\\d+\\s*$";
        String PATTERN_VALUE_LARGER_THAN_0 = "^\\s*\\d+\\.\\d+\\s*$";
        String TRAILING_ZEROS = "^\\s*(-)?\\d+\\.\\d+[0]+\\s*$";
        String LEADING_ZEROS = "^\\s*(-)?[0]+\\d+\\.\\d+\\s*$";
        String MAX_9_INTEGRALPART = "^\\s*-?\\d{1,9}\\.\\d+\\s*$";
        String MAX_9_INTEGRALPART_VALUE_LARGER_0 = "^\\s*\\d{1,9}\\.\\d+\\s*$";

        String MAX_3_INTEGRALPART_LEAST_5_DECIMALPART = "^\\s*(-)?\\d{0,3}?(.)\\d{0,5}\\s*$";

    }

    interface INTEGER {
        String LEADING_ZEROS = "^\\s*(-)?[0]+\\d+s*$";
        String PATTERN = "^\\s*-?\\d+\\s*$";
        String PATTERN_VALUE_LARGER_THAN_0 = "^\\s*\\d+\\s*$";
        String MAX_9_INTEGRALPART = "^\\s*-?\\d{1,9}\\s*$";
        String MAX_9_INTEGRALPART_VALUE_LARGER_0 = "^\\s*\\d{1,9}\\s*$";
    }

    public static boolean checkMaxLength(String s, int length) {
        return (s.length() > length);
    }

    public static String likeSpecialToStr(String str) {
        if (str == null) {
            str = "";
        }
        if (!str.trim().isEmpty()) {
            String newStr =
                str.trim()
                    .replace("\\", "\\\\")
                    .replace("\\t", "\\\\t")
                    .replace("\\n", "\\\\n")
                    .replace("\\r", "\\\\r")
                    .replace("\\z", "\\\\z")
                    .replace("\\b", "\\\\b")
                    .replaceAll("_", "\\\\_")
                    .replaceAll("%", "\\\\%");
            str = (newStr.trim());
        }
        return str;
    }

    public static String makeLikeStr(String str) {
        if (isNullOrEmpty(str)) {
            return "%%";
        }
        return "%" + str + "%";
    }



    public static String likeEscape(String value){
        if (isNullOrEmpty(value)){
            return value;
        }
        return  "%" + value.replaceAll("!","!!").replaceAll("_","!_").replaceAll("%","!%") +"%";
    }

    public static String likeEscapeOk(String value){
        if (isNullOrEmpty(value)){
            return value;
        }
        return  value.replaceAll("!","!!").replaceAll("_","!_").replaceAll("%","!%") +"%";
    }

    public static String addStringEscapse(String value, String escapse){
        if (isNullOrEmpty(value)){
            return value;
        }
        return  value.replaceAll(escapse,escapse+escapse).replaceAll("_",escapse+"_").replaceAll("%",escapse+"%");
    }
    public static String trim(String value) {
        if (isNullOrEmpty(value)){
            return null;
        }
        return value.trim();
    }

    public static boolean matcher(String str ,String regex) {
        Pattern regexCompiled = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = regexCompiled.matcher(str);
        return regexMatcher.find();
    }

    public static List<String> getAllFieldNames(String strRaw,String strGap, Object objectClass, Class clazz){
        if(isNullOrEmpty(strRaw)) return null;
        List<String> fieldNames = Arrays.stream(strRaw.split(strGap)).map(id -> {
            Field fields[] = clazz.getDeclaredFields();
            for(Field field : fields){
                try {
                    if(Integer.parseInt(id) == Integer.parseInt(field.get(objectClass) + "")){
                        return field.getName();
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }).sorted().collect(Collectors.toList());
        return fieldNames;
    }

    public static String getStringValueFromListName(List<String> names, String strGap, Object objectClass, Class clazz){
        if(names == null) return null;
        Field fields[] = clazz.getDeclaredFields();
        String result = names.stream().map(n -> {
            for(Field field : fields){
                if(field.getName().equals(n)){
                    try {
                        return field.get(objectClass).toString();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }).sorted().collect(Collectors.joining(strGap));
        return result;
    }
    public static Date getDateOfMonth(Date date){
        if(date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar.getTime();
    }

    public static Double round2Decimal(Double value){
        if (value == null){
            return null;
        }
        return Math.round(value*100)/100D;
    }
    public static boolean safeNotEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) return false;
        return !((obj1 != null) && (obj2 != null) && obj1.toString().equals(obj2.toString()));
    }


    public static String getSafeString(String value) {
        return value != null ? value : "";
    }

    public static void trimStringValues(Object model) {
        for (Field field : model.getClass().getDeclaredFields()) {
            try{
                field.setAccessible(true);
                Object value = field.get(model);
                if(value != null){
                    if(value instanceof String){
                        String trim = (String) value;
                        field.set(model,trim.trim());
                    }
                }
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }

        }
    }

    public static String escapeSql(String value){
        value = value.replace("_", "|_");
        value = value.replace("%", "|%");
        value = value.replace("[", "|[");
        value = value.replace("]", "|]");
        return "%"+value+"%";
    }



    public static String objectToJson(Object data, String defaultValue) {
        if (isNull(data)) {
            return defaultValue;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(data);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return "";
        }
    }

    public static String objectToJson(Object data) {
        return objectToJson(data, "");
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean notNullNorEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        return true;
    }


}
