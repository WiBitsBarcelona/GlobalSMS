package eu.globaldevelopers.globalsms.Class;

public class Transaction {
    private float totalDieselAmount;
    private float totalAdblueAmount;
    private float totalRedAmount;
    private float totalGasAmount;
    private float totalDieselLiters;
    private float totalAdblueLiters;
    private float totalRedLiters;
    private float totalGasKilos;
    private int totalTransactions;

    public float getTotalDieselAmount() {
        return totalDieselAmount;
    }

    public void setTotalDieselAmount(float totalDieselAmount) {
        this.totalDieselAmount = totalDieselAmount;
    }

    public void addTotalDieselAmount(float totalDieselAmount) {
        this.totalDieselAmount += totalDieselAmount;
    }

    public float getTotalAdblueAmount() {
        return totalAdblueAmount;
    }

    public void setTotalAdblueAmount(float totalAdblueAmount) {
        this.totalAdblueAmount = totalAdblueAmount;
    }

    public void addTotalAdblueAmount(float totalAdblueAmount) {
        this.totalAdblueAmount += totalAdblueAmount;
    }

    public float getTotalRedAmount() {
        return totalRedAmount;
    }

    public void setTotalRedAmount(float totalRedAmount) {
        this.totalRedAmount = totalRedAmount;
    }

    public void addTotalRedAmount(float totalRedAmount) {
        this.totalRedAmount += totalRedAmount;
    }

    public float getTotalGasAmount() {
        return totalGasAmount;
    }

    public void setTotalGasAmount(float totalGasAmount) {
        this.totalGasAmount = totalGasAmount;
    }

    public void addTotalGasAmount(float totalGasAmount) {
        this.totalGasAmount += totalGasAmount;
    }

    public float getTotalDieselLiters() {
        return totalDieselLiters;
    }

    public void setTotalDieselLiters(float totalDieselLiters) {
        this.totalDieselLiters = totalDieselLiters;
    }

    public void addTotalDieselLiters(float totalDieselLiters) {
        this.totalDieselLiters += totalDieselLiters;
    }

    public float getTotalAdblueLiters() {
        return totalAdblueLiters;
    }

    public void setTotalAdblueLiters(float totalAdblueLiters) {
        this.totalAdblueLiters = totalAdblueLiters;
    }

    public void addTotalAdblueLiters(float totalAdblueLiters) {
        this.totalAdblueLiters += totalAdblueLiters;
    }

    public float getTotalRedLiters() {
        return totalRedLiters;
    }

    public void setTotalRedLiters(float totalRedLiters) {
        this.totalRedLiters = totalRedLiters;
    }

    public void addTotalRedLiters(float totalRedLiters) {
        this.totalRedLiters += totalRedLiters;
    }

    public float getTotalGasKilos() {
        return totalGasKilos;
    }

    public void setTotalGasKilos(float totalGasKilos) {
        this.totalGasKilos = totalGasKilos;
    }

    public void addTotalGasKilos(float totalGasKilos) {
        this.totalGasKilos += totalGasKilos;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public void addTotalTransactions(int totalTransactions) {
        this.totalTransactions += totalTransactions;
    }

    public float getTotalLiters() {
        return this.totalDieselLiters + this.totalAdblueLiters + this.totalRedLiters + this.totalGasKilos;
    }
}
