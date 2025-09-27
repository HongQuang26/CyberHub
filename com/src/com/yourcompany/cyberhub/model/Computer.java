package com.yourcompany.netcafe.model;

public class Computer {
    private int computerId;
    private String computerName;
    private String status; // AVAILABLE, IN_USE, MAINTENANCE

    public Computer(int computerId, String computerName, String status) {
        this.computerId = computerId;
        this.computerName = computerName;
        this.status = status;
    }

    // Getters and Setters
    public int getComputerId() { return computerId; }
    public void setComputerId(int computerId) { this.computerId = computerId; }
    public String getComputerName() { return computerName; }
    public void setComputerName(String computerName) { this.computerName = computerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}