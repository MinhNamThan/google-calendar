package Search;

import java.sql.Date;

public class EventList {
    private String Title;
    private Date dayStart;
    private String timeStart;
    private String addresses;

    public EventList(String title, Date dayStart, String timeStart, String addresses) {
        this.Title = title;
        this.dayStart = dayStart;
        this.timeStart = timeStart;
        this.addresses = addresses;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public Date getDayStart() {
        return dayStart;
    }

    public void setDayStart(Date dayStart) {
        this.dayStart = dayStart;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }
}
