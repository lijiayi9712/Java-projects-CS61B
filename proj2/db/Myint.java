package db;

/**
 * Created by LiJiayi on 2/27/17.
 */public class Myint extends Value {
    public Myint(int val, Column col) {
        type = Type.INT;
        intv = val;
        this.col = col;
    }

    public Myint(int val) {
        type = Type.INT;
        intv = val;
    }



    @Override
    public boolean equals(Value x) {
        if (x.type.equals(this.type)) {
            if (x.intv == this.intv) {
                return true;
            }
        }
        return false;
    }

    public int getintv() {
        return intv;
    }

    public Value add(Value x) {
        if (x.type.equals(this.type)) {
            return new Myint(this.intv + x.intv);
        } else if (x.type.equals(Type.FLOAT)) {
            return new Myfloat(this.intv + x.flov);
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return this;
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    public Value subtract(Value x) {
        if (x.type.equals(this.type)) {
            return new Myint(this.intv - x.intv);
        } else if (x.type.equals(Type.FLOAT)) {
            return new Myfloat(this.intv - x.flov);
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return this;
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    public Value multi(Value x) {
        if (x.type.equals(this.type)) {
            return new Myint(this.intv * x.intv);
        } else if (x.type.equals(Type.FLOAT)) {
            return new Myfloat(this.intv * x.flov);
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return new Myint(0);
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    public Value div(Value x) {
        if (x.type.equals(this.type)) {
            if (x.intv == 0) {
                return new Mystr("NaN");
            } else {
                return new Myint(this.intv / x.intv);
            }
        } else if (x.type.equals(Type.FLOAT)) {
            if (x.flov == 0) {
                return new Mystr("NaN");
            } else {
                return new Myfloat(this.intv / x.flov);
            }
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return new Mystr("NaN");
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    public int getValue() {
        return this.intv;
    }

    @Override
    public int compareTo(Value v) {
        int returning;
        if (this.type.equals(v.type)) {
            if (this.intv > v.intv) {
                returning = 1;
            } else if (this.intv == v.intv) {
                returning = 0;
            } else {
                returning = -1;
            }
        } else if (v.type.equals(Type.FLOAT)) {
            if (this.intv > v.flov) {
                returning = 1;
            } else if (this.intv == v.flov) {
                returning = 0;
            } else {
                returning = -1;
            }
        } else {
            throw new RuntimeException();
        }
        return returning;
    }

    @Override
    public String toString() {
        String str = "";
        return str + intv;
    }
}
