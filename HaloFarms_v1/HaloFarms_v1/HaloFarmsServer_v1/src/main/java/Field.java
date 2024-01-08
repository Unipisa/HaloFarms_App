import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Field {
    // name and address of field
    private String name, address;
    // date of analysis
    private String date;
    // list of points inside field
    private List<Point> points;

    public Field() {
    }

    public Field(String name, String address, List<Point> points) {
        this.name = name;
        this.address = address;
        this.points = points;
        this.date = "Not yet analyzed";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name) &&
                Objects.equals(address, field.address) &&
                Objects.equals(date, field.date) &&
                Objects.equals(points, field.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, date, points);
    }
}