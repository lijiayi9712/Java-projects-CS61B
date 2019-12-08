package db;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static db.Type.FLOAT;
import static db.Type.INT;
import static db.Type.STRING;

public class Database {
    ArrayList<Table> tables;

    public Database() {
        this.tables = new ArrayList<>();
    }

    public String transact(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            return ("ERROR: .*");
        }
    }


    Table product(Table t1, Table t2) {
        // Cartesian products of two tables when the two tables don't have any shared columns
        String helper = getTableCol(t1, t2);
        Table t3 = new Table("t3", helper);
        for (int i = 0; i < t1.collength; i += 1) {
            Row row1 = (Row) t1.rows.get(i);
            Value[] temp1 = row1.r;
            //http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
            for (int j = 0; j < t2.collength; j += 1) {
                Row row2 = (Row) t2.rows.get(j);
                Value[] temp2 = row2.r;
                Value[] c = (Value[]) Array.newInstance(temp1.getClass().
                        getComponentType(), temp1.length + temp2.length);
                System.arraycopy(temp1, 0, c, 0, temp1.length);
                System.arraycopy(temp2, 0, c, temp1.length, temp2.length);
                t3.insert(new ArrayList<>(Arrays.asList(c)));
                //http://stackoverflow.com/questions/9811261/convert-an-array-into-an-arraylist
            }
        }
        return t3;
    }


    boolean rowmatches(Table t1, Row r1, Table t2, Row r2, ArrayList<String> shared) {
        //helper function for Join method to ensure two Rows match before merging them
        Value[] value1 = r1.r;
        Value[] value2 = r2.r;
        ArrayList<Integer> index1 = t1.getColIndex(shared);
        ArrayList<Integer> index2 = t2.getColIndex(shared);
        for (int i = 0; i < index1.size(); i += 1) {
            if (!value1[index1.get(i)].equals(value2[index2.get(i)])) {
                return false;
            }
        }
        return true;
    }

    boolean helper(Value sum, Value v, String compare) {
        //helper function to compare two Values and handle edge cases "NOVALUE"
        if (sum.equals("NOVALUE")) {
            return false;
        } else if (v.equals("NOVALUE")) {
            return false;
        } else if (sum.equals("NaNERROR: Type don't match for inserting new row")) {
            return (sum.strv.equals("NaN"));
        } else {
            if (compare.equals(">")) {
                return sum.compareTo(v) > 0;
            } else if (compare.equals("<")) {
                return sum.compareTo(v) < 0;
            } else if (compare.equals("<=")) {
                return (sum.compareTo(v) < 0) || (sum.compareTo(v) == 0);
            } else if (compare.equals(">=")) {
                return (sum.compareTo(v) > 0) || (sum.compareTo(v) == 0);
            } else if (compare.equals("!=")) {
                return (sum.compareTo(v) != 0);
            } else {
                return sum.compareTo(v) == 0;
            }
        }
    }


    boolean addhelper(Table t, Row r, Column[] columns,
                      String operation, String compare, Value v) {
        Value[] value = r.r;
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < t.cols.size(); i += 1) {
            if (Arrays.asList(columns).contains(t.cols.get(i))) {
                index.add(i);    //index is an arraylist of indexes
            }
        }
        Value[] values = new Value[index.size()];
        for (int i = 0; i < index.size(); i += 1) {
            values[i] = value[index.get(i)];
        }
        if (compare.equals(">")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.add(n);
            }
            sum = sum.subtract(values[0]);
            if (sum.equals("NOVALUE")) {
                return false;
            } else {
                return helper(sum, v, compare);
            }
        } else if (compare.equals("<")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.add(n);
            }
            sum = sum.subtract(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.add(n);
            }
            sum = sum.subtract(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals(">=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.add(n);
            }
            sum = sum.subtract(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("<=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.add(n);
            }
            sum = sum.subtract(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("!=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.add(n);
            }
            sum = sum.subtract(values[0]);
            return helper(sum, v, compare);
        } else {
            throw new IllegalArgumentException("Wrong condition statement");
        }
    }

    boolean subtracthelper(Table t, Row r, Column[] columns,
                           String operation, String compare, Value v) {
        Value[] value = r.r;
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < t.cols.size(); i += 1) {
            if (Arrays.asList(columns).contains(t.cols.get(i))) {
                index.add(i);    //index is an arraylist of indexes
            }
        }
        Value[] values = new Value[index.size()];
        for (int i = 0; i < index.size(); i += 1) {
            values[i] = value[index.get(i)];
        }
        if (operation.equals(">")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.subtract(n);
            }
            sum = sum.add(values[0]);
            return helper(sum, v, compare);
        } else if (operation.equals("<")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.subtract(n);
            }
            sum = sum.add(values[0]);
            return helper(sum, v, compare);
        } else if (operation.equals("=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.subtract(n);
            }
            sum = sum.add(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals(">=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.subtract(n);
            }
            sum = sum.add(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("<=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.subtract(n);
            }
            sum = sum.add(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("!=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.subtract(n);
            }
            sum = sum.add(values[0]);
            return helper(sum, v, compare);
        } else {
            throw new IllegalArgumentException("Wrong condition statement");
        }
    }

    boolean multihelper(Table t, Row r, Column[] columns,
                        String operation, String compare, Value v) {
        Value[] value = r.r;
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < t.cols.size(); i += 1) {
            if (Arrays.asList(columns).contains(t.cols.get(i))) {
                index.add(i);    //index is an arraylist of indexes
            }
        }
        Value[] values = new Value[index.size()];
        for (int i = 0; i < index.size(); i += 1) {
            values[i] = value[index.get(i)];
        }
        if (operation.equals(">")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (operation.equals("<")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (operation.equals("=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals(">=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("<=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("!=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else {
            throw new IllegalArgumentException("Wrong condition statement");
        }
    }


    boolean divhelper(Table t, Row r, Column[] columns,
                      String operation, String compare, Value v) {
        Value[] value = r.r;
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < t.cols.size(); i += 1) {
            if (Arrays.asList(columns).contains(t.cols.get(i))) {
                index.add(i);    //index is an arraylist of indexes
            }
        }
        Value[] values = new Value[index.size()];
        for (int i = 0; i < index.size(); i += 1) {
            values[i] = value[index.get(i)];
        }
        if (operation.equals(">")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (operation.equals("<")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (operation.equals("=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals(">=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("<=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else if (compare.equals("!=")) {
            Value sum = values[0];
            for (Value n : values) {
                sum = sum.multi(n);
            }
            sum = sum.div(values[0]);
            return helper(sum, v, compare);
        } else {
            throw new IllegalArgumentException("Wrong condition statement");
        }
    }


    boolean rowcompare(Table t, Row r, Column[] columns,
                       String operation, String compare, Value v) {
        //Helper functions for select by Checking
        // whether these two rows satisfy the requirements in user inputs
        Value[] rowvalue = r.r;
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < t.cols.size(); i += 1) {
            if (Arrays.asList(columns).contains(t.cols.get(i))) {
                index.add(i);    //index is an arraylist of indexes
            }
        }
        Value[] values = new Value[index.size()];
        for (int i = 0; i < index.size(); i += 1) {
            values[i] = rowvalue[index.get(i)];
        }
        if (operation == null) {
            Value sum = values[0];
            if (sum.equals("NOVALUE")) {
                return false;
            } else {
                return helper(sum, v, compare);
            }
        } else if (operation.equals("+")) {
            return addhelper(t, r, columns,
                    operation, compare, v);
        } else if (operation.equals("-")) {
            return subtracthelper(t, r, columns,
                    operation, compare, v);
        } else if (operation.equals("*")) {
            return multihelper(t, r, columns,
                    operation, compare, v);
        } else if (operation.equals("/")) {
            return divhelper(t, r, columns,
                    operation, compare, v);
        } else {
            throw new IllegalArgumentException("Wrong condition statement");
        }
    }


    ArrayList<Value> mergerow(Table t1, Row r1, Table t2,
                              Row r2, ArrayList<String> shared) {
        //Merge two rows , one of the helper function for Join
        ArrayList<Value> inserting = new ArrayList<Value>();
        if (rowmatches(t1, r1, t2, r2, shared)) {
            Value[] values1 = (Value[]) r1.r;
            Value[] values2 = (Value[]) r2.r;
            for (int index = 0; index < values1.length; index += 1) {
                inserting.add(values1[index]);
            }
            ArrayList<Integer> indexes = t2.getColIndex(shared);
            for (Integer index = 0; index < values2.length; index += 1) {
                if (!indexes.contains(index)) {
                    inserting.add(values2[index]);
                }
            }
        }
        return inserting;
    }

    Table joinTwo(Table t1, Table t2) {   //Join two tables
        if (t1.equals(t2)) {
            return t1;
        }
        Table t3 = new Table("t3", getTableCol(t1, t2));
        ArrayList<String> shared = t1.getSharedCols(t2);
        if (shared.size() == 0) {
            return product(t1, t2);
        } else {
            ArrayList<Value> inserting = new ArrayList<>();
            for (int i = 0; i < t1.rows.size(); i += 1) {
                for (int j = 0; j < t2.rows.size(); j += 1) {
                    Row r1 = (Row) t1.rows.get(i);
                    Row r2 = (Row) t2.rows.get(j);
                    inserting = mergerow(t1, r1, t2, r2, shared);
                    if (inserting.size() != 0) {
                        t3.insert(inserting);
                    }
                }
            }
        }
        return t3;
    }


    Table join(ArrayList<Table> ts) {
        //Join the list of tables by Joining two tables once
        try {
            Table result = ts.get(0);
            if (ts.size() == 1) {
                return result;
            }
            for (int i = 1; i < ts.size(); i += 1) {
                result = joinTwo(result, ts.get(i));
            }
            return result;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException();
        }
    }

    static String getTableCol(Table t1, Table t2) {
        //Find all the column names the two table shared and concatenate into a long string
        String str = "";
        Iterator iter1 = t1.cols.iterator();
        while (iter1.hasNext()) {
            Column temp = (Column) iter1.next();
            String item = temp.name;
            if (!str.contains(item)) {
                str += temp.toString() + " ";
            }
        }
        Iterator iter2 = t2.cols.iterator();
        while (iter2.hasNext()) {
            Column temp = (Column) iter2.next();
            String item = temp.name;
            if (!str.contains(item)) {
                str += temp.toString() + " ";
            }
        }
        return str;
    }


    // Various common constructs, simplifies parsing.
    private static final String
            REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern
            CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern
            CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+"
                    +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    +
                    SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    +
                    "\\s*(?:,\\s*.+?\\s*)*)");


    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1),
                    m.group(2), m.group(3), m.group(4));
        } else {
            return ("ERROR: .*");
        }
    }

    private String createNewTable(String name, String[] cols) {
        try {
            for (Table table : tables) {
                if (table.name.equals(name)) {
                    return ("ERROR: .The table already exist");
                } else if (cols.length == 0) {
                    return ("ERROR: .The table has no column");
                }
            }
            StringJoiner joiner = new StringJoiner(",");
            for (int i = 0; i < cols.length; i++) {
                joiner.add(cols[i]);
            }
            String colSentence = joiner.toString();
            Table newly = new Table(name, colSentence);
            tables.add(newly);
            return "";
        } catch (RuntimeException e) {
            return "ERROR: .*";
        }
    }


    private String createSelectedTable(String name, String exprs,
                                       String ts, String conds) {
        Table tb = selecttable(exprs, ts, conds);
        Table table = new Table(name, tb.header);
        for (int i = 0; i < tb.rows.size(); i += 1) {
            Row each = (Row) tb.rows.get(i);
            table.insert(new ArrayList<>(Arrays.asList(each.r)));
        }
        this.tables.add(table);
        return "";
    }

    private String loadTable(String name) {
        try {
            BufferedReader reader = null;
            File file = new File(name + ".tbl");
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line == null) {
                return "ERROR: . File is empty and cannot be loaded";
            }
            Table newly = null;
            try {
                newly = new Table(name, line);
            } catch (RuntimeException e) {
                return "ERROR: .Malformed table";
            }

            while ((line = reader.readLine()) != null) {
                String[] helper = line.split(COMMA);
                Value[] actual = new Value[helper.length];
                ArrayList<Type> types = newly.types;
                for (int i = 0; i < helper.length; i += 1) {
                    if (types.get(i) == INT) {
                        actual[i] = new Myint(Integer.parseInt(helper[i]));
                    } else if (types.get(i) == FLOAT) {
                        actual[i] = new Myfloat(Float.parseFloat(helper[i]));
                    } else if (types.get(i) == STRING) {
                        actual[i] = new Mystr(helper[i]);
                    }
                }
                ArrayList<Value> inserting = new ArrayList(Arrays.asList(actual));
                try {
                    newly.insert(inserting);
                } catch (IllegalArgumentException e) {
                    return ("ERROR: ");
                }
            }

            for (int i = 0; i < tables.size(); i += 1) {
                if (tables.get(i).name.equals(name)) {
                    tables.remove(i);
                    tables.add(i, newly);
                    return "";
                }
            }
            tables.add(newly);
        } catch (IOException e) {
            return ("ERROR: .unable to load Table.");
        }
        return "";
    }
    //Bufferreader citation:
    // http://stackoverflow.com/questions/16265693/how-to-use-buffered-reader-in-java

    private String storeTable(String name) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter((name + ".tbl"), "UTF-8");
            Table aim = null;
            for (Table table : tables) {
                if (table.name.equals(name)) {
                    aim = table;
                }
            }
            if (aim == null) {
                return ("ERROR: the table doesn't exist");
            }
            ArrayList<Row> R = aim.rows;
            String header = aim.header;
            writer.println(header);
            for (int i = 0; i < R.size(); i += 1) {
                writer.println(R.get(i).toString());
            }
            writer.close();
        } catch (IOException e) {
            return ("ERROR: There is no table named " + name + " in this database");
        }
        return "";
    }

    private String dropTable(String name) {
        try {
            Iterator<Table> it = tables.iterator();
            int size = tables.size();
            while (it.hasNext()) {
                Table user = it.next();
                if (user.name.equals(name)) {
                    it.remove();
                }
            }
            int change = tables.size();
            if (change == size) {
                throw new RuntimeException("ERROR: Table doesn't exist");
            }
            //http://stackoverflow.com/questions/10502164/
            // how-do-i-remove-an-object-from-an-arraylist-in-java
        } catch (RuntimeException e) {
            return ("ERROR: unable to drop the Table.");
        }
        return "";
    }

    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return "ERROR: *";
        }
        try {
            Table aim = null;
            for (Table table : tables) {
                if (table.name.equals(m.group(1))) {
                    aim = table;
                }
            }
            int count = 0;

            String helper = m.group(2);
            for (int i = 0; i < helper.length(); i += 1) {
                char h = helper.charAt(i);
                if (h == ',') {
                    count += 1;
                }
            }
            ArrayList<String> inserting = new ArrayList(Arrays.asList(m.group(2).split(COMMA)));
            if (count != (inserting.size() - 1)) {
                throw new IllegalArgumentException("ERROR: *");
            }
            ArrayList<Value> actual = new ArrayList();
            ArrayList<Type> types = aim.types;
            for (int i = 0; i < inserting.size(); i += 1) {
                if (types.get(i) == INT) {
                    if (inserting.get(i).equals("NOVALUE")) {
                        actual.add(i, new Mystr("NOVALUE"));
                    } else {
                        actual.add(i, new Myint(Integer.parseInt(inserting.get(i))));
                    }
                } else if (types.get(i) == FLOAT) {
                    if (inserting.get(i).equals("NOVALUE")) {
                        actual.add(i, new Mystr("NOVALUE"));
                    } else {
                        actual.add(i, new Myfloat(Float.parseFloat(inserting.get(i))));

                    }
                } else if (types.get(i) == STRING) {
                    if (inserting.get(i).equals("NOVALUE")) {
                        actual.add(i, new Mystr(inserting.get(i)));
                    } else if (inserting.get(i).indexOf('\'') >= 0) {
                        actual.add(i, new Mystr(inserting.get(i))); // check for single quote?
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
            if (actual.size() != aim.rowlength) {
                return "ERROR: unable to insert the row into the Table.";
            }
            aim.insert(actual);
            //System.out.printf("You are trying to insert the
            // row \"%s\" into the table %s\n", m.group(2), m.group(1));
        } catch (RuntimeException e) {
            return "ERROR: unable to insert the row into the Table.";
        }
        return "";
    }

    private String printTable(String name) {
        for (int i = 0; i < tables.size(); i += 1) {
            Table table = this.tables.get(i);
            if ((table.name).equals(name)) {
                return table.toString();
            }
        }
        return ("ERROR: Table don't exist");
    }

    private String select(String expr) {
        try {
            Matcher m = SELECT_CLS.matcher(expr);
            if (!m.matches()) {
                throw new IllegalArgumentException("Malformed select: %s\n" + expr);
            }
            return select(m.group(1), m.group(2), m.group(3));
        } catch (RuntimeException e) {
            return "ERROR: Malformed select statement";
        }
    }


    private String select(String expressions, String ts, String conds) {
        Table selected = selecttable(expressions, ts, conds);
        return selected.toString();
    }


    private ArrayList sthelper(String operation, String[] columns, Table joint) {
        ArrayList helper = new ArrayList();
        ArrayList<Value> colvalue1 = (ArrayList<Value>) joint.getColumnVal(columns[0]);
        ArrayList<Value> colvalue2 = (ArrayList<Value>) joint.getColumnVal(columns[1]);
        if (operation.equals("+")) {
            helper = Column.add(colvalue1, colvalue2, joint);
        } else if (operation.equals("-")) {
            helper =
                    Column.subtract(colvalue1, colvalue2, joint);
        } else if (operation.equals("*")) {
            helper =
                    Column.multi(colvalue1, colvalue2, joint);
        } else if (operation.equals("/")) {
            helper =
                    Column.div(colvalue1, colvalue2, joint);
        }
        return helper;
    }

    private Table selecttable(
            String expressions, String tbls, String conds) {
        ArrayList<Table> tablelist = new ArrayList<>();
        String[] tablenames = tbls.split(COMMA);
        for (String name : tablenames) {
            for (Table table : this.tables) {
                if (table.name.equals(name)) {
                    tablelist.add(table);
                }
            }
        }
        if (!(tablelist.size() == tablenames.length)) {
            throw new IllegalArgumentException("ERROR: No such table");
        }
        Table joint = join(tablelist); Table inter;
        String[] exp = expressions.split(COMMA); String selectedcols = "";
        Column[] newcols = new Column[exp.length];
        ArrayList<Value>[] newcolvalues = new ArrayList[exp.length];
        for (int i = 0; i < exp.length; i += 1) {
            String exprs = exp[i];
            if (exprs.equals("*")) {
                return joint;
            } else {
                String[] seperation = exprs.split("\\s+as\\s+");
                if (seperation.length == 1) {
                    newcols[i] = joint.getColumn(exprs);
                    newcolvalues[i] = joint.getColumnVal(exprs);
                    selectedcols += (newcols[i].name
                            +
                            " " + newcols[i].type.toString().toLowerCase() + ",");
                } else {
                    String[] helper = seperation[0].split("(?!^)");
                    String operation = null;
                    int index = 0;
                    for (int j = 0; j < helper.length; j += 1) {
                        if (helper[j].equals("+") || helper[j].equals("-")
                                || helper[j].equals("*") || helper[j].equals("/")) {
                            operation = helper[j];
                            index = j;
                        }
                    }
                    String[] eli1 = (seperation[0].substring(0, index)).split("\\s+");
                    String[] eli2 = seperation[0].substring
                            (index + 1, seperation[0].length()).split("\\s+");
                    String c1 = ""; String c2 = "";
                    for (String s : eli1) {
                        if (s.length() != 0) {
                            c1 = s;
                        }
                    }
                    for (String s : eli2) {
                        if (s.length() != 0) {
                            c2 = s;
                        }
                    }
                    String[] columns = new String[]{c1, c2}; String newname = seperation[1];
                    Value v1 = (Value) joint.getColumnVal(columns[0]).get(0);
                    Value v2 = (Value) joint.getColumnVal(columns[1]).get(0);
                    Type tp = (v1.add(v2)).type;
                    newcols[i] = new Column(newname, tp, i);
                    newcolvalues[i] = sthelper(operation, columns, joint);
                    selectedcols += (
                            newcols[i].name + " " + newcols[i].type.toString().toLowerCase() + ",");
                }
            }
        }
        selectedcols = selectedcols.substring(0, selectedcols.length() - 1);
        inter = new Table("intrer", selectedcols);
        inter.insertcols(newcolvalues);
        Table finalresult = inter.copy();
        String[] conditions = null;
        try {
            conditions = conds.split(AND);
        } catch (NullPointerException e) {
            return inter;
        }
        for (String cond : conditions) {
            finalresult = conditionhandle(finalresult, cond, newcols);
        }
        return finalresult;
    }



    private void justtosplit(ArrayList helper, Column[] newcols, String[] each) {
        for (int i = 0; i < newcols.length; i += 1) {
            for (int j = 0; j < each.length; j += 1) {
                if (newcols[i].name.equals(each[j])) {
                    helper.add(newcols[i].name);
                }
            }
        }
    }


    public Table conditionhandle(Table inter, String cond, Column[] newcols) {
        Table finalresult = new Table("name", inter.header);
        Value v = null; String operation = null; String compare = null;
        String[] each = cond.split("\\s+"); ArrayList<Type> typecheck = new ArrayList<>();
        ArrayList<String> helper = new ArrayList<>(); justtosplit(helper, newcols, each);
        ArrayList<Integer> index = inter.getColIndex(helper);
        for (Integer i : index) {
            Column h = (Column) inter.cols.get(i); typecheck.add(h.type);
        }
        String columnname = null; Column[] tobecompared = null;
        if (each.length % 2 == 0) {
            throw new IllegalArgumentException("ERROR: *");
        } else {
            try {
                if (each.length == 5) {
                    tobecompared = new Column[]
                    {inter.getColumn(each[0]), inter.getColumn(each[2])};
                    Type t1 = typecheck.get(0); Type t2 = typecheck.get(2); operation = each[1];
                    if (each[each.length - 1].indexOf('\'') >= 0) {
                        if (t1 == STRING) {
                            v = new Mystr(each[each.length - 1]);
                        } else {
                            throw new IllegalArgumentException("ERROR: *");
                        }
                    } else if ((t1 == INT) && (t2 == INT)) {
                        v = new Myint(Integer.parseInt(each[each.length - 1]));
                    } else if ((t2 == FLOAT) | (t1 == FLOAT)) {
                        v = new Myfloat(Float.parseFloat(each[each.length - 1]));
                    } else {
                        columnname = each[each.length - 1];
                    }
                } else if (each.length == 3) {
                    tobecompared = new Column[]{inter.getColumn(each[0])};
                    Type t = typecheck.get(0);
                    compare = each[1];
                    if (each[each.length - 1].indexOf('\'') >= 0) {
                        if (t == STRING) {
                            each[each.length - 1] = (each[each.length - 1].substring
                                    (1, each[each.length - 1].length() - 1));
                            v = new Mystr(each[each.length - 1]);
                        } else {
                            throw new IllegalArgumentException("ERROR: *");
                        }
                    } else if ((t != STRING) && (each[each.length - 1].
                            getClass().equals(String.class))) {
                        columnname = each[each.length - 1];
                    } else if (t == INT) {
                        v = new Myint(Integer.parseInt(each[each.length - 1]));
                    } else if (t == FLOAT) {
                        v = new Myfloat(Float.parseFloat(each[each.length - 1]));
                    } else {
                        columnname = each[each.length - 1];
                    }
                }
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("ERROR: Incompatible types");
            }
        }
        ArrayList<Row> savedrows = inter.rows;
        finalresult.clearrows(); Iterator iter1 = savedrows.iterator();
        if (columnname == null) {
            while (iter1.hasNext()) {
                Row row = (Row) iter1.next();
                if (rowcompare(inter, row, tobecompared, operation, compare, v)) {
                    finalresult.insert(new ArrayList<>(Arrays.asList(row.r)));
                }
            }
            return finalresult;
        } else {
            ArrayList<Value> values = inter.getColumnVal(columnname);
            Iterator iter2 = values.iterator();
            while (iter2.hasNext() && iter1.hasNext()) {
                Row row = (Row) iter1.next(); Value vl = (Value) iter2.next();
                if (rowcompare(inter, row, tobecompared, operation, compare, vl)) {
                    finalresult.insert(new ArrayList<>(Arrays.asList(row.r)));
                }
            }
        }
        return finalresult;
    }
}
