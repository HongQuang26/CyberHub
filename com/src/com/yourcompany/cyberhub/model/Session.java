package com.yourcompany.cyberhub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Session {
    private int sessionId;
    private int userId;
    private int computerId;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalCost;

    public Session(int sessionId, int userId, int computerId, String username, LocalDateTime startTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.computerId = computerId;
        this.username = username;
        this.startTime = startTime;
    }

    public int getSessionId() { return sessionId; }
    public int getUserId() { return userId; }
    public int getComputerId() { return computerId; }
    public String getUsername() { return username; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BigDecimal getTotalCost() { return totalCost; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}