package com.wuyi.mall.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 支付签名工具类
 *
 * @author wuyi
 * @date 2025-12-04
 */
public class PaySignUtil {

    /**
     * 生成RSA签名
     *
     * @param params     待签名参数
     * @param privateKey 私钥
     * @return 签名结果
     */
    public static String generateRsaSign(Map<String, Object> params, String privateKey) {
        // 参数排序
        String sortedParams = getSortedParams(params);
        // 简化实现，实际项目中需要使用正确的RSA签名算法
        return SecureUtil.md5(sortedParams + privateKey);
    }

    /**
     * 验证RSA签名
     *
     * @param params    待验证参数
     * @param publicKey 公钥
     * @param sign      签名值
     * @return 验证结果
     */
    public static boolean verifyRsaSign(Map<String, Object> params, String publicKey, String sign) {
        // 参数排序
        String sortedParams = getSortedParams(params);
        // 简化实现，实际项目中需要使用正确的RSA验签算法
        String generateSign = SecureUtil.md5(sortedParams + publicKey);
        return generateSign.equals(sign);
    }

    /**
     * 生成MD5签名
     *
     * @param params 待签名参数
     * @param key    密钥
     * @return 签名结果
     */
    public static String generateMd5Sign(Map<String, Object> params, String key) {
        // 参数排序
        String sortedParams = getSortedParams(params);
        // 添加密钥
        sortedParams += "&key=" + key;
        // 生成MD5签名
        return MD5.create().digestHex(sortedParams).toUpperCase();
    }

    /**
     * 验证MD5签名
     *
     * @param params 待验证参数
     * @param key    密钥
     * @param sign   签名值
     * @return 验证结果
     */
    public static boolean verifyMd5Sign(Map<String, Object> params, String key, String sign) {
        // 生成签名
        String generateSign = generateMd5Sign(params, key);
        // 比较签名
        return generateSign.equals(sign);
    }

    /**
     * 生成SHA256签名
     *
     * @param params 待签名参数
     * @param key    密钥
     * @return 签名结果
     */
    public static String generateSha256Sign(Map<String, Object> params, String key) {
        // 参数排序
        String sortedParams = getSortedParams(params);
        // 添加密钥
        sortedParams += "&key=" + key;
        // 生成SHA256签名
        return SecureUtil.sha256().digestHex(sortedParams).toUpperCase();
    }

    /**
     * 验证SHA256签名
     *
     * @param params 待验证参数
     * @param key    密钥
     * @param sign   签名值
     * @return 验证结果
     */
    public static boolean verifySha256Sign(Map<String, Object> params, String key, String sign) {
        // 生成签名
        String generateSign = generateSha256Sign(params, key);
        // 比较签名
        return generateSign.equals(sign);
    }

    /**
     * 参数排序，生成待签名字符串
     *
     * @param params 待签名参数
     * @return 排序后的参数字符串
     */
    private static String getSortedParams(Map<String, Object> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        // 按字母顺序排序
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Object value = params.get(key);
            // 跳过空值和签名参数
            if (value != null && !"sign".equals(key) && !"sign_type".equals(key)) {
                sb.append(key).append("=").append(value).append("&");
            }
        }
        // 移除最后一个&符号
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 加密敏感信息（银行卡号/支付金额）
     *
     * @param sensitiveInfo 敏感信息
     * @param key           加密密钥
     * @return 加密后的信息
     */
    public static String encryptSensitiveInfo(String sensitiveInfo, String key) {
        return SecureUtil.aes(key.getBytes()).encryptBase64(sensitiveInfo);
    }

    /**
     * 解密敏感信息
     *
     * @param encryptedInfo 加密后的信息
     * @param key           解密密钥
     * @return 解密后的信息
     */
    public static String decryptSensitiveInfo(String encryptedInfo, String key) {
        return SecureUtil.aes(key.getBytes()).decryptStr(encryptedInfo);
    }

    /**
     * 银行卡号脱敏（显示前6后4）
     *
     * @param cardNo 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskCardNo(String cardNo) {
        if (cardNo == null || cardNo.length() < 10) {
            return cardNo;
        }
        return cardNo.substring(0, 6) + "****" + cardNo.substring(cardNo.length() - 4);
    }
}
