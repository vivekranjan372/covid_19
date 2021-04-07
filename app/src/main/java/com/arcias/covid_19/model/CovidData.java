package com.arcias.covid_19.model;

import java.io.Serializable;

public class CovidData implements Serializable {
    private String country;
    private String totalCases;
    private String totalDeaths;
    private String totalRecovered;
    private String newCases;
    private String newDeaths;
    private String newRecovered;
    private String date;
    public CovidData(){};

    public CovidData(String country, String totalCases, String totalDeaths,
                     String totalRecovered, String newCases, String newDeaths,
                     String newRecovered, String date) {
        this.country = country;
        this.totalCases = totalCases;
        this.totalDeaths = totalDeaths;
        this.totalRecovered = totalRecovered;
        this.newCases = newCases;
        this.newDeaths = newDeaths;
        this.newRecovered = newRecovered;
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(String totalCases) {
        this.totalCases = totalCases;
    }

    public String getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public String getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(String totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public String getNewCases() {
        return newCases;
    }

    public void setNewCases(String newCases) {
        this.newCases = newCases;
    }

    public String getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        this.newDeaths = newDeaths;
    }

    public String getNewRecovered() {
        return newRecovered;
    }

    public void setNewRecovered(String newRecovered) {
        this.newRecovered = newRecovered;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
