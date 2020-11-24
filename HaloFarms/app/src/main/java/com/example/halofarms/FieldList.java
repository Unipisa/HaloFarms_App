package com.example.halofarms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
/**
 * Class representing a Field list containing a list of analysis of the same Field.
 * This is the class saved to Firestore.
*/
public class FieldList {
    // name and address of field
    private String name, address;
    // list containing different analysis of the same field
    private List<Field> fields = new ArrayList<>();
    public FieldList() {}
    public FieldList(String name, String address, List<Field> fields) {
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
