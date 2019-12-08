package db;

public class Myfloat extends Value {

    public Myfloat(float val, Column col) {
        type = Type.FLOAT;
        flov = val;
        this.col = col;
    }

    public Myfloat(float val) {
        flov = (float) val;
        this.type = Type.FLOAT;
    }

    public boolean equals(Value x) {
        if (x.type.equals(this.type)) {
            if (x.flov == this.flov) {
                return true;
            }
        }
        return false;
    }

    public float getflov() {
        return flov;
    }

    public Value add(Value x) {
        if (x.type.equals(this.type)) {
            return new Myfloat(this.flov + x.flov);
        } else if (x.type.equals(Type.INT)) {
            return new Myfloat(this.flov + x.intv);
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return this;
            } else if (x.strv.equals("NaN")) {
                return x;
        } else {
                throw new RuntimeException(
                        "Integer Type Value can't be added to String Type Value");
            }
        } else {
            throw new RuntimeException(
                    "Float Type Value can't be added to String Type Value");
        }
    }

    public Value subtract(Value x) {
        if (x.type.equals(this.type)) {
            return new Myfloat(this.flov - x.flov);
        } else if (x.type.equals(Type.INT)) {
            return new Myfloat(this.flov - x.intv);
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return this;
            } else if (x.strv.equals("NaN")) {
                return x;
            } else {
                throw new RuntimeException(
                        "Integer Type Value can't be added to String Type Value");
            }
        } else {
            throw new RuntimeException(
                    "Float Type Value can't be subtracted to String Type Value");
        }
    }

    public Value multi(Value x) {
        if (x.type.equals(this.type)) {
            return new Myfloat(this.flov * x.flov);
        } else if (x.type.equals(Type.INT)) {
            return new Myfloat(this.flov * x.intv);
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return new Myfloat(0.0f);
            } else if (x.strv.equals("NaN")) {
                return x;
            } else {
                throw new RuntimeException(
                        "Integer Type Value can't be added to String Type Value");
            }
        } else {
            throw new RuntimeException(
                    "Float Type Value can't be multiplied to String Type Value");
        }
    }

    public Value div(Value x) {
        if (x.type.equals(this.type)) {
            if (x.flov == 0.0f) {
                return new Mystr("NaN");
                /*NEED TO ASSIGN Columns that it belongs to and VALUES NAN*/
            } else {
                return new Myfloat(this.flov / x.flov);
            }
        } else if (x.type.equals(Type.INT)) {
            if (x.intv == 0) {
                return new Mystr("NaN");
            } else {
                return new Myfloat(this.flov / x.intv);
            }
        } else if (x.type.equals(Type.STRING)) {
            if (x.strv.equals("NOVALUE")) {
                return new Mystr("NaN");
            } else if (x.strv.equals("NaN")) {
                return x;
            } else {
                throw new RuntimeException(
                        "Integer Type Value can't be added to String Type Value");
            }
        } else {
            throw new RuntimeException(
                    "Float Type Value can't be divided by String Type Value");
        }
    }

    public float getValue() {
        return this.flov;
    }

    public int compareTo(Value v) {
        int returning;
        if (this.type.equals(v.type)) {
            if (this.flov > v.flov) {
                returning = 1;
            } else if (this.flov == v.flov) {
                returning = 0;
            } else {
                returning = -1;
            }
        } else if (v.type.equals(Type.INT)) {
            if (this.flov > v.intv) {
                returning = 1;
            } else if (this.flov == v.intv) {
                returning = 0;
            } else {
                returning = -1;
            }
        } else {
            throw new RuntimeException("Float Type Value can't be compared with String Type Value");
        }
        return returning;
    }

    @Override
    public String toString() {
        String str = "";
        return str + String.format("%.3f", flov);
    }
}
