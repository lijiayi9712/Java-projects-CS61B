package db;

import static db.Type.*;


public abstract class Value<T> implements Comparable<Value> {
    Type type;
    Column col;
    float flov;
    int intv;
    String strv;

    public abstract Value add(Value x);
    public abstract Value subtract(Value x);
    public abstract Value div(Value x);
    public abstract Value multi(Value x);

    public abstract int compareTo(Value v);

    //public abstract int addtype(Value, Type t2);

    public abstract boolean equals(Value value); }
        /*if (!this.type.equals(value.type)) {
            return false;
        } else {
            return (this.compareTo(value) == 0);
        }*/

