package com.wuyi.mall.util;

/**
 * 敏感信息脱敏工具类
 *
 * @author wuyi
 * @date 2025-12-04
 */
public class DesensitizeUtil {

    /**
     * 身份证号脱敏
     * 示例：110101199001011234 → 110*********1234
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String desensitizeIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 3) + "*********" + idCard.substring(14);
    }

    /**
     * 手机号脱敏
     * 示例：13800138000 → 138****8000
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String desensitizePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 银行卡号脱敏
     * 示例：6226090000000000 → 6226****0000
     *
     * @param bankAccount 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String desensitizeBankAccount(String bankAccount) {
        if (bankAccount == null || bankAccount.length() < 8) {
            return bankAccount;
        }
        return bankAccount.substring(0, 4) + "****" + bankAccount.substring(bankAccount.length() - 4);
    }

    /**
     * 邮箱脱敏
     * 示例：test@example.com → t***t@example.com
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String desensitizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        if (username.length() <= 2) {
            return username + "@" + domain;
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
    }

    /**
     * 验证身份证号格式
     *
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        // 简单验证，实际项目中需要更复杂的校验规则
        return idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    }

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
    }
}
