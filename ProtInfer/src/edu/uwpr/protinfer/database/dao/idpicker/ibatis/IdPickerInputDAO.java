package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import edu.uwpr.protinfer.database.dao.ibatis.ProteinferInputDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerInputSummary;

public class IdPickerInputDAO extends ProteinferInputDAO {

    private static final String sqlMapNameSpace = "IdPickerInput";
    
    public IdPickerInputDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public void saveIdPickerInputList(List<IdPickerInputSummary> inputList) {
        if(inputList.size() == 0)
            return;
        StringBuilder buf = new StringBuilder();
        for(IdPickerInputSummary input: inputList) {
            buf.append(",("+input.getId()+",");
            if(input.getNumTargetHits() == -1)
                buf.append("NULL,");
            else
                buf.append(input.getNumTargetHits()+",");
            if(input.getNumDecoyHits() == -1)
                buf.append("NULL,");
            else
                buf.append(input.getNumDecoyHits()+",");
            if(input.getNumFilteredTargetHits() == -1)
                buf.append("NULL)");
            else
               buf.append(input.getNumFilteredTargetHits()+")");
        }
        buf.deleteCharAt(0);
//        System.out.println(buf.toString());
        super.save(sqlMapNameSpace+".saveIdPickerInputList", buf.toString());
    }
}
