package db;

import java.util.ArrayList;

import static db.Type.*;


public class Row {
    int index;
    Value[] r;
    int size;

    public Row(ArrayList<Value> row, int j, Type[] types) {
        index = j;
        size = row.size();
        r = new Value[size];
        for (int i = 0; i < size; i += 1) {
            if (types[i] == INT) {
                r[i] = row.get(i);
            } else if (types[i] == FLOAT) {
                r[i] = row.get(i);
            } else if (types[i] == STRING) {
                r[i] = row.get(i);
            }
        }
    }

    public Row(Value[] values, int j) {
        index = j;
        size = values.length;
        r = values;
    }


    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < size; i += 1) {
            str = str + r[i] + ",";
        }
        str = str.substring(0, str.length() - 1);
        return str;
    }
}
