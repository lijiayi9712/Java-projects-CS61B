package db;


/**
 * Created by LiJiayi on 2/27/17.
 */
public class Mystr extends Value<String> {

    public Mystr(String val, Column col) {
        type = Type.STRING;
        strv = val;
        this.col = col;
    }

    public Mystr(String val) {
        this.type = Type.STRING;
        strv = val;
    }
    public boolean equals(Value x) {
        if (this.strv.equals("NaN")) {
            return (x.strv.equals("NaN"));
        } else {
            if (x.type.equals(this.type)) {
                if (x.strv.equals(this.strv)) {
                    return true;
                }
            }
            return false;
        }
    }

    public String getStr() {
        return strv;
    }

    public Value subtract(Value x) {
        if (x.strv.equals("NaN")) {
            return new Mystr("NaN");
        } else if (this.strv.equals("NOVALUE")) {
            if (x.type == Type.INT) {
                return x.subtract(new Myint(0));
            } else if (x.type == Type.STRING) {
                return x.subtract(new Mystr(""));
            } else if (x.type == Type.FLOAT) {
                return x.subtract(new Myfloat(0.000f));
            }
        }
        throw new RuntimeException("String can only be subtracted to another String");
    }

    public Value add(Value str1) {
        String newly = null;
        if (this.strv.equals("NOVALUE")) {
            if (str1.type == Type.INT) {
                return str1.add(new Myint(0));
            } else if (str1.type == Type.STRING) {
                return str1.add(new Mystr(""));
            } else if (str1.type == Type.FLOAT) {
                return str1.add(new Myfloat(0.000f));
            }
        } else if (this.strv.equals("NaN")) {
            return new Mystr("NaN");
        } else if (str1.type == Type.STRING) {
            if (str1.strv.equals("NOVALUE")) {
                return this;
            } else {
                newly = this.strv.substring
                    (0, this.strv.length() - 1) + str1.strv.substring(1, str1.strv.length());
            }
        } else {
            throw new RuntimeException("String can only be added to another String");
        }
        return new Mystr(newly);
    }

    public Value multi(Value x) {
        if (x.strv.equals("NaN")) {
            return new Mystr("NaN");
        }
        if (this.strv.equals("NOVALUE")) {
            if (x.type == Type.INT) {
                return x.multi(new Myint(0));
            } else if (x.type == Type.STRING) {
                return x.multi(new Mystr(""));
            } else if (x.type == Type.FLOAT) {
                return x.multi(new Myfloat(0.000f));
            }
        }
        throw new RuntimeException("String can only be added to another String");
    }

    public Value div(Value x) {
        if (x.strv.equals("NaN")) {
            return new Mystr("NaN");
        } else if (this.strv.equals("NOVALUE")) {
            if (x.type == Type.INT) {
                return x.div(new Myint(0));
            } else if (x.type == Type.STRING) {
                return x.div(new Mystr(""));
            } else if (x.type == Type.FLOAT) {
                return x.div(new Myfloat(0.000f));
            }
        }
        throw new RuntimeException("String can only be added to another String");
    }

    public String getValue() {
        return this.strv;
    }

    public int compareTo(Value v) {
        if (this.getValue().equals("NaN")) {
            return 1;
        } else if (v.strv.equals("NaN")) {
            return -1;
        } else if (this.type.equals(v.type)) {
            boolean check1 = false;
            boolean check2 = false;
            for (int i = 0; i < strv.length(); i += 1) {
                char elim = this.strv.charAt(i);
                if (elim == '\'') {
                    check1 = true;
                }
            }
            for (int j = 0; j < v.strv.length(); j += 1) {
                char elim = v.strv.charAt(j);
                if (elim == '\'') {
                    check2 = true;
                }
            }
            String helper1 = this.strv;
            String helper2 = v.strv;
            if (check1) {
                helper1 = helper1.substring(1, strv.length() - 1);
            }
            if (check2) {
                helper2 = helper2.substring(1, v.strv.length() - 1);
            }
            int x = helper1.compareTo(helper2);
            return x;
        } else {
            throw new RuntimeException(
                    "String Type Value can't be compared with Other Value Types");
        }
    }
    @Override
    public String toString() {
        return this.strv;
    }
}
