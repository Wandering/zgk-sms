package cn.thinkjoy.sms.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gryang on 16/05/11.
 */
public class PhoneUtil {
    private static List<String> mobile = new ArrayList<>();
    private static List<String> unicom = new ArrayList<>();
    private static List<String> telecom = new ArrayList<>();

    static {
        mobile.add("134");
        mobile.add("135");
        mobile.add("136");
        mobile.add("137");
        mobile.add("138");
        mobile.add("139");
        mobile.add("147");
        mobile.add("150");
        mobile.add("151");
        mobile.add("152");
        mobile.add("157");
        mobile.add("158");
        mobile.add("159");
        mobile.add("187");
        mobile.add("188");
        mobile.add("1705");

        unicom.add("130");
        unicom.add("131");
        unicom.add("132");
        unicom.add("133");
        unicom.add("153");
        unicom.add("155");
        unicom.add("156");
        unicom.add("185");
        unicom.add("186");
        unicom.add("1709");

        telecom.add("180");
        telecom.add("189");
        telecom.add("133");
        telecom.add("153");
        telecom.add("1700");
    }

    public static String getPhoneNetWork(String phone) {
        String phoneFirst = phone.substring(0,3);
        if (mobile.contains(phoneFirst)) {
            return "mobile";
        } else if (unicom.contains(phoneFirst)) {
            return "unicom";
        } else if (telecom.contains(phoneFirst)) {
            return "telecom";
        } else {
            return "mobile";
        }
    }
}
