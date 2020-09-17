package com.webscraper.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AlexaSiteInfoData {

    private String websiteName;
    private String optimizationOpportunities;
    private String searchTraffic;
    private String globalSiteRanking;
    private String dailyTimeOnSite;

    public AlexaSiteInfoData() {
    }

    public AlexaSiteInfoData(String websiteName, String yearFounded, String searchTraffic, String globalRanking, String totalVisits) {
        this.websiteName = websiteName;
        this.optimizationOpportunities = yearFounded;
        this.searchTraffic = searchTraffic;
        this.globalSiteRanking = globalRanking;
        this.dailyTimeOnSite = totalVisits;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getOptimizationOpportunities() {
        return optimizationOpportunities;
    }

    public void setOptimizationOpportunities(String optimizationOpportunities) {
        this.optimizationOpportunities = optimizationOpportunities;
    }

    public String getSearchTraffic() {
        return searchTraffic;
    }

    public void setSearchTraffic(String searchTraffic) {
        this.searchTraffic = searchTraffic;
    }

    public String getGlobalSiteRanking() {
        return globalSiteRanking;
    }

    public void setGlobalSiteRanking(String globalSiteRanking) {
        this.globalSiteRanking = globalSiteRanking;
    }

    public String getDailyTimeOnSite() {
        return dailyTimeOnSite;
    }

    public void setDailyTimeOnSite(String dailyTimeOnSite) {
        this.dailyTimeOnSite = dailyTimeOnSite;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
