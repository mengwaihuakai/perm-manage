package com.solid.subscribe.web.perm.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.util.InetAddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

/**
 * @author ShaoYongJun
 */
public final class InternetProtocol {

    private static Logger log = LoggerFactory.getLogger(InternetProtocol.class);

    /**
     * 构造函数.
     */
    private InternetProtocol() {
    }

    /**
     * 获取客户端IPV4 地址.<br>
     * 支持多级反向代理
     *
     * @param request HttpServletRequest
     * @return 客户端真实IP地址
     */
    public static String getRemoteIpV4(final HttpServletRequest request) {
        try {
            String remoteAddr = request.getHeader("X-Forwarded-For");
            // 如果通过多级反向代理，X-Forwarded-For的值不止一个，而是一串用逗号分隔的IP值，此时取X-Forwarded-For中第一个非unknown的有效IP字符串
            if (isEffective(remoteAddr) && (remoteAddr.indexOf(',') > -1)) {
                String[] array = remoteAddr.split(",");
                for (String element : array) {
                    if (isEffective(element) && !isInnerAddr(element) && InetAddressUtils.isIPv4Address(element)) {
                        remoteAddr = element;
                        break;
                    }
                }
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-IP");
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            if (remoteAddr.contains(",")) {
                String[] tmpArr = remoteAddr.split(",");
                for (String ip : tmpArr) {
                    if (isEffective(ip) && !isInnerAddr(ip) && InetAddressUtils.isIPv4Address(ip)) {
                        remoteAddr = ip;
                        break;
                    }
                }
            }
            return remoteAddr;
        } catch (Exception e) {
            log.error("get romote ip error,error message:" + e.getMessage());
            return "";
        }
    }

    /**
     * 获取客户端IP地址.<br>
     * 支持多级反向代理
     *
     * @param request HttpServletRequest
     * @return 客户端真实IP地址
     */
    public static String getRemoteAddr(final HttpServletRequest request) {
        try {
            String remoteAddr = request.getHeader("X-Forwarded-For");
            // 如果通过多级反向代理，X-Forwarded-For的值不止一个，而是一串用逗号分隔的IP值，此时取X-Forwarded-For中第一个非unknown的有效IP字符串
            if (isEffective(remoteAddr) && (remoteAddr.indexOf(',') > -1)) {
                String[] array = remoteAddr.split(",");
                for (String element : array) {
                    if (isEffective(element) && !isInnerAddr(element)) {
                        remoteAddr = element;
                        break;
                    }
                }
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-IP");
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            if (remoteAddr.contains(",")) {
                String[] tmpArr = remoteAddr.split(",");
                for (String t : tmpArr) {
                    if (isEffective(t) && !isInnerAddr(t)) {
                        remoteAddr = t;
                        break;
                    }
                }
            }
            return remoteAddr;
        } catch (Exception e) {
            log.error("get romote ip error,error message:" + e.getMessage());
            return "";
        }
    }

    /**
     * 获取客户端源端口
     *
     * @param request
     * @return
     */
    public static Long getRemotePort(final HttpServletRequest request) {
        try {
            String port = request.getHeader("remote-port");
            if (StringUtils.isNotEmpty(port)) {
                try {
                    return Long.parseLong(port);
                } catch (NumberFormatException ex) {
                    log.error("convert port to long error , port:	" + port);
                    return 0L;
                }
            } else {
                return 0L;
            }
        } catch (Exception e) {
            log.error("get romote port error,error message:" + e.getMessage());
            return 0L;
        }
    }

    /**
     * 远程地址是否有效.
     *
     * @param remoteAddr 远程地址
     * @return true代表远程地址有效，false代表远程地址无效
     */
    private static boolean isEffective(final String remoteAddr) {
        boolean isEffective = false;
        if ((null != remoteAddr) && (!"".equals(remoteAddr.trim()))
                && (!"unknown".equalsIgnoreCase(remoteAddr.trim()))) {
            isEffective = true;
        }
        return isEffective;
    }


    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }

    public static boolean isInnerAddr(String remoteAddr) {
        if (remoteAddr == null) {
            return false;
        }

        if (remoteAddr.indexOf("127.0.0.1") != -1
                || remoteAddr.startsWith("10.10.")
                || remoteAddr.indexOf("200.192") != -1) {
            return true;
        }
        return false;
    }


    /**
     * 获取本地IP
     *
     * @return
     * @throws Exception
     */
    public static String getHostAddress() throws Exception {
        Enumeration<NetworkInterface> netInterfaces = null;
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                InetAddress ip = ips.nextElement();
                if (ip.isSiteLocalAddress()) {
                    return ip.getHostAddress();
                }
            }
        }
        return InetAddress.getLocalHost().getHostAddress();
    }

    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**
     * 判断端口是否被占用
     *
     * @param port
     * @return
     */
    public static boolean isPortAvailable(int port) {
        Socket s = new Socket();
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
