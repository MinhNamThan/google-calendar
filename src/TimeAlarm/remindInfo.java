package TimeAlarm;

public class remindInfo {
    private String startTime;
    private String kindEvent;
    private String Title;

    public remindInfo(String startTime, String kindEvent, String title) {
        this.startTime = startTime;
        this.kindEvent = kindEvent;
        Title = title;
    }

    public remindInfo() {
        this.startTime = null;
        this.kindEvent = null;
        Title = null;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getKindEvent() {
        return kindEvent;
    }

    public void setKindEvent(String kindEvent) {
        this.kindEvent = kindEvent;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
