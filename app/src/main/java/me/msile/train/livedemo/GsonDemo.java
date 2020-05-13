package me.msile.train.livedemo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msile on 2017/11/21.
 */

public class GsonDemo {

    static String jsonStr="[{\"TableName\":\"Table\",\"DataColumn\":[\"City\",\"Company\",\"UserName\",\"Cellphone\",\"RegisterTime\",\"PKUser\"],\"DataRowValue\":[[\"常德\",\"陈 霞\",\"陈 霞\",\"18797786117\",\"2017-11-21 10:48:23\",\"2cad55c7-debb-4080-8057-136931de3604\"],[\"徐州\",\"刘凯@13656243452\",\"刘凯\",\"13656243452\",\"2017-11-21 10:44:42\",\"bcfaa135-3aa4-4679-aafc-9f90b4669e27\"],[\"三亚\",\"天崖\",\"王天\",\"15103091071\",\"2017-11-21 10:27:41\",\"a2c008f2-c494-4062-98fd-d1b55bd9dae4\"],[\"四平\",\"卢红军@13624449973\",\"卢红军\",\"13624449973\",\"2017-11-20 16:22:14\",\"8a53ed8e-3b91-42ea-a0b8-22ea07e37cf6\"]]},{\"TableName\":\"Table1\",\"DataColumn\":[\"City\",\"Company\",\"UserName\",\"Cellphone\",\"RegisterTime\",\"PKUser\"],\"DataRowValue\":[]}]";

    public static void main(String[] args) {

    }

    class Tables extends ArrayList<Table> {

    }

    class Table {
        public String TableName;
        public DataColumn DataColumn;
        public DataRowValue DataRowValue;
    }

    class DataColumn extends HashMap<String,String> {

    }

    class DataRowValue extends ArrayList<DataRowValueItem>{
    }

    class DataRowValueItem extends HashMap<String,String>{

    }

}
