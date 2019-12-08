package db;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;


public class TestTable {
    /*@Test
    public void Test_Compare() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(1, 2)) );
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(3, 4)) );
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(5, 6)) );
        Table tbl2 = new Table("tbl2", "x int,z int");
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(1, 21)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(7, 8)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(9, 6)) );
        ArrayList results = tbl1.CompareTable_cols(tbl2);
        ArrayList expected = new ArrayList();
        expected.add("x");
        Assert.assertEquals(expected,results);
        Table tbl3 = new Table("tbl3", "A int,x int, B int, y int");
        tbl3.insert( new ArrayList<Integer>(Arrays.asList(1, 2, 37, 4)) );
        tbl3.insert( new ArrayList<Integer>(Arrays.asList(3, 4, 1, 0)) );
        tbl3.insert( new ArrayList<Integer>(Arrays.asList(5, 6, 0, 78)) );
        ArrayList results2 = tbl1.CompareTable_cols(tbl3);
        ArrayList expected2 = new ArrayList();
        expected2.add("x");
        expected2.add("y");
        Assert.assertEquals(expected2,results2);
        Database db = new Database<>();
        db.CompareTable_rows(tbl1, tbl2, results);

    }*/

    public static Database DB = new Database();
    /*@Test
    public void Test_Join() {
        Database db = new Database();
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(2, 5)) );
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(8, 3)) );
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(13, 7)) );
        Table tbl2 = new Table("tbl2", "x int,z int");
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(2, 4)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(8, 9)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(10, 1)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(11, 1)) );
        Table tbl3 = db.joinTwo(tbl1, tbl2);
        tbl3.print();*/


    @Test
    public void Test_Print() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert(new ArrayList<Integer>(Arrays.asList(1, 2)));
        tbl1.insert(new ArrayList<Integer>(Arrays.asList(3, 4)));
        tbl1.insert(new ArrayList<Integer>(Arrays.asList(5, 6)));
        System.out.println(tbl1.rows);
        tbl1.print();
        Table tbl2 = new Table("tbl2", "x string,y string");
        tbl2.insert(new ArrayList<String>(Arrays.asList("a", "b")));
        tbl2.insert(new ArrayList<String>(Arrays.asList("cde", "fgh")));
        tbl2.insert(new ArrayList<String>(Arrays.asList("i j k l", "m n o p")));
        System.out.println(tbl2.rows);
        tbl2.print();
    }

    @Test
    public void Test_getCol() {
        Table tbl1 = new Table("tbl1", "x int,y int,z int");
//        tbl1.insert( new ArrayList<Integer>(Arrays.asList(1, 2, 7)) );
//        tbl1.insert( new ArrayList<Integer>(Arrays.asList(3, 4, 8)) );
//        tbl1.insert( new ArrayList<Integer>(Arrays.asList(5, 6, 9)) );
        System.out.println(tbl1.cols);
        System.out.println(tbl1.getColumn("x"));
        System.out.println(tbl1.getColumn("y"));
        System.out.println(tbl1.getColumn("z"));
    }

    @Test
    public void Testingshared() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(1, 2)) );
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(3, 4)) );
        tbl1.insert( new ArrayList<Integer>(Arrays.asList(5, 6)) );
        Table tbl2 = new Table("tbl2", "x int,y int");
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(1, 21)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(7, 8)) );
        tbl2.insert( new ArrayList<Integer>(Arrays.asList(9, 6)) );
        ArrayList actual = tbl1.getSharedCols(tbl2);
        ArrayList expected = new ArrayList();
        expected.add("x");
        expected.add("y");
        Assert.assertEquals(expected, actual);
    }

  @Test
    public void TestingsharedValue() {
        Table tbl1 = new Table("tbl1", "x int,y int");
          tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(2))) );
          tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(3), new Myint(4))) );
          tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(5), new Myint(6))) );
        Table tbl2 = new Table("tbl2", "x int,z int");
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(21))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(8))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(9), new Myint(9))) );
        Column[] c = new Column[]{tbl1.getColumn("x")};
        //TreeMap[] actual = DB.getSharedValues(tbl1, tbl2,new String[]{"x"});
        TreeMap t = new TreeMap();
        t.put(new Myint(1), 2);
        TreeMap[] expected = new TreeMap[]{t};
        //Assert.assertArrayEquals(expected,actual);
    }

    @Test
    public void TestingMergingColumn() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(2))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(3), new Myint(4))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(5), new Myint(6))) );
        Table tbl2 = new Table("tbl2", "A int,B int");
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(21))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(8))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(9), new Myint(9))) );
        String result = DB.getTableCol(tbl1, tbl2);
        System.out.print(result);
    }


    @Test
    public void Testingbuildcol() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(2))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(3), new Myint(4))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(5), new Myint(6))) );
        ArrayList actual = tbl1.getColumnVal("x");
        ArrayList expected = new ArrayList();
        expected.add(new Myint(1));
        expected.add(new Myint(3));
        expected.add(new Myint(5));
        tbl1.print();
    }

    @Test
    public void TestProduct() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(2))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(3), new Myint(4))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(5), new Myint(6))) );
        Table tbl2 = new Table("tbl2", "A int,B int");
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(21))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(8))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(9), new Myint(9))) );
        Table c = DB.product(tbl1, tbl2);
        c.print();
    }

    @Test
    public void TestJoinTwo() {
        Table tbl1 = new Table("tbl1", "x int,y int");
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(4))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(2), new Myint(5))) );
        tbl1.insert( new ArrayList<Value>(Arrays.asList(new Myint(3), new Myint(6))) );
        Table tbl2 = new Table("tbl2", "x int,z int");
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(7))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(7))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(9))) );
        tbl2.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(11))) );
        Table t3 = DB.joinTwo(tbl1, tbl2);
        t3.print();
//        Table tbl3 = new Table("tbl3", "x int,y int, z int, w int");
//        tbl3.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(7), new Myint(2), new Myint(10))));
//        tbl3.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(7), new Myint(4), new Myint(1))));
//        tbl3.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(9), new Myint(9), new Myint(1))));
//        Table tbl4 = new Table("tbl4", "w int, b int, z int");
//        tbl4.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(7), new Myint(4))));
//        tbl4.insert( new ArrayList<Value>(Arrays.asList(new Myint(7), new Myint(7), new Myint(3))));
//        tbl4.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(9), new Myint(6))));
//        tbl4.insert( new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(11), new Myint(9))));
//        Table t5 = DB.joinTwo(tbl3, tbl4);
//        t5.print();
    }

    @Test
    public void TestValues() {
        Value x = new Myint(1);
        Value x1 = new Myint(1);
        Value y = new Myint(3);
        Value z = new Mystr("x");
        Value w = new Myfloat(1.2f);
        Assert.assertTrue(x.compareTo(x1) == 0);
        Assert.assertTrue(y.compareTo(x1) > 0);
        Assert.assertTrue(w.compareTo(x) > 0);
    }

    @Test
    public void Testinserting() {
        /*Table tbl1 = new Table("tbl1", "x int,y int");
        ArrayList<ArrayList<Value>> columns = new ArrayList<ArrayList<Value>>();
        columns.add(new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(4))));
        columns.add(new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(4))));
        columns.add(new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(4))));
        columns[0] = new ArrayList<Value>(Arrays.asList(new Myint(1), new Myint(4)));
        columns[1] = new ArrayList<Value>(Arrays.asList(new Myint(2), new Myint(5)));
        columns[2] = new ArrayList<Value>(Arrays.asList(new Myint(3), new Myint(6)));
        tbl1.insertcols(columns);
        tbl1.print();*/
    }


    @Test
    public void Testload() {
        Database DB = new Database();
        //DB.transact("load examples/records");
//        DB.transact("load examples/fans");
  //      System.out.print(DB.transact("select Lastname,Firstname from examples/fans where Firstname < Lastname"));
        //System.out.print(DB.transact("select TeamName,Season,Wins,Losses from examples/records where Wins >= Losses"));
        //System.out.print(DB.transact("select Lastname,Firstname from examples/fans where Firstname < Lastname"));
        //System.out.print(DB.transact("create table t3 (astring)"));
        //DB.transact("create table t (x int, y int)"); // Additional test for NOVALUE? (should insert for all types)
        //System.out.print(DB.transact("insert into t values 2, 5,"));
//        DB.transact("insert into t values 3, 6");
//        DB.transact("insert into t values 4, NOVALUE");
        //System.out.print(DB.transact("select x+y as z from t"));

//        DB.transact("create table t1 (x string, y int)"); // Additional test for NOVALUE? (should insert for all types)
//        DB.transact("insert into t1 values 'love',5");
//        DB.transact("insert into t1 values 'u', 6");
//        DB.transact("insert into t1 values 'jim', NOVALUE");

//        DB.transact("load t2");
//        DB.transact("select x + y as z from t");
//        DB.transact("create table T3 (Name string, Ted string, Z string)");
//        DB.transact("insert into T3 values 'Lee', 'Maurice', NOVALUE");
//        DB.transact("insert into T3 values 'Lee', 'Maurice', 'Steelers'");
//        DB.transact("insert into T3 values 'Ray', 'Mitas', NOVALUE");   //check whether
//
//        DB.transact("create table records (TeamName string,Season int,Wins int,Losses int,Ties int, Sport string)");
//        DB.transact("insert into records values 'Golden Bears',2015,8,5,0, 'bd'");
//        DB.transact("insert into records values 'Golden Bears',2014,5,7,0,'bk'");
//        DB.transact("insert into records values 'Steelers',2015,10,6,0,'soccer'");
//        DB.transact("insert into records values 'Steelers',2014,11,5,0, 'soccer'");
//        DB.transact("insert into records values 'Steelers',2013,8,8,0, 'db'");
//        DB.transact("insert into records values 'Mets',2015,90,72,0, 's'");
        //System.out.print(DB.transact("select TeamName,Sport,Season,Wins,Losses from records where Wins > Losses"));
//        DB.transact("load examples/teams");
//        DB.transact("load examples/records");
//
//        DB.transact("create table Teamrecords as select * from examples/teams, examples/records");
        //System.out.print(DB.transact("select TeamName,Sport,Wins,Losses from Teamrecords where TeamName > Sport and Wins > Losses"));

        DB.transact("create table t0 (x int,y float,z float)");
        DB.transact("insert into t0 values 1,2.000,0.000");
        DB.transact("insert into t0 values 7.300,0.000,5");
        DB.transact("create table n as select x/y as a, y/z as b from t0");
        //DB.transact("select a+b as c from n");
        System.out.print(DB.transact("select b-a as c from n"));
//        System.out.print(DB.transact("select select x + y as a, y*z as b from t0"));
//        DB.transact("create table T2 (a string, b string)");
//        DB.transact("store T2");
        //System.out.print(DB.transact("select x-y as z, Name+Ted as Q from t, T3"));
        //System.out.print(DB.transact("load x int, y int"));
        //System.out.print(DB.transact("print T1"));
        //System.out.print(DB.transact("load examples/fans"));
        //System.out.print(DB.transact("print T1"));
        //System.out.print(DB.transact("select * from T1 where x < smith"));
        //System.out.print(DB.transact("load loadMalformed0"));
        //System.out.print(DB.transact("load loadMalformed"));
    }





    public static void main(String[] args) {
        /*Testingshared();
        TestingsharedValue();
        Testingbuildcol();
        Test_getCol();
        TestProduct();
        TestValues();*/
    }
}