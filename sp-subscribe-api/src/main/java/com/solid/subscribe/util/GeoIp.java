package com.solid.subscribe.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.Country;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * @author fanyongju
 * @Title: GeoIp
 * @date 2019/4/3 14:25
 */
@Component
public class GeoIp {
    private static final Logger logger = LoggerFactory.getLogger(GeoIp.class);

    private static boolean isInit = false;
    private static DatabaseReader countryReader = null;
    private static DatabaseReader ispReader = null;

    @PostConstruct
    public void inti() throws Exception {
        countryReader = new DatabaseReader.Builder(new ClassPathResource("GeoIP2-Country.mmdb").getInputStream()).build();
        ispReader = new DatabaseReader.Builder(new ClassPathResource("GeoIP2-ISP.mmdb").getInputStream()).build();
        isInit = countryReader != null;

        logger.info("load maxmind geoip db: " + isInit);
    }

    public GeoInfo findGeoInfoByRequest(HttpServletRequest req) {
        String ip = getClientIP(req);
        return findGeoInfoByIP(ip);

    }

    public GeoInfo findGeoInfoByIP(String ip) {
        if ((!isInit) || StringUtils.isEmpty(ip)) {
            return null;
        }
        try {
            GeoInfo geoInfo = new GeoInfo();
            geoInfo.setIp(ip);
            // country
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse contryResponse = countryReader.country(ipAddress);
            Country country = contryResponse.getCountry();
            String country_code = country.getIsoCode();
            geoInfo.setCountry(country_code);

            // aso
            IspResponse ispResponse = ispReader.isp(ipAddress);
            Integer asn = ispResponse.getAutonomousSystemNumber();
            String aso = ispResponse.getAutonomousSystemOrganization();
            geoInfo.setAsn(asn);
            geoInfo.setAso(aso);
            logger.debug("findGeoInfoByIP: {} -> {} {} {}", ip, country_code, asn, aso);
            return geoInfo;
        } catch (Exception e) {
            logger.error("findGeoInfoByIP failed: {} errorMessage: {}", ip, e.getMessage());
            return null;
        }
    }

    public String getClientIP(HttpServletRequest req) {
        // X-Real-IP
        String clientIP;
        if (req.getHeader("X-Real-IP") != null && !req.getHeader("X-Real-IP").isEmpty()) {
            clientIP = req.getHeader("X-Real-IP").trim();
            return clientIP;
        }
        // X-Forwarded-For
        if (req.getHeader("X-Forwarded-For") != null && !req.getHeader("X-Forwarded-For").isEmpty()) {
            // only need the first hop
            String[] hops = req.getHeader("X-Forwarded-For").split(",", 1);
            clientIP = hops.length > 0 ? hops[0].trim() : "";
            if (!clientIP.isEmpty()) {
                return clientIP;
            }
        }
        // check the remote address from connection
        return req.getRemoteHost();
    }

    public static class GeoInfo {
        private String ip;
        private String country;
        private String aso;
        private Integer asn;

        // Getter & Setter

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getAso() {
            return aso;
        }

        public void setAso(String aso) {
            this.aso = aso;
        }

        public Integer getAsn() {
            return asn;
        }

        public void setAsn(Integer asn) {
            this.asn = asn;
        }
    }
}
