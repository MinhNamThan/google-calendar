package Convert;

public class ConvertName {
    private String Event;

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public static String convertEventToSql(String event){
        if(event.equals("sự kiện")) return "Event";
        if(event.equals("lịch hẹn")) return "Appointment";
        if(event.equals("sinh nhật")) return "Birthday";
        if(event.equals("ngày lễ")) return "Holiday";
        return null;
    }
    public static String convertNameEvent(String name){
        if(name.equals("Event")) return "Sự kiện";
        if(name.equals("Appointment")) return "Lịch hẹn";
        if(name.equals("Birthday")) return "Sinh nhật";
        if(name.equals("Holiday")) return "Ngày lễ";
        return null;
    }
}
