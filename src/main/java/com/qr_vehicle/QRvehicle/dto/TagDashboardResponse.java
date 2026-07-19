package com.qr_vehicle.QRvehicle.dto;

public class TagDashboardResponse {

    private long totalSheets;
    private long totalTags;
    private long availableTags;
    private long assignedTags;

    public TagDashboardResponse() {
    }

    public long getTotalSheets() {
        return totalSheets;
    }

    public void setTotalSheets(long totalSheets) {
        this.totalSheets = totalSheets;
    }

    public long getTotalTags() {
        return totalTags;
    }

    public void setTotalTags(long totalTags) {
        this.totalTags = totalTags;
    }

    public long getAvailableTags() {
        return availableTags;
    }

    public void setAvailableTags(long availableTags) {
        this.availableTags = availableTags;
    }

    public long getAssignedTags() {
        return assignedTags;
    }

    public void setAssignedTags(long assignedTags) {
        this.assignedTags = assignedTags;
    }
}