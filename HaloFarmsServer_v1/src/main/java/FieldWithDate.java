import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FieldWithDate {

    private String name, address;
    private List<Field> fields = new ArrayList<>();

    public FieldWithDate() {}

    public FieldWithDate(String name, String address, List<Field> fields) {
        this.name = name;
        this.address = address;
        this.fields = fields;
    }


    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
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

}
