package db;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static db.Type.*;


public class Table<T> {
    String header;
    String name; // name of the table
    int collength; // length of a column
    int rowlength; // length of a row
    ArrayList<Type> types;
    ArrayList<Row> rows; // points to the Row instances
    ArrayList<Column> cols; // points to the Column instances

    /* Creates a Table with String nm becoming the name and String[] arguments
     * becoming the column names and types. The constructor will parse through the
     * String and find which are names and which are types.
     */
    Table(String nm, String arguments) { //x int, y int, z int
        header = arguments;
        name = nm;
        collength = 0;
        rowlength = 0;
        this.rows = new ArrayList<>(); // points to the Row instances
        this.cols = new ArrayList<>(); // points to the Column instances
        this.types = new ArrayList<Type>();
        String[] helper = arguments.split(",|\\s+");
        int count = 0;
        for (String i : helper) {
            if (i.length() != 0) {
                count += 1;
            }
        }
        String[] args = new String[count];
        int j = 0;
        for (int i = 0; i < count; i += 1) {
            if (helper[j].length() == 0) {
                j += 1;
                i = i - 1;
            } else {
                args[i] = helper[j];
                j += 1;
            }
        }
        // split based on whitespace OR comma
        if (args.length % 2 != 0) {
            throw new RuntimeException("Faulty arguments");
        }
        int i = 0;
        while (i < args.length) {
            String N = args[i];
            String T = args[i + 1];
            if (T.equals("int")) {
                cols.add(new Column(N, INT, (i / 2)));
                types.add(INT);
            } else if (T.equals("string")) {
                cols.add(new Column(N, STRING, (i / 2)));
                types.add(STRING);
            } else if (T.equals("float")) {
                cols.add(new Column(N, FLOAT, (i / 2)));
                types.add(FLOAT);
            } else {
                throw new IllegalArgumentException("ERROR: Malformed table Type");
            }
            i += 2;
        }
        rowlength = args.length / 2;
    }


    private Type[] getTypes() {
        Type[] ts = new Type[rowlength];
        for (int i = 0; i < rowlength; i += 1) {
            ts[i] = this.types.get(i);
        }
        return ts;
    }

    /* Inserts a new row into the Table. */
    public void insert(ArrayList<Value> rw) {
        if (rw.size() != types.size()) {
            throw new IllegalArgumentException("ERROR: Length don't match for inserting new row");
        }
        for (int i = 0; i < types.size(); i += 1) {
            /*(!(rw.get(i).equals(
                    new Mystr("NOVALUE"))) || (rw.get(i).equals(new Mystr("NaN")))*/
            if (rw.get(i).type == STRING) {
                if ((!rw.get(i).strv.equals("NaN")) && (!rw.get(i).strv.equals("NOVALUE"))) {
                    if (!(rw.get(i).type.equals(types.get(i)))) {
                        throw new IllegalArgumentException
                        ("ERROR: Type don't match for inserting new row");
                    }
                }
            }
        }
        rows.add(new Row(rw, collength, getTypes()));
        collength += 1;
    }

    public void insert(Value[] values) {
        rows.add(new Row(values, collength));
        collength += 1;
    }

    /* Builds and returns an ArrayList containing the values of a Column. */
    ArrayList<Value> getColumnVal(String colName) {
        for (Column c : cols) {
            if (c.name.equals(colName)) {
                return buildCol(c.index);
            }
        }
        return null;
    }

    Column getColumn(String colName) {
        try {
            for (Column c : cols) {
                if (c.name.equals(colName)) {
                    return c;
                }
            }
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }

    private ArrayList<Value> buildCol(int index) {
        ArrayList<Value> c = new ArrayList<>();
        for (int i = 0; i < collength; i += 1) {
            Value[] row = rows.get(i).r;
            c.add(row[index]);
        }
        return c;
    }

    /* Prints the Columns and then the Rows of the Table. */
    void print() {
        System.out.println(this);
    }


    void insertcols(ArrayList<Value>[] columnss) {
        //ArrayList<Value>[] rowbuild = new ArrayList[cols[0].size()];
        //List<List<Value>> rowbuild = new ArrayList<>(cols[0].size());
        int helper = columnss[0].size();
        HashMap<Integer, ArrayList> rowbuild = new HashMap<>();
        for (int i = 0; i < helper; i += 1) {
            ArrayList<Value> eachrow = new ArrayList<>();
            for (ArrayList<Value> col : columnss) {
                eachrow.add(col.get(i));
            }
            rowbuild.put(i, eachrow);
        }
        for (int i = 0; i < rowbuild.size(); i += 1) {
            this.insert(rowbuild.get(i));
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < (rowlength - 1); i += 1) {
            str = str + cols.get(i).toString() + ",";
        }
        str = str + cols.get(rowlength - 1).toString() + "\n";
        for (int i = 0; i < collength; i += 1) {
            Value[] row = rows.get(i).r;
            for (int j = 0; j < (row.length - 1); j += 1) {
                str = str + row[j].toString() + ",";
            }
            str = str + row[rowlength - 1].toString() + "\n";
        }
        return str;
    }

    ArrayList<String> getSharedCols(Table table) {  //get the names of the shared Columns
        ArrayList<String> shared = new ArrayList<>();
        for (Column c1 : this.cols) {
            for (Column c2 : (ArrayList<Column>) table.cols) {
                if (c1.name.equals(c2.name)) {
                    shared.add(c2.name);
                }
            }
        }
        return shared;
    }

    ArrayList<Integer> getColIndex(ArrayList<String> shared) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < shared.size(); i += 1) {
            for (int j = 0; j < this.cols.size(); j += 1) {
                String n1 = this.cols.get(j).name;
                String n2 = shared.get(i);
                if (n1.equals(n2)) {
                    index.add(j);
                }
            }
        }
        return index;
    }

    void clearrows() {
        this.rows = new ArrayList<>();
        this.collength = 0;
    }

    Table copy() {
        Table temp = new Table("name", this.header);
        for (Row r : this.rows) {
            temp.insert(new ArrayList<>(Arrays.asList(r.r)));
        }
        return temp;
    }


    public boolean compareTo(Column col1, Column col2) {
        ArrayList<Value> value1 = getColumnVal(col1.name);
        ArrayList<Value> value2 = getColumnVal(col2.name);
        for (int i = 0; i < value1.size(); i += 1) {
            if (value1.get(i).compareTo(value2.get(i)) != 0) {
                return false;
            }
        }
        return true;
    }

}

