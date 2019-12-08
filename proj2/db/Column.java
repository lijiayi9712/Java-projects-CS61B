package db;

import java.util.ArrayList;

import static db.Type.*;


public class Column {
    String name;
    Type type;
    int index;

    public Column(String nm, Type tp, int i) {
        name = nm;
        type = tp;
        index = i;
    }

    public Type getType() {
        return type;
    }

    public static ArrayList<Value> add(ArrayList<Value> c1, ArrayList<Value> c2, Table t) {
        ArrayList<Value> result = new ArrayList<>();
        for (int i = 0; i < c1.size(); i += 1) {
            result.add((c1.get(i)).add(c2.get(i)));
        }
        return result;
    }

    public static ArrayList<Value> subtract(ArrayList<Value> c1, ArrayList<Value> c2, Table t) {
        ArrayList<Value> result = new ArrayList<>();
        for (int i = 0; i < c1.size(); i += 1) {
            result.add((c1.get(i)).subtract(c2.get(i)));
        }
        return result;
    }

    public static ArrayList<Value> multi(ArrayList<Value> c1, ArrayList<Value> c2, Table t) {
        ArrayList<Value> result = new ArrayList<>();
        for (int i = 0; i < c1.size(); i += 1) {
            result.add((c1.get(i)).multi(c2.get(i)));
        }
        return result;
    }

    public static ArrayList<Value> div(ArrayList<Value> c1, ArrayList<Value> c2, Table t) {
        ArrayList<Value> result = new ArrayList<>();
        for (int i = 0; i < c1.size(); i += 1) {
            result.add((c1.get(i)).div(c2.get(i)));
        }
        return result;
    }




    @Override
    public String toString() {
        String str = "";
        if (type == INT) {
            str = "int";
        } else if (type == STRING) {
            str = "string";
        } else if (type == FLOAT) {
            str = "float";
        }
        return name + " " + str;
    }
}
